package gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.Filemanager;
import logic.Logic;
import wiiboard.Wiiboard;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DashboardController {
    private Logic logic;
    private Wiiboard wiiboard;

    private Stage stage;

    @FXML
    private Button connectButton;

    @FXML
    private Button exportButton;

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

    @FXML
    private Button buttonCOPPlot;

    @FXML
    private Button buttonXYSplit;

    @FXML
    private TextField personText;

    @FXML
    private ComboBox selectTest;

    private XYChart.Series seriesPlotting = new XYChart.Series<Double, Double>();
    private XYChart.Series seriesRecordingX = new XYChart.Series<Double, Double>();
    private XYChart.Series seriesRecordingY = new XYChart.Series<Double, Double>();
    private double tp = 0.0;
    private double area = 0.0;
    private double curvelength = 0.0;
    private double curveX = 0.0;
    private double curveY = 0.0;
    private int duration = 0;

    public DashboardController(Logic logic, Wiiboard wiiboard) {
        this.logic = logic;
        this.wiiboard = wiiboard;
    }

    public void setConnectionInfo(String info) {
        wiiStats.setText(info);
    }

    public void setup(Stage stage) {
        setStage(stage);
        connectButton.setOnMouseClicked(e -> wiiboard.startWiiboardDiscoverer());
        startButton.setOnMouseClicked(e -> startRecording());
        buttonCOPPlot.setOnMouseClicked(e -> {
            COP.setVisible(true);
            recordingPane.setVisible(false);
        });
        buttonXYSplit.setOnMouseClicked(e -> {
            COP.setVisible(false);
            recordingPane.setVisible(true);
        });

        exportButton.setOnMouseClicked(e -> exportData());


        copChart.getData().add(seriesPlotting);

        recordingXChart.getData().add(seriesRecordingX);
        recordingYChart.getData().add(seriesRecordingY);
    }

    private void exportData() {
        String id = personText.getText();
        String type = (String) selectTest.getValue();

        if (id.equals("")) {
            showDialog("Please provide a person id");
        } else if (type == null) {
            showDialog("Please provide a test type");
        } else {
            File file = Filemanager.findDir(stage);
            try {
                Filemanager.writeToCSV(type, id, duration, tp, curveX, curveY, curvelength, area, logic.getCopList(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            showDialog("Data exported sucessfully");
        }
    }

    private void showDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wiibalance");
        alert.setContentText(message);
        alert.showAndWait();
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

    private void startRecording() {
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

        recXXAxis.setAutoRanging(false);
        recYXAxis.setAutoRanging(false);

        recXXAxis.setLowerBound(0.0);
        recXXAxis.setUpperBound(time);
        recYXAxis.setLowerBound(0.0);
        recYXAxis.setUpperBound(time);

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
        tp = logic.findTP();
        area = logic.calculateCurveArea();
        curvelength = logic.calculateCurveLength();
        curveX = logic.calcCurveLengthX();
        curveY = logic.calcCurveLengthY();

        xCurvelength.setText(String.format("%.2f", curveX));
        yCurvelength.setText(String.format("%.2f", curveY));
        curve.setText(String.format("%.2f", curvelength));
        areal.setText(String.format("%.2f", area));
        turning.setText(String.format("%.2f", tp));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
