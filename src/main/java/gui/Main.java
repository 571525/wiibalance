package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import wiiboard.Wiiboard;

/**
 * This is the centre of the application, responisble for gui and all couplings in the application.
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
        textArea = new TextArea();
        stackPane.getChildren().add(textArea);
        scene = new Scene(stackPane, 640, 480);
        stage.setScene(scene);
        stage.show();
        wiiboard.registerGui(this);
        wiiboard.startWiiboardDiscoverer();

    }

    @Override
    public void updateConnectionInfo(String info) {
        textArea.setText(info);
    }


}
