package org.example.AfterLogin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.paint.Paint;
import org.example.App;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class AddGroupController implements Initializable {

    @FXML
    private JFXTextField group_name_join;

    @FXML
    private JFXButton join;

    @FXML
    private JFXTextField group_name_create;

    @FXML
    private JFXButton create;

    @FXML
    private JFXListView<User> list_view;

    private static ObservableList<User> users;

    private Socket socket;

    public AddGroupController() {

        socket = App.getSocket();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        users = MainMenuController.getUsers();

        list_view.setCellFactory(piPiUsersListView -> new CreateGroupListCell());

        list_view.setItems(users);
        list_view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        create.setOnAction(actionEvent ->  {
            if (group_name_create.getText().equals("")) {
                group_name_create.setPromptText("Enter a valid name");
                group_name_create.setUnFocusColor(Paint.valueOf("red"));
            }
            else {
                group_name_create.setUnFocusColor(Paint.valueOf("4d4d4d"));
                ObservableList<User> selected = list_view.getSelectionModel().getSelectedItems();
                ArrayList<String> selectedArray = new ArrayList<>();

                selectedArray.add(MainMenuController.getUsername());
                for(User user : selected) {
                    selectedArray.add(user.getUsername());
                }

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("users", selectedArray);
                    jsonObject.put("group_name", group_name_create.getText());
                    System.out.println(jsonObject);
                    socket.emit("create_group", jsonObject.toString(), new Ack() {
                        @Override
                        public void call(Object... objects) {
                            String status = objects[0].toString();
                            System.out.println(status);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        join.setOnAction(actionEvent ->  {
            if (group_name_join.getText().equals("")) {
                group_name_join.setPromptText("Enter a valid name");
                group_name_join.setUnFocusColor(Paint.valueOf("red"));
            }
            else {
                group_name_join.setUnFocusColor(Paint.valueOf("4d4d4d"));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", MainMenuController.getUsername());
                    jsonObject.put("group_name", group_name_join.getText());

                    socket.emit("join_group", jsonObject.toString(), new Ack() {
                        @Override
                        public void call(Object... objects) {
                            String status = objects[0].toString();
                            System.out.println(status);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
