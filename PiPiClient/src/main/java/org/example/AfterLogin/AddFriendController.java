package org.example.AfterLogin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.App;

import java.net.URL;
import java.util.ResourceBundle;

public class AddFriendController implements Initializable {

    Socket socket;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXButton login;

    private ObservableList<User> users;


    public AddFriendController () {
        this.socket = App.getSocket();
        users = MainMenuController.getUsers();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        handleFriendRequest();

        socket.on("add_friend_status", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String status = objects[0].toString();

                if (status.equals("username_not_found")) {
                    username.setText("");
                    username.setPromptText("username notfound");
                } else if (status.equals("duplicated")) {
                    username.setText("");
                    username.setPromptText("already exists");
                } else {

                    System.out.println(users.toString());
                    username.setText("");
                    username.setPromptText("friend added");
                    users.add(new User(status, false));
                }

            }
        });
    }

    private void handleFriendRequest() {

        login.setOnAction(actionEvent -> {
            String friend = username.getText();
            System.out.println("clicked");

            socket.emit("friend_request", friend);

        });

    }
}
