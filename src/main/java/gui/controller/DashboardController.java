package gui.controller;

import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

import java.util.List;
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

        seriesPlotting.setName("COP");
        copChart.getData().add(seriesPlotting);


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
            seriesPlotting.getData().clear();
            seriesPlotting.getData().add(new XYChart.Data<>(xVal, yVal));
        });
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
        }
    }

    private void changeViewToRecording(int time) {
        COP.setVisible(false);
        recordingPane.setVisible(true);
        recXXAxis.setLowerBound(0.0);
        recXXAxis.setUpperBound(time);
        recYXAxis.setLowerBound(0.0);
        recYXAxis.setUpperBound(time);

        startTimer(time);
    }

    private void startTimer(int time) {
        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger remaining = new AtomicInteger(time + 1);

        // put dummy data onto graph per second
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

        curve.setText(String.format("%.2f", curvelength));
        areal.setText(String.format("%.2f", area));
        turning.setText(String.format("%.2f", tp));

    }
}
