package org.example.AfterLogin.Games.TicTacToe;

import com.jfoenix.controls.JFXButton;

import java.util.ArrayList;

public class TicTile {
    private boolean isChecked;
    private JFXButton button;
    private int place;

    private static ArrayList<TicTile> tiles = new ArrayList<>();

    public TicTile(JFXButton button, int place) {
        isChecked = false;
        this.button = button;
        this.place = place;
        tiles.add(this);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public JFXButton getButton() {
        return button;
    }

    public void setButton(JFXButton button) {
        this.button = button;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }



    public static JFXButton get(int number) {
        for (TicTile tile : tiles) {
            if (tile.place == number) {
                return tile.getButton();
            }
        }
        return null;
    }

    public static TicTile getTile (int number) {
        for (TicTile tile : tiles) {
            if (tile.place == number) {
                return tile;
            }
        }
        return null;
    }

    public static void remove() {
        tiles.removeAll(tiles);
    }

    public static ArrayList<TicTile> getTiles() {
        return tiles;
    }

    public static void setTiles(ArrayList<TicTile> tiles) {
        TicTile.tiles = tiles;
    }
}
