package org.example.Prequals;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Socket;

import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import org.example.AfterLogin.MainMenuController;
import org.example.App;
import org.json.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class LoginController implements Initializable {

    @FXML
    private JFXButton login;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private Label forgotPass;
    @FXML
    private Label regHere;

    private Socket socket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /*
        send a json containing username and password
         */

        System.out.println("login page");

        socket = App.getSocket();

        login.setOnAction(actionEvent -> {
            JSONObject userPass = new JSONObject();

            try {
                userPass.put("username", username.getText());
                userPass.put("password", password.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("login", userPass.toString());
            System.out.println("login");

        });

        forgotPass.setOnMouseClicked(mouseEvent -> {
            try {
                socket.off();

                App.setRoot("forgot");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        password.setOnMouseClicked(mouseEvent -> {
            password.setUnFocusColor(Paint.valueOf("4d4d4d"));
        });

        username.setOnMouseClicked(mouseEvent -> {
            username.setUnFocusColor(Paint.valueOf("4d4d4d"));
        });


        /*
        listener for login answer
         */
        socket.on("login_info", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String info = objects[0].toString();

                if (info.equals("password_incorrect")) {
                    password.setText("");
                    password.setPromptText("incorrect password");
                    password.setUnFocusColor(Paint.valueOf("red"));
                }
                if (info.equals("not_found")) {
                    username.setText("");
                    username.setPromptText("username not found");
                    username.setUnFocusColor(Paint.valueOf("red"));
                }

                if (info.equals("success")) {
                    System.out.println("logged in");

                    socket.off();

                    MainMenuController.setUsername(username.getText());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                App.setRoot("main_menu");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

            }
        });


        regHere.setOnMouseClicked(mouseEvent -> {
            try {
                socket.off();

                App.setRoot("register");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

}
