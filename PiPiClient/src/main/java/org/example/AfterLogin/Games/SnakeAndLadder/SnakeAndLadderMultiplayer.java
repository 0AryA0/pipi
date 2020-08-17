package org.example.AfterLogin.Games.SnakeAndLadder;

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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import org.example.AfterLogin.Games.Player;
import org.example.AfterLogin.MainMenuController;
import org.example.App;
import org.example.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SnakeAndLadderMultiplayer extends SnakeAndLadder implements Initializable {
    @FXML
    private AnchorPane game_board;

    @FXML
    private AnchorPane info;

    @FXML
    private JFXButton start;

    @FXML
    private JFXButton exit;

    @FXML
    private JFXListView<SnakeAndLadderPlayer> leaderBoard;

    @FXML
    private JFXListView<String> messages;

    @FXML
    private JFXTextField message;

    @FXML
    private JFXButton send;

    @FXML
    private AnchorPane dicePane;


    private String lobbyName;
    private boolean startGame = false;
    private Socket socket;
    private String turn = "";

    int i = 0;
    int diceNumber;

    private ObservableList<SnakeAndLadderPlayer> playersList;
    Dice dice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dice = new Dice();
        dicePane.getChildren().add(dice);
        lobbyName = SnakeAndLadderController.getJoinedLobbyName();
        socket = App.getSocket();
        playersList = FXCollections.observableArrayList();
        makeGameBoard(game_board);

        leaderBoard.setCellFactory(playerListView -> new LeaderBoardCell());
        leaderBoard.setItems(playersList);

        //get players in this lobby from LobbyHandler in server

        socket.emit("player_info", lobbyName, new Ack() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    System.out.println(jsonObject);
                    JSONArray jsonArray = jsonObject.getJSONArray("players");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        playersList.add(new SnakeAndLadderPlayer("red", jsonArray.getString(i)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("player_joined_game", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String color;
                        playersList.add(new SnakeAndLadderPlayer("red", objects[0].toString()));
                        leaderBoard.refresh();
                    }
                });
                System.out.println("pipi");
            }
        });

        startGame();
        turnHandle();
        playerMoved();
        nextTurn();
        winEvent();
        biggerThanHundred();
        getMessage();
        finish();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    leaderBoard.refresh();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        send.setOnAction(actionEvent ->  {
            try {
                send();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        exit.setOnAction(actionEvent ->  {
            socket.emit("exit_snake_and_ladder", MainMenuController.getUsername(), new Ack() {
                @Override
                public void call(Object... objects) {
                    String status = objects[0].toString();
                    if (status.equals("exit")) {
                        mainMenu();
                    }
                }
            });
        });

        message.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                try {
                    send();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void send() throws JSONException {
        if (!message.getText().equals("")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sender", MainMenuController.getUsername());
            jsonObject.put("message", message.getText());
            socket.emit("snake_and_ladder_message", jsonObject.toString());
            message.setText("");
        }
    }

    private void getMessage() {
        socket.on("snake_message", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String message = objects[0].toString();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        messages.getItems().add(message);

                    }
                });
            }
        });
    }

    private void biggerThanHundred() {
        socket.on("bigger100", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                int diceNumber = Integer.parseInt(objects[0].toString());
                dice.set(diceNumber);
            }
        });
    }

    private void startGame() {
        socket.on("start_game", new Emitter.Listener() {

            @Override
            public void call(Object... objects) {
                try {
                    JSONObject jsonObject = new JSONObject(objects[0].toString());

                    turn = jsonObject.getString("name");

                    for (SnakeAndLadderPlayer player : playersList) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 0) {
                                    player.setColor("red");
                                    players.add(player);
                                } else if (i == 1) {
                                    player.setColor("blue");
                                    players.add(player);
                                } else if (i == 2) {
                                    player.setColor("green");
                                    players.add(player);
                                } else {
                                    player.setColor("yellow");
                                    players.add(player);
                                }
                                i++;
                                player.start();
                                leaderBoard.refresh();
                                game_board.getChildren().add(player.getCircle());
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void winEvent() {
        socket.on("player_won", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();

                move(json);

                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("win event");

                        mainMenu();
                    }
                }, 3000);

            }
        });
    }

    private void mainMenu() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                socket.off();
                playersList.removeAll();
                players.removeAll(players);

                try {
                    App.setRoot("main_menu");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void turnHandle() {
        start.setOnAction(actionEvent -> {
            System.out.println(isMyTurn());
            if (isMyTurn()) {
                socket.emit("turn_event", MainMenuController.getUsername());
            }
        });
    }

    private void nextTurn() {
        socket.on("next_turn", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                turn = objects[0].toString();
            }
        });
    }

    private void finish() {
        socket.on("finish", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String status = objects[0].toString();
                System.out.println(status);
                mainMenu();
            }
        });
    }


    private void playerMoved() {
        socket.on("player_moved", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("player moved");
                String json = objects[0].toString();

                move(json);
            }
        });
    }

    private void move(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String username = jsonObject.getString("username");
            int diceNumber = jsonObject.getInt("dice_number");
            int newPlace = jsonObject.getInt("new_place");

            SnakeAndLadderPlayer player = search(username);
            dice.set(diceNumber);

            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    for (int i = Objects.requireNonNull(player).current(); i <= newPlace; i++) {
                        Objects.requireNonNull(player).goTo(i);
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            checkForSnakeOrLadder(SnakeAndLadder.getPosition(player.getName()));

                        }
                    });
                }
            }, 1500);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isMyTurn() {
        return turn.equals(MainMenuController.getUsername());
    }

    private SnakeAndLadderPlayer search(String username) {
        for (SnakeAndLadderPlayer player : playersList) {
            if (player.getName().equals(username)) {
                return player;
            }
        }
        return null;
    }
}
