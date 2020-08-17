package org.example.AfterLogin;

import com.jfoenix.controls.*;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MessageController implements Initializable {

    @FXML
    private JFXListView<HBox> list_view;
    private String clickedItem;

    @FXML
    private JFXTextField message;

    @FXML
    private JFXButton send;

    boolean isGroup;
    private Socket socket;

    public MessageController() {

        isGroup = MainMenuController.isGroup();
        socket = App.getSocket();
        clickedItem = MainMenuController.getClickedName();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        JSONObject jsonObject = new JSONObject();

        //tell server that we saw unread messages
        if (isGroup) {
            try {
                jsonObject.put("group_name", clickedItem);
                jsonObject.put("is_group", true);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                jsonObject.put("username", MainMenuController.getUsername());
                jsonObject.put("friend_name", MainMenuController.getClickedName());
                jsonObject.put("is_group", false);

                JSONObject json = new JSONObject();
                json.put("receiver", MainMenuController.getUsername());
                json.put("sender", MainMenuController.getClickedName());
                socket.emit("update_unread_messages", json.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //get all messages from sever
        socket.emit("give_messages", jsonObject.toString(), new Ack() {
            @Override
            public void call(Object... objects) {
                String jsonString = objects[0].toString();
                try {
                    JSONObject json = new JSONObject(jsonString);
                    JSONArray jsonArray = json.getJSONArray("messages");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jsonS = jsonArray.getString(i);

                        JSONObject object = new JSONObject(jsonS);
                        TextFlow label = new TextFlow();
                        Text text = new Text(object.getString("message"));

                        label.getChildren().add(text);
                        label.setMaxWidth(275);
                        text.setFont(Font.font("DejaVu", 16));


                        HBox hBox = new HBox();
                        hBox.getChildren().add(label);

                        if (isGroup) {
                            if (object.getString("sender").equals(MainMenuController.getUsername())) {
                                hBox.setAlignment(Pos.CENTER_RIGHT);
                                label.getStyleClass().add("messages");

                            } else {
                                hBox.setAlignment(Pos.CENTER_LEFT);
                                label.getStyleClass().add("friend");

                            }
                        } else {
                            if (object.getString("sender").equals(clickedItem)) {
                                hBox.setAlignment(Pos.CENTER_LEFT);
                                label.getStyleClass().add("friend");

                            } else {
                                hBox.setAlignment(Pos.BASELINE_RIGHT);
                                label.getStyleClass().add("messages");

                            }
                        }


                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                list_view.getItems().add(hBox);
                            }
                        });

                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (list_view.getItems().size() != 0) {
                                list_view.scrollTo(list_view.getItems().size() - 1);

                            }
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //handle online messages
        onlineMessage();

        send.setOnAction(actionEvent -> {
            String messageText = message.getText();

            JSONObject json = new JSONObject();
            try {
                json.put("username", MainMenuController.getUsername());
                json.put("friendName", MainMenuController.getClickedName());
                json.put("message", messageText);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //send message to the server
            socket.emit("message_sent_from_user", json.toString());
        });

        message.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                send();
            }
        });
        send.setOnAction(actionEvent -> {
            send();
        });

    }

    private void send() {
        String messageText = message.getText();
        if (!messageText.equals("")) {
            JSONObject json = new JSONObject();
            try {
                json.put("is_group", isGroup);
                json.put("username", MainMenuController.getUsername());
                json.put("friendName", MainMenuController.getClickedName());
                json.put("message", messageText);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("message_sent_from_user", json.toString());
            message.setText("");
        }

    }

    private void onlineMessage() {
        socket.on("message_sent_when_online", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                try {
                    String clickedUser = MainMenuController.getClickedName();
                    JSONObject message = new JSONObject(objects[0].toString());


                    Label label = new Label(message.getString("message"));

                    HBox hBox = new HBox();
                    hBox.getChildren().add(label);

                    if (isGroup) {
                        if (message.getString("sender").equals(MainMenuController.getUsername())) {
                            hBox.setAlignment(Pos.CENTER_RIGHT);
                            label.getStyleClass().add("messages");

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    list_view.getItems().add(hBox);

                                }
                            });

                        } else if (message.getString("receiver").equals(MainMenuController.getClickedName())){
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            label.getStyleClass().add("friend");

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    list_view.getItems().add(hBox);

                                }
                            });

                        }

                    } else {
                        if (message.getString("sender").equals(clickedItem)) {
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            label.getStyleClass().add("friend");

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    list_view.getItems().add(hBox);

                                }
                            });
                            if (message.getString("sender").equals(MainMenuController.getClickedName())) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("receiver", message.getString("receiver"));
                                jsonObject.put("sender", message.getString("sender"));
                                socket.emit("update_unread_messages", jsonObject.toString());

                            }

                        } else if (message.getString("receiver").equals(MainMenuController.getClickedName())){
                            hBox.setAlignment(Pos.BASELINE_RIGHT);
                            label.getStyleClass().add("messages");

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    list_view.getItems().add(hBox);

                                }
                            });


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
