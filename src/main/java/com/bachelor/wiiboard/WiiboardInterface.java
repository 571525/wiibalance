package com.bachelor.wiiboard;

import com.bachelor.gui.GuiInterface;
import com.bachelor.logic.LogicInterface;

public interface WiiboardInterface {

    void registerGui(GuiInterface gui);

    void registerLogic(LogicInterface logic);

    void startWiiboardDiscoverer();

    void startRecordingData(int seconds);

}
