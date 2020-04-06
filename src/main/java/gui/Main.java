package gui;

import gui.controller.DashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.Logic;
import wiiboard.Wiiboard;


/**
 * This is the centre of the application, responsible for gui and all couplings in the application.
 */
public class Main extends Application implements GuiInterface {

    private DashboardController controller;
    private Scene scene;
    private Logic logic;
    private Wiiboard wiiboard;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        logic = new Logic();
        wiiboard = new Wiiboard();

        wiiboard.registerLogic(logic);
        wiiboard.registerGui(this);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
        controller = new DashboardController(logic, wiiboard);
        loader.setController(controller);
        Parent root = loader.load();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        controller.setup();
    }

    @Override
    public void updateConnectionInfo(String info) {
        controller.setConnectionInfo(info);
    }


    @Override
    public void notifyTestFinished() {
        controller.stopRecording();
    }

    @Override
    public void notifyCopChanged(double xVal, double yVal) {
        controller.plotPoint(xVal, yVal);
    }

    @Override
    public void plotXrecorded(double xVal, double time) {
        controller.plotXRec(xVal, time);
    }

    @Override
    public void plotYrecorded(double yVal, double time) {
        controller.plotYRec(yVal, time);
    }
}
