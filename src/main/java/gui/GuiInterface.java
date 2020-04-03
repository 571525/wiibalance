package gui;

import java.util.List;

public interface GuiInterface {

    /**
     * Used to send wiiboard connection info to gui
     * @param info
     */
    void updateConnectionInfo(String info);


    /**
     * Notifies the gui that a test has finished.
     */
    void notifyTestFinished();

    /**
     * Plots new point on COP chart
     * @param xVal
     * @param yVal
     */
    void notifyCopChanged(double xVal, double yVal);

    /**
     * Plot recorded xVal and time on recorded X chart
     * @param xVal
     * @param time
     */
    void plotXrecorded(double xVal, double time);

    /**
     * Plots recorded yVal and time on Y chart
     * @param yVal
     * @param time
     */
    void plotYrecorded(double yVal, double time);
}
