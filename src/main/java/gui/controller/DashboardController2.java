package gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logic.Filemanager;
import logic.Logic;
import wiiboard.Wiiboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DashboardController2 {
    private Logic logic;
    private Wiiboard wiiboard;

    private Stage stage;

    @FXML
    private Button connectButton;
    @FXML
    private TextField wiiStats;

    @FXML
    private Button exportButton;

    @FXML
    private Button buttonTPPlots;
    @FXML
    private Button buttonCOPPlot;
    @FXML
    private Button buttonXYSplit;

    @FXML
    private LineChart copChart;

    @FXML
    private LineChart tpResultPlot;
    @FXML
    private LineChart msdPlot;
    @FXML
    private LineChart slopePlot;
    @FXML
    private LineChart timeseriesPlot;

    @FXML
    private LineChart recordingYChart;
    @FXML
    private LineChart recordingXChart;

    @FXML
    private NumberAxis recYXAxis;
    @FXML
    private NumberAxis recXXAxis;

    @FXML
    private TextField personText;
    @FXML
    private ComboBox selectTest;
    @FXML
    private TextField durationInput;
    @FXML
    private Button startButton;

    @FXML
    private TextArea curve;
    @FXML
    private TextArea areal;
    @FXML
    private TextArea tpResult;
    @FXML
    private TextArea xCurvelength;
    @FXML
    private TextArea yCurvelength;

    @FXML
    private AnchorPane COPPane;
    @FXML
    private GridPane TPPane;
    @FXML
    private GridPane XYSplitPane;

    @FXML
    private TextArea remainingTime;


    private XYChart.Series seriesPlotting = new XYChart.Series<Double, Double>();
    private XYChart.Series seriesRecording = new XYChart.Series<Double, Double>();
    private XYChart.Series seriesRecordingX = new XYChart.Series<Double, Double>();
    private XYChart.Series seriesRecordingY = new XYChart.Series<Double, Double>();
    private XYChart.Series TPseries = new XYChart.Series<Double, Double>();
    private XYChart.Series msdSeries = new XYChart.Series<Double, Double>();
    private XYChart.Series timeseries = new XYChart.Series<Double, Double>();
    private XYChart.Series slopeSeries = new XYChart.Series<Double, Double>();
    private double tp = 0.0;
    private double area = 0.0;
    private double curvelength = 0.0;
    private double curveX = 0.0;
    private double curveY = 0.0;
    private int duration = 0;

    public DashboardController2(Logic logic, Wiiboard wiiboard) {
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
            COPPane.setVisible(true);
            XYSplitPane.setVisible(false);
            TPPane.setVisible(false);
        });
        buttonXYSplit.setOnMouseClicked(e -> {
            COPPane.setVisible(false);
            XYSplitPane.setVisible(true);
            TPPane.setVisible(false);
        });
        buttonTPPlots.setOnMouseClicked(e -> {
            COPPane.setVisible(false);
            XYSplitPane.setVisible(false);
            TPPane.setVisible(true);
        });


        exportButton.setOnMouseClicked(e -> exportData());


        seriesRecording.setName("Test result");
        seriesPlotting.setName("Current COP");

        copChart.getData().add(seriesPlotting);
        copChart.getData().add(seriesRecording);
        copChart.setCreateSymbols(false);

        recordingXChart.getData().add(seriesRecordingX);
        recordingYChart.getData().add(seriesRecordingY);

        msdPlot.getData().add(msdSeries);
        slopePlot.getData().add(slopeSeries);
        timeseriesPlot.getData().add(timeseries);

        tpResultPlot.getData().add(TPseries);
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
            resetResultPlots();
            wiiboard.startRecordingData(duration);
            startTimer(duration);
        }
    }

    private void resetResultPlots() {
        xCurvelength.setText("");
        yCurvelength.setText("");
        curve.setText("");
        areal.setText("");
        tpResult.setText("");
        seriesRecording.getData().clear();
        seriesRecordingX.getData().clear();
        seriesRecordingY.getData().clear();
        timeseries.getData().clear();
        TPseries.getData().clear();
        msdSeries.getData().clear();
        slopeSeries.getData().clear();
    }

    private void changeViewToRecording(int time) {
        COPPane.setVisible(false);
        XYSplitPane.setVisible(true);
        TPPane.setVisible(false);

        recXXAxis.setAutoRanging(false);
        recYXAxis.setAutoRanging(false);

        recXXAxis.setLowerBound(0.0);
        recXXAxis.setUpperBound(time);
        recYXAxis.setLowerBound(0.0);
        recYXAxis.setUpperBound(time);
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

    public void stopRecording() throws FileNotFoundException {

        tp = logic.findTP();
        plotTPCurve();
        area = logic.calculateCurveArea();
        curvelength = logic.calculateCurveLength();
        curveX = logic.calcCurveLengthX();
        curveY = logic.calcCurveLengthY();

        xCurvelength.setText(String.format("%.2f", curveX));
        yCurvelength.setText(String.format("%.2f", curveY));
        curve.setText(String.format("%.2f", curvelength));
        areal.setText(String.format("%.2f", area));
        tpResult.setText(String.format("%.2f", tp));
    }

    private void plotTPCurve() {
        List<List<Double>> tpCurve = logic.getTpCurve();
        List<List<Double>> msdCurve = logic.getMsdCurve();
        List<List<Double>> timeseriescurve = logic.getTimeSeries();


        Platform.runLater(() -> {
            tpCurve.forEach(a -> {
                double x = a.get(0);
                double y = a.get(1);
                XYChart.Data point = new XYChart.Data<>(x, y);
                TPseries.getData().add(point);
            });

            timeseriescurve.forEach(a -> {
                double x = a.get(0);
                double y = a.get(1);
                XYChart.Data point = new XYChart.Data<>(x, y);
                timeseries.getData().add(point);
            });
            msdCurve.forEach(a -> {
                double x = a.get(0);
                double y = a.get(1);
                XYChart.Data point = new XYChart.Data<>(x, y);
                msdSeries.getData().add(point);
            });
            msdCurve.forEach(a -> {
                double x = a.get(0);
                double y = a.get(1);
                XYChart.Data point = new XYChart.Data<>(x, y);
                slopeSeries.getData().add(point);
            });
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void plotCopRec(double xVal, double yVal) {
        Platform.runLater(() -> seriesRecording.getData().add(new XYChart.Data<>(xVal, yVal)));
    }
}
