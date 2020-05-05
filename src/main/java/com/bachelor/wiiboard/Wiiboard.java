package com.bachelor.wiiboard;

import com.bachelor.gui.GuiInterface;
import com.bachelor.logic.LogicInterface;
import com.bachelor.wiiboard.wiiboardStack.WiiBoardDiscoverer;
import com.bachelor.wiiboard.wiiboardStack.WiiBoardDiscoveryListener;
import com.bachelor.wiiboard.wiiboardStack.event.WiiBoardButtonEvent;
import com.bachelor.wiiboard.wiiboardStack.event.WiiBoardListener;
import com.bachelor.wiiboard.wiiboardStack.event.WiiBoardMassEvent;
import com.bachelor.wiiboard.wiiboardStack.event.WiiBoardStatusEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class implements the wiiboard interface and is the connecting class to the rest of the application
 */
public class Wiiboard implements WiiboardInterface {

    private static final int L = 433; //wiiboardStack length
    private static final int W = 228; // wiiboardStack width


    private AtomicReference<Double> xVal = new AtomicReference<>(0.0);
    private AtomicReference<Double> yVal = new AtomicReference<>(0.0);
    private boolean updated = false;

    private LogicInterface logic;
    private GuiInterface gui;
    private ExecutorService worker;

    private WiiBoardListener listener = new WiiBoardListener() {
        @Override
        public void wiiBoardButtonEvent(WiiBoardButtonEvent buttonEvent) {
            if (buttonEvent.isPressed())
                buttonEvent.getWiiBoard().requestStatus();
            if (buttonEvent.isReleased()) {
                System.out.println(Thread.getAllStackTraces().keySet());
            }
        }

        @Override
        public void wiiBoardMassReceived(WiiBoardMassEvent massEvent) {
            double tr = massEvent.getTopRight();
            double tl = massEvent.getTopLeft();
            double br = massEvent.getBottomRight();
            double bl = massEvent.getBottomLeft();

            double yNew = (L / 2.0) * (((tr + br) - (tl + bl)) / (tr + br + tl + bl));
            double xNew = -(W / 2.0) * (((tr + tl) - (br + bl)) / (tr + br + tl + bl));
            setxVal(calibrateW(xNew));
            setyVal(calibrateL(yNew));
            setUpdated(true);
            worker.execute(() -> gui.notifyCopChanged(xNew, yNew));
        }

        @Override
        public void wiiBoardStatusReceived(WiiBoardStatusEvent status) {
            System.out.println(status.batteryLife());
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
        worker = Executors.newCachedThreadPool(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
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
            if (WiiBoardDiscoverer.isBluetoothReady()) {
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
        Thread t = new Thread(() -> {
            logic.clearData();

            long start = System.currentTimeMillis();
            long duration = start + seconds * 1000;
            double xPrev = 0.0, yPrev = 0.0;

            while (System.currentTimeMillis() < duration) {
                if (isUpdated()) {
                    double xTemp = getxVal();
                    double yTemp = getyVal();

                    if (xTemp != xPrev || yTemp != yPrev) { //We want unique values
                        double time = (System.currentTimeMillis() - start) / 1000.0;
                        logic.addCopPoint(xTemp, yTemp, time);
                        xPrev = xTemp;
                        yPrev = yTemp;

                        worker.execute(() -> {
                            gui.plotXrecorded(xTemp, time);
                            gui.plotYrecorded(yTemp, time);
                            gui.plotCOPRecorded(xTemp, yTemp);
                        });

                    }
                }
            }

            gui.notifyTestFinished();
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * Calibrations are based on the article "Validating and Calibrating the Nintendo Wii
     * Balance Board to Derive Reliable Center of Pressure Measures"
     * located at https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4239899/ section 2.4.4.
     * This method calibrates the longer side of the board measuring 433mm.
     *
     * @param rec
     * @return
     */
    private double calibrateL(double rec) {
        return 1 / 1.098 * (rec + 0.001);
    }

    /**
     * Calibrations are based on the article "Validating and Calibrating the Nintendo Wii
     * Balance Board to Derive Reliable Center of Pressure Measures"
     * located at https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4239899/ section 2.4.4.
     *
     * This method calibrates the shorter side of the board measuring 228mm.
     * @param rec
     * @return
     */
    private double calibrateW(double rec) {
        return 1 / 1.088 * (rec - 0.002);
    }

    public double getxVal() {
        return xVal.get();
    }

    public void setxVal(double xVal) {
        this.xVal.set(xVal);
    }

    public double getyVal() {
        return yVal.get();
    }

    public void setyVal(double yVal) {
        this.yVal.set(yVal);
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

}
