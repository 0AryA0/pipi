package org.example.AfterLogin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.example.App;

import java.io.IOException;

public class GameCell extends JFXListCell<Game> {

    FXMLLoader loader;

    @FXML
    private ImageView image;

    @FXML
    private Label game_name;

    @FXML
    private JFXButton play;



    @FXML
    private AnchorPane pane;

    @Override
    protected void updateItem(Game game, boolean b) {
        super.updateItem(game, b);

        if(b || game == null) {
            setText(null);
            setGraphic(null);
        }
        else {

            if(loader == null) {
                loader = new FXMLLoader(App.class.getResource("game_cell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            game_name.setText(game.getName());
            game_name.setTextFill(Paint.valueOf("purple"));

            play.setOnAction(actionEvent ->  {
                System.out.println("single clicked " + game_name);
            });

            play.setOnAction(actionEvent ->  {
                if(game.getName().equals("SNAKE AND LADDER")) {
                    try {
                        App.setRoot("snake_and_ladder");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (game.getName().equals("TIC TAC TOE")) {
                    try {
                        App.setRoot("tic_tac_toe");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            image.setImage(game.getGameImage());

            setText(null);
            setGraphic(pane);

        }
    }
}
