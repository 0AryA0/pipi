package org.example;

import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    /*
    java --module-path /home/arya//Projects/JavaProjects/dependencies/javafx/openjfx-11.0.2_linux-x64_bin-sdk/javafx-sdk-11.0.2/lib --add-modules javafx.controls,javafx.fxml -jar FinalProjectAPClient-1.0-SNAPSHOT-jar-with-dependencies.jar
     */

    static final int PORT = 8585;
    static final String HOST_NAME = "http://localhost";

    private static Font font;
    private static Socket socket;

    public static Socket getSocket() {
        return socket;
    }

    private static Scene scene;

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        App.primaryStage = primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {

        clientConnect();
        font = Font.loadFont(App.class.getResource("font/Montserrat-Regular.ttf").toExternalForm(), 16);
        scene = new Scene(loadFXML("login"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        primaryStage = stage;



        stage.setOnCloseRequest(windowEvent ->  {
            socket.close();
            Platform.exit();
            System.exit(1);
            System.out.println("exit");
        });

    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void setRoot(Pane pane) {
        scene.setRoot(pane);
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void clientConnect() throws Exception {
        socket = IO.socket(HOST_NAME + ":" + PORT);
        socket.connect();

    }

    public static Font getFont() {
        return font;
    }

    public static void resizeStage(double width, double height) {
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
    }


}