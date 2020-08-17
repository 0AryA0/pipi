package org.example.AfterLogin;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXRippler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.example.App;

import java.io.IOException;


public class UsersListCell extends JFXListCell<User> {


    FXMLLoader loader;

    @FXML
    Label head_label;
    @FXML
    private Circle online_status;
    @FXML
    private Label unread;

    @FXML
    Label side_label;
    @FXML
    ImageView photo;
    @FXML
    AnchorPane pane;

    @FXML
    JFXRippler rippler;

    @FXML
    HBox hBox;


    @Override
    protected void updateItem(User piPiUsers, boolean b) {
        super.updateItem(piPiUsers, b);

        if(b || piPiUsers == null) {
            setText(null);
            setGraphic(null);
        }
        else {

            if(loader == null) {
                loader = new FXMLLoader(App.class.getResource("chats_cell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Circle clip = new Circle(26,40,26);
            clip.setFill(Paint.valueOf("red"));
            clip.setStroke(Paint.valueOf("black"));
            photo.setClip(clip);

            head_label.setText(String.valueOf(piPiUsers.getUsername()));
            unread.setText(piPiUsers.getUnread());

            Image image = piPiUsers.getPhoto();
            photo.setImage(image);

            if(piPiUsers.isOnline()) {
                online_status.setFill(Paint.valueOf("green"));
            }
            else {
                online_status.setFill(Paint.valueOf("red"));
            }

            setText(null);
            setGraphic(pane);

        }
    }

}
