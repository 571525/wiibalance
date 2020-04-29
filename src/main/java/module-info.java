/**
 *
 */
module com.bachelor {
    requires javafx.controls;
    requires javafx.fxml;
    requires bluecove;
    exports com.bachelor.gui;
    exports com.bachelor.gui.controller;
    exports com.bachelor.gui.uiUtils;
    exports com.bachelor.logic;
    exports com.bachelor.wiiboard;
    exports com.bachelor.wiiboard.bluetooth;
    exports com.bachelor.wiiboard.wiiboardStack.event;
    exports com.bachelor.wiiboard.wiiboardStack;
    opens com.bachelor.gui.controller;
}