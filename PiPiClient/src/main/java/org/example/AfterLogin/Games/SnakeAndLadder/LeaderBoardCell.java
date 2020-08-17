package org.example.AfterLogin.Games.SnakeAndLadder;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.example.App;

import java.io.IOException;

public class LeaderBoardCell extends JFXListCell<SnakeAndLadderPlayer> {

    FXMLLoader loader;

    @FXML
    private Circle color;

    @FXML
    private Label username;

    @FXML
    private Label position;

    @FXML
    private AnchorPane pane;

    @Override
    protected void updateItem(SnakeAndLadderPlayer item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {
            setText(null);
            setGraphic(null);
        }
        else {

            if(loader == null) {
                loader = new FXMLLoader(App.class.getResource("leader_board_cell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            username.setText(item.getName());
            color.setFill(Paint.valueOf(item.getColor()));
            position.setText(item.current() + "");

            setText(null);
            setGraphic(pane);
        }
    }
}
