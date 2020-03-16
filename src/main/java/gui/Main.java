package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import wiiboard.Wiiboard;

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

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        stackPane = new StackPane(l);
        textArea = new TextArea();
        textArea.setText("TEST");
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
