package org.example.AfterLogin.Games.TicTacToe;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.AfterLogin.Games.Lobby;
import org.example.AfterLogin.Games.LobbyCell;
import org.example.AfterLogin.MainMenuController;
import org.example.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TicTacToeController implements Initializable {

    @FXML
    private JFXTextField lobby_name;

    @FXML
    private JFXButton create_btn;

    @FXML
    private JFXButton play;

    @FXML
    private JFXListView<Lobby> lobbies;

    @FXML
    private JFXButton back;

    private Socket socket;


    private static String joinedLobbyName;

    private ObservableList<Lobby> lobbyObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socket = App.getSocket();

        getLobbiesInformation();

        create_btn.setOnAction(actionEvent -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lobby_name", lobby_name.getText());
                jsonObject.put("username", MainMenuController.getUsername());
                jsonObject.put("number_of_players", 2);
                jsonObject.put("game_name", "tic tac toe");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("lobby_creation", jsonObject.toString());

        });

        play.setOnAction(actionEvent -> {
            try {
                App.setRoot("tic_tac_toe_single");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        back.setOnAction(actionEvent -> {
            try {
                App.setRoot("main_menu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        joinLobby();
        lobbyAddWhileOnline();

        lobbies.setCellFactory(lobbyListView -> new LobbyCell());
        lobbies.setItems(lobbyObservableList);
    }

    private void joinLobby() {
        socket.on("join_lobby_info", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    String status = jsonObject.getString("status");

                    System.out.println(status);

                    if (status.equals("joined_successfully")) {
                        joinedLobbyName = jsonObject.getString("lobby");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    App.setRoot("tic_tac_toe_multiplayer");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getLobbiesInformation() {
        lobbyObservableList = FXCollections.observableArrayList();
        lobbyObservableList.removeAll();

        socket.emit("lobby_info", "tic tac toe", new Ack() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();
                System.out.println(json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("lobbies");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        System.out.println(json);
                        int number = jsonArray.getJSONObject(i).getInt("number_of_players");
                        int current = jsonArray.getJSONObject(i).getInt("current");
                        String name = jsonArray.getJSONObject(i).getString("lobby_name");
                        Lobby lobby = new Lobby(name, number, current);
                        lobby.setGameName("tic tac toe");

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lobbyObservableList.add(lobby);
                                lobbies.refresh();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void lobbyAddWhileOnline() {
        socket.on("lobby_created_while_online", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();
                System.out.println(json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int number = jsonObject.getInt("number_of_players");
                    int current = jsonObject.getInt("current");
                    String name = jsonObject.getString("lobby_name");
                    String gameName = jsonObject.getString("game_name");

                    if (gameName.equals("tic tac toe")) {
                        Lobby lobby = new Lobby(name, number, current);
                        lobby.setGameName(gameName);
                        System.out.println(name);
                        System.out.println(number);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lobbyObservableList.add(lobby);
                                lobbies.refresh();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
