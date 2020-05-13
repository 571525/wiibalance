package com.bachelor.wiiboard;

import com.bachelor.gui.GuiInterface;
import com.bachelor.logic.LogicInterface;

public interface WiiboardInterface {

    /**
     * Register the gui to be used in the application
     * @param gui
     */
    void registerGui(GuiInterface gui);

    /**
     * Register the Logic implemented by this application
     * @param logic
     */
    void registerLogic(LogicInterface logic);

    /**
     * Starts the discovering of the wii balance board
     */
    void startWiiboardDiscoverer();

    /**
     * Starts data recording
     * @param seconds to record data
     */
    void startRecordingData(int seconds);

}
