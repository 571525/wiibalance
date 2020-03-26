package wiiboard;

import gui.GuiInterface;
import logic.LogicInterface;
import wiiboard.wiiboardStack.WiiBoard;
import wiiboard.wiiboardStack.WiiBoardDiscoverer;
import wiiboard.wiiboardStack.WiiBoardDiscoveryListener;
import wiiboard.wiiboardStack.event.WiiBoardButtonEvent;
import wiiboard.wiiboardStack.event.WiiBoardListener;
import wiiboard.wiiboardStack.event.WiiBoardMassEvent;
import wiiboard.wiiboardStack.event.WiiBoardStatusEvent;

/**
 * This class implements the wiiboard interface and is the connecting class to the rest of the application
 */
public class Wiiboard implements WiiboardInterface {

    private static final int L = 433; //wiiboardStack length
    private static final int W = 228; // wiiboardStack width

    private volatile double tr;
    private volatile double tl;
    private volatile double br;
    private volatile double bl;
    private volatile boolean updated = false;

    private LogicInterface logic;
    private GuiInterface gui;

    private WiiBoardListener listener = new WiiBoardListener() {
        @Override
        public void wiiBoardButtonEvent(WiiBoardButtonEvent buttonEvent) {

        }

        @Override
        public void wiiBoardMassReceived(WiiBoardMassEvent massEvent) {
            tr = massEvent.getTopRight();
            tl = massEvent.getTopLeft();
            br = massEvent.getBottomRight();
            bl = massEvent.getBottomLeft();
            updated = true;
        }

        @Override
        public void wiiBoardStatusReceived(WiiBoardStatusEvent status) {

        }

        @Override
        public void wiiBoardDisconnected() {

        }
    };

    private WiiBoardDiscoverer discoverer;
    private WiiBoardDiscoveryListener discoveryListener = new WiiBoardDiscoveryListener() {
        @Override
        public void wiiBoardDiscovered(WiiBoard wiiboard) {
            wiiboard.addListener(listener);
        }
    };

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
            discoverer = WiiBoardDiscoverer.getWiiBoardDiscoverer(gui);
            discoverer.addWiiBoardDiscoveryListener(discoveryListener);
            discoverer.startWiiBoardSearch();
        } else {
            throw new NullPointerException("Gui is not registered");
        }
    }

    @Override
    public void startRecordingData(int seconds) {

        logic.clearData();

        long start = System.currentTimeMillis();
        long duration = start + seconds * 1000;

        gui.startCountdown();

        double xVal, yVal, xPrev = 0.0, yPrev = 0.0;
        while (System.currentTimeMillis() < duration) {
            if (updated) {

                xVal = (L / 2.0) * (((tr + br) - (tl + bl)) / (tr + br + tl + bl));
                yVal = (W / 2.0) * (((tr + tl) - (br + bl)) / (tr + br + tl + bl));

                if(xVal != xPrev || yVal != yPrev) { //We want unique values
                    logic.addCopPoint(xVal, yVal, (System.currentTimeMillis() - start) / 1000.0);
                    xPrev = xVal;
                    yPrev = yVal;
                }
                updated = false;
            }
        }

        gui.notifyTestFinished();
    }
}
