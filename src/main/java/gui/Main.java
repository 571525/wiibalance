package gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import logic.Logic;
import wiiboard.Wiiboard;

import java.util.List;

/**
 * This is the centre of the application, responsible for gui and all couplings in the application.
 */
public class Main extends Application implements GuiInterface {

    private Scene scene;
    private StackPane stackPane;
    private TextArea textArea;

    private Wiiboard wiiboard = new Wiiboard();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        stackPane = new StackPane();
        Button button = new Button();

        button.setText("TEST CLICK");
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                wiiboard.startRecordingData(20);
            }
        });

        textArea = new TextArea();
        stackPane.getChildren().add(textArea);
        stackPane.getChildren().add(button);
        scene = new Scene(stackPane, 640, 480);
        stage.setScene(scene);
        stage.show();
        wiiboard.registerGui(this);
        wiiboard.registerLogic(new Logic());
        wiiboard.startWiiboardDiscoverer();


    }

    @Override
    public void updateConnectionInfo(String info) {
        textArea.setText(info);
    }

    @Override
    public void startCountdown() {

    }

    @Override
    public void displayDataPoint(List<Double> datapoint) {

    }

    @Override
    public void notifyTestFinished() {

    }


}
