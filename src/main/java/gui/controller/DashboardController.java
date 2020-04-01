package gui.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.Logic;
import wiiboard.Wiiboard;

import java.util.List;

public class DashboardController {
    private Logic logic;
    private Wiiboard wiiboard;

    private boolean plotting = true;
    private boolean recording = false;

    @FXML
    private Button connectButton;

    @FXML
    private TextField wiiStats;

    @FXML
    private LineChart copChart;

    @FXML
    private TextField durationInput;

    @FXML
    private Button startButton;

    @FXML
    private TextArea curve;

    @FXML
    private TextArea areal;

    @FXML
    private TextArea turning;

    private XYChart.Series series = new XYChart.Series<Double, Double>();


    public DashboardController(Logic logic, Wiiboard wiiboard) {
        this.logic = logic;
        this.wiiboard = wiiboard;
    }


    public void setConnectionInfo(String info) {
        wiiStats.setText(info);
    }

    public void setup() {
        connectButton.setOnMouseClicked(e -> wiiboard.startWiiboardDiscoverer());
        startButton.setOnMouseClicked(e -> startRecording());

        copChart.getData().add(series);
        startPlotting();
    }

    private void startPlotting() {
        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                while (plotting) {
                    List<Double> point = wiiboard.getCopPoint();
                    plotPoint(point.get(0), point.get(1));
                    Thread.sleep(50);
                }
                return null;
            }
        };
        new Thread(task).start();
    }


    private void plotPoint(double xVal, double yVal) {
        Platform.runLater(() -> {
            series.getData().clear();
            series.getData().add(new XYChart.Data<>(xVal, yVal));
        });
    }

    private void recordPoint(double x, double y) {
        Platform.runLater(() -> {
            series.getData().add(new XYChart.Data<>(x, y));
        });
    }

    public void startRecording() {
        plotting = false;
        recording = true;

        int duration = Integer.parseInt(durationInput.getText());
        wiiboard.startRecordingData(duration);

        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                while (recording) {
                    List<Double> point = wiiboard.getCopPoint();
                    recordPoint(point.get(0), point.get(1));
                    Thread.sleep(50);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void stopRecording() {
        recording = false;

        double tp = logic.findTP();
        double area = logic.calculateCurveArea();
        double curvelength = logic.calculateCurveLength();
        double curveX = logic.calcCurveLengthX();
        double curveY = logic.calcCurveLengthY();

        curve.setText(String.format("%.2f", curvelength));
        areal.setText(String.format("%.2f", area));
        turning.setText(String.format("%.2f", tp));

    }
}
