package org.example.AfterLogin.Games;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import io.socket.client.Socket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.example.AfterLogin.MainMenuController;
import org.example.App;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LobbyCell extends JFXListCell<Lobby> {
    FXMLLoader loader;

    @FXML
    private Label name;
    @FXML
    private Label players;
    @FXML
    private AnchorPane pane;

    @FXML
    private JFXButton join;

    Socket socket;


    @Override
    public void updateItem(Lobby item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {
            setText(null);
            setGraphic(null);
        }
        else {

            this.socket = App.getSocket();

            if(loader == null) {
                loader = new FXMLLoader(App.class.getResource("lobby_cell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            name.setText(item.getLobbyName());
            players.setText("" + item.getCurrentPlayers() + " / " + item.getNumberOfPlayers());
            join.setOnAction(actionEvent ->  {
                JSONObject jsonObject = new JSONObject();

                if (item.getGameName().equals("snake and ladder")) {
                    try {
                        jsonObject.put("username", MainMenuController.getUsername());
                        jsonObject.put("game_name", "snake and ladder");
                        jsonObject.put("lobby_name", item.getLobbyName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("join_lobby", jsonObject.toString());
                }
                else if (item.getGameName().equals("tic tac toe")) {
                    try {
                        jsonObject.put("username", MainMenuController.getUsername());
                        jsonObject.put("game_name", "tic tac toe");
                        jsonObject.put("lobby_name", item.getLobbyName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("join_lobby", jsonObject.toString());
                }



            });
            setText(null);
            setGraphic(pane);
        }
    }
}
