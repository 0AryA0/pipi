package org.example.AfterLogin.Games.SnakeAndLadder;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.example.AfterLogin.Games.Player;

import java.util.ArrayList;
import java.util.Objects;

public class SnakeAndLadderPlayer extends Player {

    private static ArrayList<SnakeAndLadderPlayer> players = new ArrayList<>();
    private int currentPlace;
    private String color;
    private Circle circle;

    public SnakeAndLadderPlayer(String color, String name) {
        super(name);
        this.circle = new Circle();
        this.circle.setFill(Paint.valueOf(color));
        this.color = color;
        this.circle.setRadius(25);
        players.add(this);
        currentPlace = 0;
    }

    public void setColor(String color) {
        this.color = color;
        this.circle.setFill(Paint.valueOf(color));
    }

    public void start() {
        this.circle.setCenterX(Objects.requireNonNull(GameTile.get(1)).centerX());
        this.circle.setCenterY(Objects.requireNonNull(GameTile.get(1)).centerY());
        currentPlace = 1;
    }

    public void goTo(int number) {

        double destX = Objects.requireNonNull(GameTile.get(number)).centerX();
        double destY = Objects.requireNonNull(GameTile.get(number)).centerY();

        this.circle.setCenterX(destX);
        this.circle.setCenterY(destY);
        currentPlace = number;
    }

    public int current(){
        return  currentPlace;
    }

    public String getColor() {
        return color;
    }

    public Circle getCircle() {
        return circle;
    }
}
