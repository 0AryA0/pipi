package org.example.AfterLogin.Games.TicTacToe;

import com.jfoenix.controls.JFXButton;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Paint;
import org.example.AfterLogin.MainMenuController;
import org.example.App;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class TicTacToeSingle implements Initializable {

    @FXML
    private JFXButton b11;

    @FXML
    private JFXButton b12;

    @FXML
    private JFXButton b13;

    @FXML
    private JFXButton b21;

    @FXML
    private JFXButton b22;

    @FXML
    private JFXButton b23;

    @FXML
    private JFXButton b31;

    @FXML
    private JFXButton b32;

    @FXML
    private JFXButton b33;

    @FXML
    private JFXButton back;

    @FXML
    private JFXButton restart;

    private ArrayList<TicTile> tiles;

    private boolean turn;
    private char board[][];
    private Socket socket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        board = new char[][]{{'_', '_', '_'},
                {'_', '_', '_'},
                {'_', '_', '_'}};


        tiles = new ArrayList<>();
        socket = App.getSocket();

        TicTacToeMultiplayer.addTiles(tiles, b11, b12, b13, b21, b22, b23, b31, b32, b33);

        turn = true;

        Objects.requireNonNull(TicTile.get(11)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(11)).isChecked()) {
                board[0][0] = 'x';
                TicTile.get(11).setText("X");
                TicTile.getTile(11).setChecked(true);
                TicTile.get(11).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Objects.requireNonNull(TicTile.get(12)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(12)).isChecked()) {
                board[0][1] = 'x';
                TicTile.get(12).setText("X");
                TicTile.getTile(12).setChecked(true);
                TicTile.get(12).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Objects.requireNonNull(TicTile.get(13)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(13)).isChecked()) {
                board[0][2] = 'x';
                TicTile.get(13).setText("X");
                TicTile.getTile(13).setChecked(true);
                TicTile.get(13).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Objects.requireNonNull(TicTile.get(21)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(21)).isChecked()) {
                board[1][0] = 'x';
                TicTile.get(21).setText("X");
                TicTile.get(21).setTextFill(Paint.valueOf("#85a1ff"));
                TicTile.getTile(21).setChecked(true);
                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Objects.requireNonNull(TicTile.get(22)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(22)).isChecked()) {
                board[1][1] = 'x';
                TicTile.get(22).setText("X");
                TicTile.getTile(22).setChecked(true);
                TicTile.get(22).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Objects.requireNonNull(TicTile.get(23)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(23)).isChecked()) {
                board[1][2] = 'x';
                TicTile.get(23).setText("X");
                TicTile.getTile(23).setChecked(true);
                TicTile.get(23).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Objects.requireNonNull(TicTile.get(31)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(31)).isChecked()) {
                board[2][0] = 'x';
                TicTile.get(31).setText("X");
                TicTile.getTile(31).setChecked(true);
                TicTile.get(31).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Objects.requireNonNull(TicTile.get(32)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(32)).isChecked()) {
                board[2][1] = 'x';
                TicTile.get(32).setText("X");
                TicTile.getTile(32).setChecked(true);
                TicTile.get(32).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Objects.requireNonNull(TicTile.get(33)).setOnAction(actionEvent ->  {
            if (turn && !Objects.requireNonNull(TicTile.getTile(33)).isChecked()) {
                board[2][2] = 'x';
                TicTile.get(33).setText("X");
                TicTile.getTile(33).setChecked(true);
                TicTile.get(33).setTextFill(Paint.valueOf("#85a1ff"));

                try {
                    if (!checkWin() && !checkDraw()) {
                        botTurn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        back.setOnAction(actionEvent -> {
            try {
                TicTile.remove();
                App.setRoot("main_menu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        restart.setOnAction(actionEvent ->  {
            restart();
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (checkDraw() || checkWin()) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    restart();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void restart() {
        for (TicTile tile : tiles) {
            tile.setChecked(false);
            JFXButton button = tile.getButton();
            button.setText("");
            tile.setButton(button);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '_';
            }
        }
    }

    private void botTurn() {
        Move bestMove = findBestMove(board);
        board[bestMove.row][bestMove.col] = 'o';
        int number = Integer.parseInt((bestMove.row + 1 )+ "" + (bestMove.col + 1));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf("%c ", board[i][j]);
            }
            System.out.println();
        }
        System.out.println(number);

        TicTile.get(number).setText("O");
        TicTile.getTile(number).setChecked(true);
        TicTile.get(number).setTextFill(Paint.valueOf("yellow"));


        turn = true;
    }

    private boolean checkWin() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", MainMenuController.getUsername());
        jsonObject.put("game", "tic_tac_toe");

        for (int i = 0; i < 3; i++) {
            if (board[0][i] == board[1][i]  && board[1][i] == board[2][i] && board[1][i] != '_') {
                if (board[1][i] == 'x') {
                    System.out.println("you won");
                    jsonObject.put("won", true);
                }
                if (board[1][i] == 'o') {
                    System.out.println("you lost");
                    jsonObject.put("won", false);
                }

                socket.emit("single_player_stat", jsonObject.toString());

                return true;
            }
            if (board[i][0] == board[i][1]  && board[i][1] == board[i][2] && board[i][1] != '_') {
                if (board[i][1] == 'x') {
                    System.out.println("you won");
                    jsonObject.put("won", true);
                }
                if (board[i][1] == 'o') {
                    System.out.println("you lost");
                    jsonObject.put("won", false);

                }
                socket.emit("single_player_stat", jsonObject.toString());

                return true;
            }
        }

        if ( ((board[0][0] == board[1][1] && board[1][1] == board[2][2])
                || (board[0][2] == board[1][1] && board[1][1] == board[2][0])) && board[1][1] != '_') {

            if (board[1][1] == 'x') {
                System.out.println("you won");
                jsonObject.put("win", true);
            }
            if (board[1][1] == 'o') {
                System.out.println("you lost");
                jsonObject.put("win", false);
            }
            socket.emit("single_player_stat", jsonObject.toString());

            return true;
        }

        return false;
    }

    private boolean checkDraw() throws JSONException {
        int number = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 'x' || board[i][j] == 'o') {
                    number++;
                }
            }
        }

        return number == 9 && !checkWin();
    }

    static class Move
    {
        int row, col;
    };

    static char player = 'o', opponent = 'x';

    // This function returns true if there are moves
// remaining on the board. It returns false if
// there are no moves left to play.
    static Boolean isMovesLeft(char board[][])
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == '_')
                    return true;
        return false;
    }

    // This is the evaluation function as discussed
// in the previous article ( http://goo.gl/sJgv68 )
    static int evaluate(char b[][])
    {
        // Checking for Rows for X or O victory.
        for (int row = 0; row < 3; row++)
        {
            if (b[row][0] == b[row][1] &&
                    b[row][1] == b[row][2])
            {
                if (b[row][0] == player)
                    return +10;
                else if (b[row][0] == opponent)
                    return -10;
            }
        }

        // Checking for Columns for X or O victory.
        for (int col = 0; col < 3; col++)
        {
            if (b[0][col] == b[1][col] &&
                    b[1][col] == b[2][col])
            {
                if (b[0][col] == player)
                    return +10;

                else if (b[0][col] == opponent)
                    return -10;
            }
        }

        // Checking for Diagonals for X or O victory.
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2])
        {
            if (b[0][0] == player)
                return +10;
            else if (b[0][0] == opponent)
                return -10;
        }

        if (b[0][2] == b[1][1] && b[1][1] == b[2][0])
        {
            if (b[0][2] == player)
                return +10;
            else if (b[0][2] == opponent)
                return -10;
        }

        // Else if none of them have won then return 0
        return 0;
    }

    // This is the minimax function. It considers all
