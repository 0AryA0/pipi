package org.example.AfterLogin.Games.SnakeAndLadder;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import org.example.AfterLogin.Games.Player;

import java.util.ArrayList;

public class SnakeAndLadder {
    protected static final int MAP_SIZE = 720;

    protected static int NUMBER_OF_PLAYERS;

    protected static ArrayList<SnakeAndLadderPlayer> players = new ArrayList<>();

    protected static ArrayList<GameTile> gameTiles = new ArrayList<>();

    protected void makeGameBoard(AnchorPane game_board) {
        players = new ArrayList<>();
        int number = 100;
        for (int i = 0; i < MAP_SIZE / GameTile.getSIZE(); i++) {
            for (int j = 0; j < MAP_SIZE / GameTile.getSIZE(); j++) {
                GameTile gameTile = new GameTile(number);
                gameTile.setX(j * GameTile.getSIZE());
                gameTile.setY(i * GameTile.getSIZE());
                gameTile.setFill(Paint.valueOf("transparent"));

                game_board.getChildren().add(gameTile);
                gameTiles.add(gameTile);
                if (i % 2 == 0) {
                    number--;
                } else {
                    number++;
                }
            }

            if (number % 2 == 0) {
                number -= 9;
            } else {
                number -= 11;
            }
        }
    }

    protected static void resetPlayers() {
        players.removeAll(players);
    }

    protected void checkForSnakeOrLadder(int number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SnakeAndLadderPlayer player = players.get(number);
                int current = player.current();

                //ladder statements
                if (current == 2) {
                    player.goTo(18);
                    try {
                        Thread.sleep(200);
                        player.goTo(23);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                else if (current == 6) {
                    player.goTo(15);
                    try {
                        Thread.sleep(200);
                        player.goTo(26);
                        Thread.sleep(200);
                        player.goTo(36);
                        Thread.sleep(200);
                        player.goTo(45);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                else if (current == 20) {
                    player.goTo(39);
                    try {
                        Thread.sleep(200);
                        player.goTo(59);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                else if (current == 52) {
                    player.goTo(69);
                    try {
                        Thread.sleep(200);
                        player.goTo(72);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if (current == 57) {
                    player.goTo(77);
                    try {
                        Thread.sleep(200);
                        player.goTo(85);
                        Thread.sleep(200);
                        player.goTo(96);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if (current == 71) {
                    player.goTo(89);
                    try {
                        Thread.sleep(200);
                        player.goTo(92);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //snake statements

                else if (current == 43) {
                    player.goTo(44);
                    try {
                        Thread.sleep(200);
                        player.goTo(36);
                        Thread.sleep(200);
                        player.goTo(37);
                        Thread.sleep(200);
                        player.goTo(24);
                        Thread.sleep(200);
                        player.goTo(17);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                else if (current == 50) {
                    player.goTo(49);
                    try {
                        Thread.sleep(150);
                        player.goTo(32);
                        Thread.sleep(150);
                        player.goTo(28);
                        Thread.sleep(150);
                        player.goTo(27);
                        Thread.sleep(150);
                        player.goTo(14);
                        Thread.sleep(150);
                        player.goTo(6);
                        Thread.sleep(150);
                        player.goTo(5);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                else if (current == 56) {
                    player.goTo(54);
                    try {
                        Thread.sleep(150);
                        player.goTo(48);
                        Thread.sleep(150);
                        player.goTo(33);
                        Thread.sleep(150);
                        player.goTo(27);
                        Thread.sleep(150);
                        player.goTo(13);
                        Thread.sleep(150);
                        player.goTo(8);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                else if (current == 73) {
                    player.goTo(74);
                    try {
                        Thread.sleep(150);
                        player.goTo(66);
                        Thread.sleep(150);
                        player.goTo(46);
                        Thread.sleep(150);
                        player.goTo(35);
                        Thread.sleep(150);
                        player.goTo(25);
                        Thread.sleep(150);
                        player.goTo(15);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if (current == 84) {
                    player.goTo(85);
                    try {
                        Thread.sleep(150);
                        player.goTo(75);
                        Thread.sleep(150);
                        player.goTo(76);
                        Thread.sleep(150);
                        player.goTo(64);
                        Thread.sleep(150);
                        player.goTo(63);
                        Thread.sleep(150);
                        player.goTo(58);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if (current == 87) {
                    player.goTo(88);
                    try {
                        Thread.sleep(150);
                        player.goTo(72);
                        Thread.sleep(150);
                        player.goTo(69);
                        Thread.sleep(150);
                        player.goTo(52);
                        Thread.sleep(150);
                        player.goTo(49);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                else if (current == 98) {
                    player.goTo(99);
                    try {
                        Thread.sleep(150);
                        player.goTo(81);
                        Thread.sleep(150);
                        player.goTo(80);
                        Thread.sleep(150);
                        player.goTo(62);
                        Thread.sleep(150);
                        player.goTo(59);
                        Thread.sleep(150);
                        player.goTo(41);
                        Thread.sleep(150);
                        player.goTo(40);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    protected static int getPosition (String username) {
        for (int i = 0; i < players.size(); i++) {
            if (username.equals(players.get(i).getName())) {
                return i;
            }
        }
        return 0;
    }
}
