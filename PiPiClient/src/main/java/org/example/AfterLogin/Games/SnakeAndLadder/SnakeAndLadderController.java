package org.example.AfterLogin.Games.SnakeAndLadder;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class SnakeAndLadderController implements Initializable {

    @FXML
    private JFXTextField lobby_name;

    @FXML
    private JFXSlider create_slider;

    @FXML
    private JFXButton create_btn;

    @FXML
    private JFXSlider bot_slider;

    @FXML
    private JFXButton play;

    @FXML
    private JFXListView<Lobby> lobbies;

    @FXML
    private JFXButton back;

    private Socket socket;
    private static String joinedLobbyName;

    private static int numberOfPlayersSingle;
    private static int numberOfPlayersMultiplayer;

    private ObservableList<Lobby> lobbyObservableList;

    public static int getNumberOfPlayersSingle() {
        return numberOfPlayersSingle;
    }

    public SnakeAndLadderController() {
        socket = App.getSocket();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socket = App.getSocket();
        getLobbiesInformation();

        sliders();
        buttons();
        lobbyAddWhileOnline();
        joinLobby();
        refreshLobby();
        checkForDelete();

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
                                    App.setRoot("snake_and_ladder_multiplayer");
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

    private void refreshLobby() {
        socket.on("refresh_lobby", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String lobbyName = jsonObject.getString("lobby_name");
                    String gameName = jsonObject.getString("game_name");
                    int current = jsonObject.getInt("current");

                    for (Lobby lobby : lobbyObservableList) {
                        if (lobby.getLobbyName().equals(lobbyName)) {
                            lobby.setCurrentPlayers(current);
                            lobbies.refresh();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void checkForDelete() {
        socket.on("refresh_lobby_for_delete", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String lobbyName = objects[0].toString();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lobbyObservableList.removeIf(lobby -> lobby.getLobbyName().equals(lobbyName));
                        lobbies.refresh();
                    }
                });
            }
        });
    }

    private void getLobbiesInformation() {
        lobbyObservableList = FXCollections.observableArrayList();
        lobbyObservableList.removeAll();

        socket.emit("lobby_info", "snake and ladder", new Ack() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("lobbies");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int number = jsonArray.getJSONObject(i).getInt("number_of_players");
                        int current = jsonArray.getJSONObject(i).getInt("current");
                        String name = jsonArray.getJSONObject(i).getString("lobby_name");
                        System.out.println(name);
                        Lobby lobby = new Lobby(name, number, current);
                        lobby.setGameName("snake and ladder");

                        lobbyObservableList.add(lobby);
                        lobbies.refresh();
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
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int number = jsonObject.getInt("number_of_players");
                    int current = jsonObject.getInt("current");
                    String name = jsonObject.getString("lobby_name");
                    String gameName = jsonObject.getString("game_name");

                    if (gameName.equals("snake and ladder")) {
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

    private void buttons() {
        create_btn.setOnAction(actionEvent -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", MainMenuController.getUsername());
                jsonObject.put("lobby_name", lobby_name.getText());
                jsonObject.put("number_of_players", numberOfPlayersMultiplayer);
                jsonObject.put("game_name", "snake and ladder");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("lobby_creation", jsonObject.toString());
        });
        back.setOnAction(actionEvent -> {
            try {
                App.setRoot("main_menu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        play.setOnAction(actionEvent -> {
            try {
                App.setRoot("snake_and_ladder_single");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void sliders() {
        bot_slider.setMax(4);
        bot_slider.setMin(2);

        create_slider.setMax(4);
        create_slider.setMin(2);

        numberOfPlayersMultiplayer = 4;
        numberOfPlayersSingle = 4;

        bot_slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                numberOfPlayersSingle = newValue.intValue();
            }
        });

        create_slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                numberOfPlayersMultiplayer = newValue.intValue();
            }
        });
    }

    public static String getJoinedLobbyName() {
        return joinedLobbyName;
    }
}
