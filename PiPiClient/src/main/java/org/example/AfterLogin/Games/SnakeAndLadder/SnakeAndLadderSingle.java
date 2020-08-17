package org.example.AfterLogin.Games.SnakeAndLadder;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.example.AfterLogin.MainMenuController;
import org.example.App;
import org.example.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class SnakeAndLadderSingle extends SnakeAndLadder implements Initializable {

    @FXML
    private AnchorPane game_board;

    private ObservableList<SnakeAndLadderPlayer> leaderBoardsList;

    @FXML
    private JFXListView<SnakeAndLadderPlayer> leaderBoard;

    @FXML
    private AnchorPane info;

    @FXML
    private JFXButton start;

    @FXML
    private JFXButton exit;

    @FXML
    private Dice dice;
    private Socket socket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        socket = App.getSocket();
        leaderBoardsList = FXCollections.observableArrayList();

        NUMBER_OF_PLAYERS = SnakeAndLadderController.getNumberOfPlayersSingle();
        System.out.println(NUMBER_OF_PLAYERS);

        makeGameBoard(game_board);
        SnakeAndLadder.resetPlayers();

        addPlayers();

        leaderBoard.setCellFactory(playerListView -> new LeaderBoardCell());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    leaderBoard.refresh();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        dice = new Dice();
        info.getChildren().add(dice);



        start.setOnAction(actionEvent -> {
            for (int i = 0; i < players.size(); i++) {
                int x = i;
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        turn(x);
                    }
                },x * 4000);
            }
        });

        exit.setOnAction(actionEvent ->  {
            try {
                App.setRoot("main_menu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void turn(int x) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dice.roll();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int number = dice.getValueProperty();

                        SnakeAndLadderPlayer player = players.get(x);

                        int current = player.current();

                        if(!(current + number > 100)) {
                            for (int i = current; i <= current + number; i++) {
                                player.goTo(i);

                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(current + number == 100) {
                            System.out.println("player : " + player.getName() + "won");
                            boolean won = false;
                            if(player.getName().equals(MainMenuController.getUsername())) {
                                won = true;
                            }
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("game", "snake_and_ladder");
                                jsonObject.put("username", MainMenuController.getUsername());
                                jsonObject.put("won", won);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            socket.emit("single_player_stat", jsonObject.toString());

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(App.getPrimaryStage(), "player : " + player.getName() + "won", 1500, 500, 500);
                                    try {
                                        App.setRoot("main_menu");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }

                    }
                }, 1500);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        checkForSnakeOrLadder(x);
                    }
                });

            }
        }).start();

    }

    private void addPlayers() {

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            SnakeAndLadderPlayer player;
            if (i == 0) {
                player = new SnakeAndLadderPlayer("red", MainMenuController.getUsername());
                player.start();
                game_board.getChildren().add(player.getCircle());

                players.add(player);
            } else if (i == 1) {
                player = new SnakeAndLadderPlayer("blue", "player1");
                player.start();
                game_board.getChildren().add(player.getCircle());
                players.add(player);
            } else if (i == 2) {
                player = new SnakeAndLadderPlayer("green", "player2");
                player.start();
                game_board.getChildren().add(player.getCircle());

                players.add(player);
            } else {
                player = new SnakeAndLadderPlayer("yellow", "player3");
                player.start();
                game_board.getChildren().add(player.getCircle());

                players.add(player);
            }

            leaderBoardsList.add(player);
        }

        leaderBoard.setItems(leaderBoardsList);
    }


}
