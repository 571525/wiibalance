package com.balance.gui.controller;

import com.balance.gui.uiUtils.LogarithmicAxis;
import com.balance.logic.Filemanager;
import com.balance.logic.LogicInterface;
import com.balance.wiiboard.WiiboardInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gillius.jfxutils.chart.JFXChartUtil;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DashboardController {
    private LogicInterface logic;
    private WiiboardInterface wiiboard;
    private Stage stage; // Needed when exporting data

    //Menu
    @FXML
    private MenuItem menuAbout;
    @FXML
    private MenuItem menuHelp;
    @FXML
    private MenuItem menuFileExport;
    @FXML
    private MenuItem menuFileExportAll;
    @FXML
    private MenuItem menuFileClose;

    //Connect area
    @FXML
    private Button connectButton;
    @FXML
    private TextField wiiStats;

    //Menu to switch between views
    @FXML
    private Button buttonTPPlots;
    @FXML
    private Button buttonCOPPlot;
    @FXML
    private Button buttonXYSplit;

    //COP plot
    @FXML
    private AnchorPane COPPane;
    @FXML
    private LineChart copChart;
    @FXML
    private Button resetCop;

    //TP result area
    @FXML
    private GridPane TPPane;
    @FXML
    private LineChart tpResultPlot;
    @FXML
    private LineChart msdPlot;
    @FXML
    private LineChart slopePlot;
    @FXML
    private LineChart timeseriesPlot;
    @FXML
    private Button slopeResetZoom;
    @FXML
    private Button tpResetZoom;
    @FXML
    private Button resetXi;
    @FXML
    private Button resetMsd;

    //X-Y Split screen
    @FXML
    private GridPane XYSplitPane;
    @FXML
    private LineChart recordingYChart;
    @FXML
    private LineChart recordingXChart;
    @FXML
    private NumberAxis recYXAxis;
    @FXML
    private NumberAxis recXXAxis;
    @FXML
    private Button resetXSplit;
    @FXML
    private Button resetYSplit;

    // Area named "DASHBOARD"
    @FXML
    private TextField personText;
    @FXML
    private ComboBox selectTest;
    @FXML
    private TextField durationInput;
    @FXML
    private TextField repeatTestInput;
    @FXML
    private Button startButton;
    @FXML
    private TextField remainingTime;
    @FXML
    private TextField remainingTest;

    //Result area
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

    //Save buttons
    @FXML
    private Button exportButton;
    @FXML
    private Button exportAllButton;

    //refrences to the area with buttons to switch between test results
    @FXML
    private VBox testSwitchBox;
    @FXML
    private ScrollPane testScroll;

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
    private int testRepeats = 0;

    private List<HashMap<String, Object>> testResults;

    public DashboardController(LogicInterface logic, WiiboardInterface wiiboard) {
        this.logic = logic;
        this.wiiboard = wiiboard;
    }

    public void setConnectionInfo(String info) {
        wiiStats.setText(info);
    }

    /**
     * Called just after controller is attached to fxml document in order to set up fxml references
     *
     * @param stage
     */
    public void setup(Stage stage) {
        setStage(stage);
        setupViewMenu();
        setupActions();
        setupCharts();
        setupMenu();
    }

    private void setupViewMenu() {
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
    }

    private void setupActions() {
        connectButton.setOnMouseClicked(e -> wiiboard.startWiiboardDiscoverer());
        startButton.setOnMouseClicked(e -> {
            try {
                testRepeats = Integer.parseInt(repeatTestInput.getText());
            } catch (NumberFormatException er) {
            }
            remainingTest.setText("" + testRepeats);
            testResults = new ArrayList<>();
            testSwitchBox.getChildren().clear();
            startRecording();
        });
        selectTest.getSelectionModel().selectedItemProperty().addListener((options, oldvalue, newvalue) -> {
            if (newvalue.equals("Romberg")) {
                repeatTestInput.setText("4");
                durationInput.setText("20");
            } else {
                repeatTestInput.setText("1");
            }
        });

        slopeResetZoom.setOnMouseClicked(e -> resetSlopeZoom());
        tpResetZoom.setOnMouseClicked(e -> resetTpZoom());
        resetCop.setOnMouseClicked(e -> resetCopZoom());
        resetMsd.setOnMouseClicked(e -> resetMsdZoom());
        resetXi.setOnMouseClicked(e -> resetXiZoom());
        resetXSplit.setOnMouseClicked(e -> resetXSplitZoom());
        resetYSplit.setOnMouseClicked(e -> resetYSplitZoom());
        exportButton.setOnMouseClicked(e -> exportData());
        exportAllButton.setOnMouseClicked(e -> exportAllData());
    }

    private void setupMenu() {
        menuHelp.setOnAction(e -> Alerter.showDialog("Not implemented yet"));
        menuHelp.setOnAction(e -> Alerter.showDialog("Not implemented yet"));
        menuFileClose.setOnAction(e -> Platform.exit());
        menuFileExport.setOnAction(e -> exportData());
        menuFileExportAll.setOnAction(e -> exportAllData());
    }

    private void setupCharts() {
        seriesRecording.setName("Test result");
        seriesPlotting.setName("Current COP");
        copChart.getData().add(seriesPlotting);
        copChart.getData().add(seriesRecording);
       // copChart.setCreateSymbols(false);
        recordingXChart.getData().add(seriesRecordingX);
        recordingYChart.getData().add(seriesRecordingY);
        msdPlot.getData().add(msdSeries);
        slopePlot.getData().add(slopeSeries);
        timeseriesPlot.getData().add(timeseries);
        tpResultPlot.getData().add(TPseries);
        Platform.runLater(() -> {
            JFXChartUtil.setupZooming(copChart);
            JFXChartUtil.setupZooming(timeseriesPlot);
            JFXChartUtil.setupZooming(slopePlot);
            JFXChartUtil.setupZooming(msdPlot);
            JFXChartUtil.setupZooming(tpResultPlot);
            JFXChartUtil.setupZooming(recordingYChart);
            JFXChartUtil.setupZooming(recordingXChart);
        });
    }

    /**
     * Exports the currently selected data to a CSV file
     */
    private void exportData() {
        String id = personText.getText();
        String type = (String) selectTest.getValue();

        if (id.equals("")) {
            Alerter.showDialog("Please provide a person id");
        } else if (type == null) {
            Alerter.showDialog("Please provide a test type");
        } else {
            File file = Filemanager.findDir(stage);
            try {
                Filemanager.writeToCsv(type, id, duration, tp, curveX, curveY, curvelength, area, logic.getCopList(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Alerter.showDialog("Data exported sucessfully");
        }
    }

    /**
     * Exports all data stored in testResults to one CSV file
     */
    private void exportAllData() {
        String id = personText.getText();
        String type = (String) selectTest.getValue();

        if (id.equals("")) {
            Alerter.showDialog("Please provide a person id");
        } else if (type == null) {
            Alerter.showDialog("Please provide a test type");
        } else {
            File file = Filemanager.findDir(stage);
            try {
                Filemanager.writeAllToCsv(testResults, file, id, type, duration);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Alerter.showDialog("Data exported sucessfully");
        }
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
            testRepeats = Integer.parseInt(remainingTest.getText());
        } catch (NumberFormatException e) {
            Alerter.showDialog("Duration must be a valid integer number. \n - no decimals allowed");
            return;
        }
        if (duration > 0 && testRepeats > 0) {
            resetAllAxis();
            changeViewToRecording(duration);
            resetResultPlots();
            wiiboard.startRecordingData(duration);
            startTimer(duration, "#273043");
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

    private void startTimer(int time, String color) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger remaining = new AtomicInteger(time + 1);
        remainingTime.setStyle("-fx-text-fill: " + color + ";-fx-background-color: C6F1E7;");
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int t = remaining.getAndDecrement();
            if (t <= 1) scheduledExecutorService.shutdown();
            Platform.runLater(() -> remainingTime.setText(String.format("%d", remaining.get())));
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopRecording() {
        plotResults();

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("tp", tp);
        map.put("area", area);
        map.put("curvelength", curvelength);
        map.put("curveX", curveX);
        map.put("curveY", curveY);
        List<List<Double>> list = new ArrayList<>();
        logic.getCopList().forEach(a -> list.add(a));
        map.put("cop", list);
        testResults.add(map);

        try {
            testRepeats = Integer.parseInt(remainingTest.getText());
            testRepeats--;
            remainingTest.setText("" + testRepeats);
            if (testRepeats > 0) {
                addResultButton(testRepeats, map);
                startTimer(5, "red");
                Thread.sleep(5000);
                startRecording();
            } else if (testRepeats == 0) {
                addResultButton(testRepeats, map);
            }
        } catch (NumberFormatException e) {
        } catch (InterruptedException e) {
        }
    }

    private void plotResults() {
        tp = logic.findTP();
        plotTPCurve();
        area = logic.calculateCurveArea();
        curvelength = logic.calculateCurveLength();
        curveX = logic.calcCurveLengthX();
        curveY = logic.calcCurveLengthY();
        xCurvelength.setText(String.format("%.2f \n (mm)", curveX));
        yCurvelength.setText(String.format("%.2f \n (mm)", curveY));
        curve.setText(String.format("%.2f \n (mm)", curvelength));
        areal.setText(String.format("%.2f \n (mm^2)", area));
        tpResult.setText(String.format("%.2f \n (sec)", tp));
    }

    private void addResultButton(int testRepeats, HashMap<String, Object> map) {
        int tTest = -1;
        try {
            tTest = Integer.parseInt(repeatTestInput.getText());
        } catch (NumberFormatException e) {
        }

        if (tTest >= 0) {
            Button button = new Button();
            int nr = tTest - testRepeats;
            button.setText("Test " + nr);
            button.setStyle("-fx-background-color: #11CBD7; -fx-text-fill: #273043;-fx-pref-width: 100;");
            button.setId("testSwitch");
            button.setOnMouseClicked(e -> {
                logic.setCopList((List) map.get("cop"));
                updateView();
            });
            Platform.runLater(() -> testSwitchBox.getChildren().add(button));
        }
    }

    private void updateView() {
        resetAllAxis();
        plotResults();
        msdSeries.getData().clear();
        timeseries.getData().clear();
        slopeSeries.getData().clear();
        TPseries.getData().clear();
        plotTPCurve();
        updateAllCharts();
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
                slopeSeries.getData().add(point);
            });
        });
    }

    private void resetAllAxis() {
        resetCopZoom();
        resetSlopeZoom();
        resetTpZoom();
        NumberAxis recYY = (NumberAxis) recordingYChart.getYAxis();
        recYY.setLowerBound(-125);
        recYY.setUpperBound(125);
        NumberAxis recXY = (NumberAxis) recordingXChart.getYAxis();
        recXY.setLowerBound(-125);
        recXY.setUpperBound(125);
        timeseriesPlot.getYAxis().setAutoRanging(true);
        timeseriesPlot.getXAxis().setAutoRanging(true);
        msdPlot.getXAxis().setAutoRanging(true);
        msdPlot.getYAxis().setAutoRanging(true);
    }

    private void resetMsdZoom() {
        msdPlot.getYAxis().setAutoRanging(true);
        msdPlot.getXAxis().setAutoRanging(true);
    }

    private void resetXiZoom() {
        timeseriesPlot.getXAxis().setAutoRanging(true);
        timeseriesPlot.getYAxis().setAutoRanging(true);
    }

    private void resetXSplitZoom() {
        NumberAxis recXY = (NumberAxis) recordingXChart.getYAxis();
        recXY.setLowerBound(-125);
        recXY.setUpperBound(125);
        recXXAxis.setLowerBound(0);
        try {
            duration = Integer.parseInt(durationInput.getText());
            recXXAxis.setUpperBound(duration);
        } catch (NumberFormatException e) {
        }
    }

    private void resetYSplitZoom() {
        NumberAxis recYY = (NumberAxis) recordingYChart.getYAxis();
        recYY.setLowerBound(-125);
        recYY.setUpperBound(125);
        recYXAxis.setLowerBound(0);
        try {
            duration = Integer.parseInt(durationInput.getText());
            recYXAxis.setUpperBound(duration);
        } catch (NumberFormatException e) {
        }
    }

    private void resetCopZoom() {
        try {
            NumberAxis copX = (NumberAxis) copChart.getXAxis();
            NumberAxis copY = (NumberAxis) copChart.getYAxis();
            copX.setLowerBound(-150);
            copX.setUpperBound(150);
            copY.setLowerBound(-150);
            copY.setUpperBound(150);
        }catch (ConcurrentModificationException e) {}
    }

    private void resetSlopeZoom() {
        LogarithmicAxis slopeX = (LogarithmicAxis) slopePlot.getXAxis();
        LogarithmicAxis slopeY = (LogarithmicAxis) slopePlot.getYAxis();
        slopeX.setLowerBound(0.01);
        slopeX.setUpperBound(8);
        slopeY.setLowerBound(0.01);
        slopeY.setUpperBound(10000);
    }

    private void resetTpZoom() {
        LogarithmicAxis tpX = (LogarithmicAxis) tpResultPlot.getXAxis();
        LogarithmicAxis tpY = (LogarithmicAxis) tpResultPlot.getYAxis();
        tpX.setLowerBound(0.01);
        tpX.setUpperBound(8);
        tpY.setLowerBound(0.01);
        tpY.setUpperBound(1.5);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void plotCopRec(double xVal, double yVal) {
        Platform.runLater(() -> seriesRecording.getData().add(new XYChart.Data<>(xVal, yVal)));
    }

    private void updateAllCharts() {
        seriesRecording.getData().clear();
        seriesRecordingX.getData().clear();
        seriesRecordingY.getData().clear();
        List<List<Double>> list = logic.getCopList();
        list.forEach(a -> {
            plotCopRec(a.get(0), a.get(1));
            plotXRec(a.get(0), a.get(2));
            plotYRec(a.get(1), a.get(2));
        });
    }
}
