package wiiboard;

import gui.GuiInterface;
import logic.LogicInterface;
import wiiboard.wiiboardStack.WiiBoardDiscoverer;

/**
 * This class implements the wiiboard interface and is the connecting class to the rest of the application
 */
public class Wiiboard implements WiiboardInterface {

    private LogicInterface logic;
    private GuiInterface gui;

    public Wiiboard() {
        System.setProperty("bluecove.jsr82.psm_minimum_off", "true"); //enable bluetooth to work properly

    }


    @Override
    public void registerGui(GuiInterface gui) {
        this.gui = gui;
    }

    @Override
    public void registerLogic(LogicInterface logic) {
        this.logic = logic;
    }

    @Override
    public void startWiiboardDiscoverer() {
        if (gui != null) {
            WiiBoardDiscoverer discoverer = WiiBoardDiscoverer.getWiiBoardDiscoverer(gui);
            discoverer.startWiiBoardSearch();
        }else {
            throw new NullPointerException("Gui is not registered");
        }
    }

    @Override
    public void startRecordingData(int seconds) {

    }
}
