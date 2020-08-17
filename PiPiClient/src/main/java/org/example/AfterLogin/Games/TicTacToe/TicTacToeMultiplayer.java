package org.example.AfterLogin.Games.TicTacToe;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Paint;
import org.example.AfterLogin.MainMenuController;
import org.example.App;
import org.example.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TicTacToeMultiplayer implements Initializable {

    @FXML
    private JFXButton b11;

    @FXML
    private JFXButton b12;

    @FXML
    private JFXButton b13;

    @FXML
    private JFXButton b21;

    @FXML
    private JFXButton b22;

    @FXML
    private JFXButton b23;

    @FXML
    private JFXButton b31;

    @FXML
    private JFXButton b32;

    @FXML
    private JFXButton b33;

    @FXML
    private JFXButton exit;

    @FXML
    private JFXListView<String> messages;

    @FXML
    private JFXTextField message;

    @FXML
    private JFXButton send;

    String lobbyName = "";

    private Socket socket;
    private String turn= "";
    boolean isGameStarted = false;
    private char myChar = '_';
    private ArrayList<TicTile> tiles;

    public TicTacToeMultiplayer() {
        socket = App.getSocket();
        tiles = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        w8ToStart();
        buttons();
        handleMessages();
        getTurn();

        socket.on("win_event_tic", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                for (TicTile tile : tiles) {
                    tile.setChecked(false);
                }
                exit();
            }
        });

        exit.setOnAction(actionEvent ->  {
            socket.emit("exit_from_game_tic", "exit");

        });

        send.setOnAction(actionEvent ->  {
            send();
        });
        message.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                send();
            }
        });
    }

    private void handleMessages() {
        socket.on("message_tic", new Emitter.Listener() {
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

    private void send() {
        String pm = message.getText();
        if (!pm.equals("")) {
            socket.emit("message_tic", MainMenuController.getUsername() + " : " + pm);
        }
    }

    private void exit() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    TicTile.remove();
                    socket.off();

                    App.setRoot("main_menu");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    private void getTurn() {
        socket.on("turn_back", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int number = Integer.parseInt(jsonObject.getInt("r") + "" + jsonObject.getInt("c"));
                    System.out.println(number);

                    TicTile.getTile(number).setChecked(true);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sign = jsonObject.getString("sign");
                                if (sign.equals("X")) {
                                    TicTile.get(number).setTextFill(Paint.valueOf("#85a1ff"));
                                }
                                else {
                                    TicTile.get(number).setTextFill(Paint.valueOf("yellow"));
                                }
                                TicTile.get(number).setText(sign);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    turn = jsonObject.getString("turn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void w8ToStart() {
        socket.on("start_game_tic", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                turn = objects[0].toString();
                isGameStarted = true;
                tiles = new ArrayList<>();

                addTiles(tiles, b11, b12, b13, b21, b22, b23, b31, b32, b33);

                if (isMyTurn()) {
                    myChar = 'X';
                }
                else {
                    myChar = 'O';
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(App.getPrimaryStage(), turn + "'s turn", 1500, 500, 500);
                    }
                });
            }
        });
    }

    static void addTiles(ArrayList<TicTile> tiles, JFXButton b11, JFXButton b12, JFXButton b13, JFXButton b21, JFXButton b22, JFXButton b23, JFXButton b31, JFXButton b32, JFXButton b33) {
        tiles.add(new TicTile(b11, 11));
        tiles.add(new TicTile(b12, 12));
        tiles.add(new TicTile(b13, 13));
        tiles.add(new TicTile(b21, 21));
        tiles.add(new TicTile(b22, 22));
        tiles.add(new TicTile(b23, 23));
        tiles.add(new TicTile(b31, 31));
        tiles.add(new TicTile(b32, 32));
        tiles.add(new TicTile(b33, 33));
    }

    private boolean isMyTurn() {
        return MainMenuController.getUsername().equals(turn);
    }

    private void buttons() {

        b11.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(11).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 1);
                    jsonObject.put("c", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());

            }
        });
        b12.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(12).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 1);
                    jsonObject.put("c", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });
        b13.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(13).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 1);
                    jsonObject.put("c", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });
        b21.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(21).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 2);
                    jsonObject.put("c", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });
        b22.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(22).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 2);
                    jsonObject.put("c", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });
        b23.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(23).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 2);
                    jsonObject.put("c", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });
        b31.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(31).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 3);
                    jsonObject.put("c", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });
        b32.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(32).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 3);
                    jsonObject.put("c", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });
        b33.setOnAction(actionEvent ->  {
            if (isGameStarted && isMyTurn() && !TicTile.getTile(33).isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("char", myChar);
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("r", 3);
                    jsonObject.put("c", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("turn", jsonObject.toString());
            }
        });

    }

}
