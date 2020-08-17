package org.example.AfterLogin;

import com.jfoenix.controls.JFXButton;
import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.example.App;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.AcceptPendingException;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML
    private AnchorPane pane;

    @FXML
    private Label solo_win;

    @FXML
    private Label solo_lose;

    @FXML
    private Label multi_win;

    @FXML
    private Label multi_lose;

    @FXML
    private JFXButton back;

    private Socket socket;
    String username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socket = App.getSocket();
        username = MainMenuController.getUsername();

        socket.emit("win_lose_information", username, new Ack() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject snakeAndLadder = new JSONObject(jsonObject.getString("snake_and_ladder"));
                    int soloLose = snakeAndLadder.getInt("lose_solo");
                    int soloWin = snakeAndLadder.getInt("win_solo");
                    int multiLose = snakeAndLadder.getInt("lose_multi");
                    int multiWin = snakeAndLadder.getInt("win_multi");

                    JSONObject ticTacToe = new JSONObject(jsonObject.getString("tic_tac_toe"));
                    soloLose += ticTacToe.getInt("lose_solo");
                    soloWin += ticTacToe.getInt("win_solo");
                    multiLose += ticTacToe.getInt("lose_multi");
                    multiWin += ticTacToe.getInt("win_multi");


                    solo_win.setText(String.valueOf(soloWin));
                    solo_lose.setText(String.valueOf(soloLose));
                    multi_lose.setText(String.valueOf(multiLose));
                    multi_win.setText(String.valueOf(multiWin));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        back.setOnAction(actionEvent ->  {
            try {
                socket.off();
                App.setRoot("main_menu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
