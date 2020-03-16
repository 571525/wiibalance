package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import wiiboard.wiiboardStack.WiiBoard;
import wiiboard.wiiboardStack.WiiBoardDiscoverer;
import wiiboard.wiiboardStack.WiiBoardDiscoveryListener;
import wiiboard.wiiboardStack.event.WiiBoardButtonEvent;
import wiiboard.wiiboardStack.event.WiiBoardListener;
import wiiboard.wiiboardStack.event.WiiBoardMassEvent;
import wiiboard.wiiboardStack.event.WiiBoardStatusEvent;

public class CopPlotTest extends Application {

    private ScatterChart<Double,Double> chart;
    private XYChart.Series series;


    @Override
    public void start(Stage primaryStage) throws Exception{

        this.series = new XYChart.Series<Double, Double>();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        loader.setController(new Controller());
        Parent root = loader.load();
        primaryStage.setTitle("Wiibalance");
        Scene scene = new Scene(root);
        chart = (ScatterChart<Double, Double>) scene.lookup("#chart");
        chart.getData().add(series);
        primaryStage.setScene(scene);
        primaryStage.show();
        initiateWiiboard();
    }




    public static void main(String[] args) {
        launch(args);
    }

    public void updatePlot(XYChart.Data<Double, Double> data) {
        series.getData().clear();
        series.getData().add(data);
    }

    private void initiateWiiboard() {

        System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
        WiiBoardListener wiiBoardListener = new WiiBoardListener() {

            @Override
            public void wiiBoardButtonEvent(WiiBoardButtonEvent buttonEvent) {

            }

            @Override
            public void wiiBoardMassReceived(WiiBoardMassEvent massEvent) {
                int L = 433; //wiiboardStack length
                int W = 228; // wiiboardStack width
                double TR = massEvent.getTopRight();
                double TL = massEvent.getTopLeft();
                double BR = massEvent.getBottomRight();
                double BL = massEvent.getBottomLeft();

                double xVal = (L/2) * (((TR+BR)-(TL+BL))/(TR+BR+TL+BL));
                double yVal = (W/2) * (((TR+TL)-(BR+BL))/(TR+BR+TL+BL));


                Platform.runLater(() -> {
                    updatePlot(new XYChart.Data<Double,Double>(xVal,yVal));
                });
            }

            @Override
            public void wiiBoardStatusReceived(WiiBoardStatusEvent status) {

            }

            @Override
            public void wiiBoardDisconnected() {

            }

        };

        WiiBoardDiscoveryListener wiiBoardDiscoveryListener = new WiiBoardDiscoveryListener() {
            @Override
            public void wiiBoardDiscovered(WiiBoard wiiboard) {
                wiiboard.addListener(wiiBoardListener);
            }
        };

        WiiBoardDiscoverer discoverer = WiiBoardDiscoverer.getWiiBoardDiscoverer();
        discoverer.addWiiBoardDiscoveryListener(wiiBoardDiscoveryListener);
        discoverer.startWiiBoardSearch();
    }



}