// the possible ways the game can go and returns
// the value of the board
    static int minimax(char board[][],
                       int depth, Boolean isMax)
    {
        int score = evaluate(board);

        // If Maximizer has won the game
        // return his/her evaluated score
        if (score == 10)
            return score;

        // If Minimizer has won the game
        // return his/her evaluated score
        if (score == -10)
            return score;

        // If there are no more moves and
        // no winner then it is a tie
        if (isMovesLeft(board) == false)
            return 0;

        // If this maximizer's move
        if (isMax)
        {
            int best = -1000;

            // Traverse all cells
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    // Check if cell is empty
                    if (board[i][j]=='_')
                    {
                        // Make the move
                        board[i][j] = player;

                        // Call minimax recursively and choose
                        // the maximum value
                        best = Math.max(best, minimax(board,
                                depth + 1, !isMax));

                        // Undo the move
                        board[i][j] = '_';
                    }
                }
            }
            return best;
        }

        // If this minimizer's move
        else
        {
            int best = 1000;

            // Traverse all cells
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    // Check if cell is empty
                    if (board[i][j] == '_')
                    {
                        // Make the move
                        board[i][j] = opponent;

                        // Call minimax recursively and choose
                        // the minimum value
                        best = Math.min(best, minimax(board,
                                depth + 1, !isMax));

                        // Undo the move
                        board[i][j] = '_';
                    }
                }
            }
            return best;
        }
    }

    // This will return the best possible
    // move for the player
    static Move findBestMove(char board[][])
    {
        int bestVal = -1000;
        Move bestMove = new Move();
        bestMove.row = -1;
        bestMove.col = -1;

        // Traverse all cells, evaluate minimax function
        // for all empty cells. And return the cell
        // with optimal value.
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                // Check if cell is empty
                if (board[i][j] == '_')
                {
                    // Make the move
                    board[i][j] = player;

                    // compute evaluation function for this
                    // move.
                    int moveVal = minimax(board, 0, false);

                    // Undo the move
                    board[i][j] = '_';

                    // If the value of the current move is
                    // more than the best value, then update
                    // best/
                    if (moveVal > bestVal)
                    {
                        bestMove.row = i;
                        bestMove.col = j;
                        bestVal = moveVal;
                    }
                }
            }
        }

        System.out.printf("The value of the best Move " +
                "is : %d\n\n", bestVal);

        return bestMove;
    }

}
