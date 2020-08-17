package org.example.AfterLogin.Games.SnakeAndLadder;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class GameTile extends Rectangle {
    private static final int SIZE = 72;
    private static ArrayList<GameTile> gameTiles = new ArrayList<>();

    private int tileNumber;

    public int getTileNumber() {
        return tileNumber;
    }

    public static int getSIZE() {
        return SIZE;
    }

    public GameTile(int tileNumber) {
        this.setWidth(SIZE);
        this.setHeight(SIZE);
        this.tileNumber = tileNumber;
        gameTiles.add(this);
        this.setOnMouseClicked(mouseEvent ->  {
            System.out.println(tileNumber);
        });
    }

    public static GameTile get(int number) {
        for(GameTile tile : gameTiles) {
            if(tile.getTileNumber() == number) {
                return  tile;
            }
        }
        return null;
    }

    public double centerX () {
        return this.getX() + (this.getWidth() / 2);
    }

    public double centerY() {
        return this.getY() + (this.getHeight() / 2);
    }


}
