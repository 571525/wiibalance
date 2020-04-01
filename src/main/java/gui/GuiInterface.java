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

}
