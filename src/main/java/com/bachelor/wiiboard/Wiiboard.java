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

/**
 * This class implements the wiiboard interface and is the connecting class to the rest of the application
 */
public class Wiiboard implements WiiboardInterface {

    private static final int L = 433; //Board length in mm
    private static final int W = 228; //Board width in mm
    private long recStart = 0;
    private boolean recording = false;
    private LogicInterface logic;
    private GuiInterface gui;
    private ExecutorService worker;
    private ExecutorService guiWorker;
    private double xNew = 0.0;
    private double yNew = 0.0;
    private double xPrev = 0.0;
    private double yPrev = 0.0;

    private WiiBoardDiscoverer discoverer;
    private WiiBoardDiscoveryListener discoveryListener;
    private WiiBoardListener listener = new WiiBoardListener() {
        @Override
        public void wiiBoardButtonEvent(WiiBoardButtonEvent buttonEvent) {
            if (buttonEvent.isPressed())
                buttonEvent.getWiiBoard().requestStatus();
        }

        @Override
        public void wiiBoardMassReceived(WiiBoardMassEvent massEvent) {
            worker.execute(() -> {
                double tr = massEvent.getTopRight();
                double tl = massEvent.getTopLeft();
                double br = massEvent.getBottomRight();
                double bl = massEvent.getBottomLeft();
                double recTime = (System.currentTimeMillis() - recStart) / 1000.0;

                yPrev = yNew;
                xPrev = xNew;
                yNew = calibrateL((L / 2.0) * (((tr + br) - (tl + bl)) / (tr + br + tl + bl)));
                xNew = -calibrateW((W / 2.0) * (((tr + tl) - (br + bl)) / (tr + br + tl + bl)));

                if (recording) {
                    if (xNew != xPrev || yNew != yPrev) { // we want unique readings
                        logic.addCopPoint(xNew, yNew, recTime);
                        guiWorker.execute(() -> {
                            gui.plotXrecorded(xNew, recTime);
                            gui.plotYrecorded(yNew, recTime);
                            gui.plotCOPRecorded(xNew, yNew);
                        });
                    }
                }
                guiWorker.execute(() -> gui.notifyCopChanged(xNew, yNew));
            });
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

    public Wiiboard() {
        System.setProperty("bluecove.jsr82.psm_minimum_off", "true"); //enable bluetooth to work properly
        worker = Executors.newCachedThreadPool(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
        guiWorker = Executors.newCachedThreadPool(r -> {
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
            recording = true;
            recStart = System.currentTimeMillis();
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
            }
            recording = false;
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
     * <p>
     * This method calibrates the shorter side of the board measuring 228mm.
     *
     * @param rec
     * @return
     */
    private double calibrateW(double rec) {
        return 1 / 1.088 * (rec - 0.002);
    }
}