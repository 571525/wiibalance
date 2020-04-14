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

import javax.bluetooth.BluetoothStateException;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the wiiboard interface and is the connecting class to the rest of the application
 */
public class Wiiboard implements WiiboardInterface {

    private static final int L = 433; //wiiboardStack length
    private static final int W = 228; // wiiboardStack width

    private double tr;
    private double tl;
    private double br;
    private double bl;

    private double xVal = 0.0, yVal = 0.0, xPrev = 0.0, yPrev = 0.0;


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

            xVal = (L / 2.0) * (((tr + br) - (tl + bl)) / (tr + br + tl + bl));
            yVal = (W / 2.0) * (((tr + tl) - (br + bl)) / (tr + br + tl + bl));

            gui.notifyCopChanged(xVal,yVal);

            updated = true;
        }

        @Override
        public void wiiBoardStatusReceived(WiiBoardStatusEvent status) {

        }

        @Override
        public void wiiBoardDisconnected() {
            gui.updateConnectionInfo("Disconnected");
        }
    };

    private WiiBoardDiscoverer discoverer;
    private WiiBoardDiscoveryListener discoveryListener;

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
            if(WiiBoardDiscoverer.isBluetoothReady()) {
                discoveryListener = wiiboard -> wiiboard.addListener(listener);
                discoverer = WiiBoardDiscoverer.getWiiBoardDiscoverer(gui);
                discoverer.addWiiBoardDiscoveryListener(discoveryListener);
                discoverer.startWiiBoardSearch();
            } else {
                gui.updateConnectionInfo("Bluetooth not available");
            }
        } else {
            throw new NullPointerException("Gui is not registered");
        }
    }

    @Override
    public void startRecordingData(int seconds) {
        new Thread(() -> {
            logic.clearData();

            long start = System.currentTimeMillis();
            long duration = start + seconds * 1000;

            while (System.currentTimeMillis() < duration) {
                if (updated) {

                    if (xVal != xPrev || yVal != yPrev) { //We want unique values

                        double time = (System.currentTimeMillis() - start) / 1000.0;
                        logic.addCopPoint(getxVal(), getyVal(), time);

                        gui.plotXrecorded(xVal,time);
                        gui.plotYrecorded(yVal,time);
                        gui.plotCOPRecorded(xVal,yVal);

                        xPrev = xVal;
                        yPrev = yVal;
                    }
                    updated = false;
                }
            }

            gui.notifyTestFinished();
        }).start();
    }

    public double getxVal() {
        return xVal;
    }

    public void setxVal(double xVal) {
        this.xVal = xVal;
    }

    public double getyVal() {
        return yVal;
    }

    public void setyVal(double yVal) {
        this.yVal = yVal;
    }

    @Override
    public List getCopPoint() {
        return Arrays.asList(xVal, yVal);
    }
}
