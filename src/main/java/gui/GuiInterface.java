package gui;

import java.util.List;

public interface GuiInterface {

    /**
     * Used to send wiiboard connection info to gui
     * @param info
     */
    void updateConnectionInfo(String info);

    /**
     *Starts a coundown before a test is running
     */
    void startCountdown();

    /**
     * sends a recorded datapoint to gui
     * @param datapoint
     */
    void displayDataPoint(List<Double> datapoint);

    /**
     * Notifies the gui that a test has finished.
     */
    void notifyTestFinished();
}
