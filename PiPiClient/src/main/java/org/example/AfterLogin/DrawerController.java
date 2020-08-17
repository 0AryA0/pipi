package org.example.AfterLogin;

import com.jfoenix.controls.JFXButton;
import io.socket.client.Socket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DrawerController implements Initializable {

    @FXML
    private Label username;

    @FXML
    private JFXButton profile;

    @FXML
    private JFXButton main;

    @FXML
    private JFXButton games;

    @FXML
    private JFXButton logout;

    private Socket socket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socket = App.getSocket();
        username.setText(MainMenuController.getUsername());
        logout.setOnAction(actionEvent ->  {
            socket.off();
            try {
                App.setRoot("login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        main.setOnAction( actionEvent ->  {
            try {
                App.setRoot("main_menu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        profile.setOnAction(actionEvent -> {
            try {
                App.setRoot("profile");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        games.setOnAction(actionEvent ->  {
            try {
                App.setRoot("games");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
