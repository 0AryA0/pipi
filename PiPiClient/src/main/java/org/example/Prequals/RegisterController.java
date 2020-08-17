package org.example.Prequals;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.example.App;
import org.example.Toast;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField question;
    @FXML
    private JFXTextField answer;
    @FXML
    private JFXButton register;
    @FXML
    private Label logHere;

    private Socket socket;

    private boolean kill = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        socket = App.getSocket();
        /*
        thread to check if a field is valid, turn unFocus color to default
        would be handful after unsuccessful registration...
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!kill) {
                    if(isUsernameValid()) {
                        username.setUnFocusColor(Paint.valueOf("4d4d4d"));
                    }
                    if(isQuestionValid()) {
                        question.setUnFocusColor(Paint.valueOf("4d4d4d"));
                    }
                    if(isAnswerValid()) {
                        answer.setUnFocusColor(Paint.valueOf("4d4d4d"));
                    }
                    if(isPasswordValid()) {
                        password.setUnFocusColor(Paint.valueOf("4d4d4d"));
                    }
                    if(isEmailValid(email.getText())) {
                        email.setUnFocusColor(Paint.valueOf("4d4d4d"));
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        /*
        check email and password validation status right when user is typing

         */

        email.setOnKeyReleased(mouseEvent ->  {
            checkEmail();
        });
        email.setOnMouseClicked(mouseEvent ->  {
            checkEmail();
        });

        password.setOnKeyReleased(keyEvent ->  {
            checkPassword();
        });

        password.setOnMouseClicked(mouseEvent ->  {
            checkPassword();
        });

        // pn register button clicked

        register.setOnAction(actionEvent ->  {
            try {
                registerAction();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        /*
        a listener to get duplicated username event from server
         */
        socket.on("get_reg_confirmation", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {

                if(objects[0].toString().equals("duplicate_username")) {
                    username.setUnFocusColor(Paint.valueOf("red"));
                    username.setText("");
                    username.setPromptText("username already exists");
                }

                if(objects[0].toString().equals("duplicated_email")) {
                    email.setUnFocusColor(Paint.valueOf("red"));
                    email.setText("");
                    email.setPromptText("email already exists");
                }

                if (objects[0].toString().equals("register_successful")) {

                   success();

                    try {
                        Thread.sleep(2000);
                        socket.off();
                        App.setRoot("login");

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        logHere.setOnMouseClicked(mouseEvent ->  {
            try {

                socket.off();
                kill = true;
                App.setRoot("login");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void success() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getPrimaryStage(), "registered", 1500, 500, 500);
            }
        });

    }

    private void checkEmail () {
        if(isEmailValid(email.getText())) {
            email.setFocusColor(Paint.valueOf("#4059a9"));
        }
        else {
            email.setFocusColor(Paint.valueOf("red"));

        }
    }

    private void checkPassword() {
        if(isPasswordValid()) {
           password.setFocusColor(Paint.valueOf("#4059a9"));
        }
        else {
            password.setFocusColor(Paint.valueOf("red"));

        }
    }

    private void handleInvalidFields () {
        if(!isUsernameValid()) {
            username.setText("");
            username.setPromptText("please enter a valid username");
            username.setUnFocusColor(Paint.valueOf("red"));
        }

        if(!isQuestionValid()) {
            question.setText("");
            question.setPromptText("please enter a valid question");
            question.setUnFocusColor(Paint.valueOf("red"));
        }

        if(!isAnswerValid()) {
            answer.setText("");
            answer.setPromptText("please enter a valid answer");
            answer.setUnFocusColor(Paint.valueOf("red"));
        }

        if(!isPasswordValid()) {
            password.setText("");
            password.setPromptText("please enter a valid password");
            password.setUnFocusColor(Paint.valueOf("red"));
        }

        if(!isEmailValid(email.getText())) {
            email.setText("");
            email.setPromptText("please enter a valid email");
            email.setUnFocusColor(Paint.valueOf("red"));
        }
    }


    private void registerAction() throws JSONException {
        if(isPasswordValid() && isEmailValid(email.getText()) && isAnswerValid()
                && isQuestionValid() && isUsernameValid()) {
            emitRegister();
            System.out.println("sending");
        }
        else {
            handleInvalidFields();
        }
    }

    private boolean isUsernameValid () {
        return username.getText().length() > 0;
    }

    private boolean isQuestionValid () {
        return question.getText().length() > 0;
    }

    private boolean isAnswerValid () {
        return answer.getText().length() > 0;
    }

    private boolean isPasswordValid () {
        return password.getText().length() > 8;
    }

    public boolean isEmailValid (String email) {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void emitRegister () throws JSONException {
        JSONObject information = new JSONObject();

        information.put("username", username.getText());
        information.put("password", password.getText());
        information.put("email", email.getText());
        information.put("question", question.getText());
        information.put("answer", answer.getText());

        socket.emit("register_event", information.toString());
    }



}
