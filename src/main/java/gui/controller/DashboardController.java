package gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import logic.Logic;
import wiiboard.Wiiboard;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DashboardController {
    private Logic logic;
    private Wiiboard wiiboard;

    private boolean plotting = true;

    @FXML
    private Button connectButton;

    @FXML
    private TextField wiiStats;

    @FXML
    private LineChart copChart;

    @FXML
    private LineChart recordingYChart;

    @FXML
    private LineChart recordingXChart;

    @FXML
    private NumberAxis recYXAxis;

    @FXML
    private NumberAxis recXXAxis;

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

    @FXML
    private TextField xCurvelength;

    @FXML
    private TextField yCurvelength;

    @FXML
    private AnchorPane COP;

    @FXML
    private AnchorPane recordingPane;

    @FXML
    private TextField remainingTime;

    private XYChart.Series seriesPlotting = new XYChart.Series<Double, Double>();

    private XYChart.Series seriesRecordingX = new XYChart.Series<Double, Double>();
    private XYChart.Series seriesRecordingY = new XYChart.Series<Double, Double>();


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

        copChart.getData().add(seriesPlotting);

        recordingXChart.getData().add(seriesRecordingX);
        recordingYChart.getData().add(seriesRecordingY);
    }

    public void plotPoint(double xVal, double yVal) {
        Platform.runLater(() -> {
            seriesPlotting.getData().clear();
            seriesPlotting.getData().add(new XYChart.Data<>(xVal, yVal));
        });
    }

    public void plotXRec(double xVal, double time) {
        Platform.runLater(() -> seriesRecordingX.getData().add(new XYChart.Data<>(time, xVal)));
    }

    public void plotYRec(double yVal, double time) {
        Platform.runLater(() -> seriesRecordingY.getData().add(new XYChart.Data<>(time, yVal)));
    }

    public void startRecording() {
        int duration = 0;
        try {
            duration = Integer.parseInt(durationInput.getText());
        } catch (NumberFormatException e) {
            //TODO - alert user to write a number
            return;
        }

        if (duration > 0) {
            changeViewToRecording(duration);
            wiiboard.startRecordingData(duration);
            startTimer(duration);
        }
    }

    private void changeViewToRecording(int time) {
        COP.setVisible(false);
        recordingPane.setVisible(true);

        recXXAxis.setLowerBound(0.0);
        recXXAxis.setUpperBound((double) time);
        recYXAxis.setLowerBound(0.0);
        recYXAxis.setUpperBound((double) time);

        seriesRecordingX.getData().clear();
        seriesRecordingY.getData().clear();
    }

    private void startTimer(int time) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger remaining = new AtomicInteger(time + 1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int t = remaining.getAndDecrement();
            if (t <= 1) scheduledExecutorService.shutdown();

            Platform.runLater(() -> remainingTime.setText(String.format("%d", remaining.get())));
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopRecording() {
        double tp = logic.findTP();
        double area = logic.calculateCurveArea();
        double curvelength = logic.calculateCurveLength();
        double curveX = logic.calcCurveLengthX();
        double curveY = logic.calcCurveLengthY();

        xCurvelength.setText(String.format("%.2f", curveX));
        yCurvelength.setText(String.format("%.2f", curveY));
        curve.setText(String.format("%.2f", curvelength));
        areal.setText(String.format("%.2f", area));
        turning.setText(String.format("%.2f", tp));
    }
}
