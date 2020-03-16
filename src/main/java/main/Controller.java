package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ScatterChart<Double,Double> chart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
