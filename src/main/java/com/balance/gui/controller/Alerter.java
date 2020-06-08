package com.balance.gui.controller;

import javafx.scene.control.Alert;

public class Alerter {
     /**
     * Alerts a message dialog to the user
     *
     * @param message
     */
    public static void showDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wiibalance");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
