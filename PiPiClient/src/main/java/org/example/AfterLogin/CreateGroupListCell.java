package org.example.AfterLogin;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.example.App;

import java.io.IOException;

public class CreateGroupListCell extends JFXListCell<User> {

    FXMLLoader loader;

    @FXML
    Label name;

    @FXML
    ImageView image;
    @FXML
    AnchorPane pane;

    @Override
    protected void updateItem(User piPiUsers, boolean b) {
        super.updateItem(piPiUsers, b);


        if(b || piPiUsers == null) {
            setText(null);
            setGraphic(null);
        }
        else {

            if(loader == null) {
                loader = new FXMLLoader(App.class.getResource("create_group_chat_cell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Circle clip = new Circle(36,24,21);
            clip.setFill(Paint.valueOf("red"));
            clip.setStroke(Paint.valueOf("black"));
            image.setClip(clip);

            name.setText(String.valueOf(piPiUsers.getUsername()));

            Image image = piPiUsers.getPhoto();
            this.image.setImage(image);


            setText(null);
            setGraphic(pane);

        }
    }
}
