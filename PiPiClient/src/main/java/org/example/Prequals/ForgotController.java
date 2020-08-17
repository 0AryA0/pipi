package org.example.Prequals;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.example.App;
import org.example.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ForgotController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXButton getInfo;
    @FXML
    private Text question;
    @FXML
    private JFXTextField answer;
    @FXML
    private JFXButton check;
    @FXML
    private Label login;

    private Socket socket;

    String confirmedUsername = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        socket = App.getSocket();

        login.setOnMouseClicked(mouseEvent ->  {
            try {
                socket.off();
                App.setRoot("login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        username.setOnMouseClicked(mouseEvent ->  {
            username.setUnFocusColor(Paint.valueOf("4d4d4d"));
        });

        answer.setOnMouseClicked(mouseEvent ->  {
            username.setUnFocusColor(Paint.valueOf("4d4d4d"));
        });

        //getting question from server
        getInfo.setOnAction(event ->  {
            if(username.getText().equals("")) {
                username.setPromptText("enter a valid username");
                username.setUnFocusColor(Paint.valueOf("red"));
            }
            else {
                socket.emit("forgot_pass_username", username.getText());
            }
        });

        //show question
        socket.on("forgot_user_info", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String status = objects[0].toString();

                if(status.equals("not_found")) {
                    username.setText("");
                    username.setPromptText("username not found");
                    username.setUnFocusColor(Paint.valueOf("red"));
                }
                else {
                    question.setText(status);
                    confirmedUsername = username.getText();

                }
            }
        });

        check.setOnAction(mouseEvent ->  {
            if(confirmedUsername.equals("")) {
                Toast.makeText(App.getPrimaryStage(), "get username info first", 1500, 300, 300);
            }
            else {
                if(answer.getText().equals("")) {
                    answer.setPromptText("enter a valid answer");
                    answer.setUnFocusColor(Paint.valueOf("red"));
                }
                else {
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("username", confirmedUsername);
                        jsonObject.put("answer", answer.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("forgot_answer_info", jsonObject.toString());
                }
            }
        });

        //listener to forgot ans answer
        socket.on("forgot_answer_status", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {

                //System.out.println(objects[0]);
                String status = objects[0].toString();

                if(status.equals("answer_incorrect")) {
                   answer.setText("");
                   answer.setPromptText("incorrect answer");
                   answer.setUnFocusColor(Paint.valueOf("red"));
                }
                else {
                    System.out.println("correct answer");
                    socket.on("password_is", new Emitter.Listener() {
                        @Override
                        public void call(Object... objects) {
                            String password = objects[0].toString();

                            System.out.println(password);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "your password is : " + password, ButtonType.OK);
                                    alert.setTitle("password");
                                    alert.setHeaderText("answer was correct");
                                    alert.setX(App.getPrimaryStage().getX() + (App.getPrimaryStage().getWidth() / 2) - 180);
                                    alert.setY(App.getPrimaryStage().getY() + (App.getPrimaryStage().getHeight() / 2));
                                    alert.showAndWait();

                                    if(alert.getResult() == ButtonType.OK) {
                                        try {
                                            socket.off();
                                            App.setRoot("login");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });



    }
}
