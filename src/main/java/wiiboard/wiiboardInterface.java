package wiiboard;

import gui.GuiInterface;
import logic.LogicInterface;

import java.util.List;

public interface wiiboardInterface {

    void registerGui(GuiInterface gui);

    void startWiiboardDiscoverer();

    void startRecordingData(LogicInterface logic);





}
