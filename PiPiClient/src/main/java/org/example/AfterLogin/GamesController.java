package org.example.AfterLogin;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import org.example.App;

import java.net.URL;
import java.util.ResourceBundle;

public class GamesController implements Initializable {


    @FXML
    private JFXListView<Game> games_list_view;

    private ObservableList<Game> games;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        games = FXCollections.observableArrayList();

        games.add(new Game("SNAKE AND LADDER", new Image(App.class.getResource("images/snake.jpg").toExternalForm())));
        games.add(new Game("TIC TAC TOE", new Image(App.class.getResource("images/tictac.png").toExternalForm())));
        //games.add(new Game("pipi"));

        games_list_view.setCellFactory(e -> new GameCell());

        games_list_view.setItems(games);
    }
}
