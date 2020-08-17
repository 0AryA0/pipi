package org.example.AfterLogin;

import javafx.scene.image.Image;

public class Game {
    private String name;
    private Image gameImage;

    public Game(String name) {
        this.name = name;
    }

    public Game(String name, Image gameImage) {
        this.name = name;
        this.gameImage = gameImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getGameImage() {
        return gameImage;
    }

    public void setGameImage(Image gameImage) {
        this.gameImage = gameImage;
    }
}
