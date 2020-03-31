package gui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import logic.Logic;
import wiiboard.Wiiboard;

public class DashboardController {
    private Logic logic;
    private Wiiboard wiiboard;

    @FXML
    private Button connectButton;

    @FXML
    private TextField wiiStats;


    public DashboardController(Logic logic, Wiiboard wiiboard) {
        this.logic = logic;
        this.wiiboard = wiiboard;
    }


    public void setConnectionInfo(String info) {
        wiiStats.setText(info);
    }

    public void connect() {
        connectButton.setOnMouseClicked(e -> wiiboard.startWiiboardDiscoverer());
    }

}
