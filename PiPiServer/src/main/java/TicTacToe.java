import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;

public class TicTacToe implements Runnable{

    SocketIOServer server;
    Lobby lobby;
    private ArrayList<Player> players;
    private String room;
    private static Connection connection;
    private char[][] board;
    boolean turn = true;

    public TicTacToe(SocketIOServer server, Lobby lobby) {
        connection = Server.getConnection();
        board = new char[][]{{'_', '_', '_'},
                {'_', '_', '_'},
                {'_', '_', '_'}};
        this.lobby = lobby;
        this.server = server;
        players = new ArrayList<>();
        room = lobby.getLobbyName();
        for (String player : lobby.getPlayers()) {
            players.add(new Player(player));
        }
    }

    @Override
    public void run() {
        for (SocketIOClient client : server.getAllClients()) {
            for (Player player : players) {
                String username = player.getName();
                if (username.equals(client.get("username"))) {
                    client.joinRoom(room);
                }
            }
        }
        server.getRoomOperations(room).sendEvent("start_game_tic", players.get(0).getName());

        turnEvent();
        exitEvent();
        message();

        while(true) {
            System.out.println("running");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void exitEvent() {
        server.addEventListener("exit_from_game_tic", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String s, AckRequest ackRequest) throws Exception {
                if (server.getRoomOperations(room).getClients().contains(client)) {
                    setWinOrLose(client.get("username"), false);
                    if (client.get("username").equals(players.get(0).getName())) {
                        setWinOrLose(players.get(1).getName(), true);
                        server.getRoomOperations(room).sendEvent("win_event_tic", players.get(1).getName());
                        removeLobby();

                    }
                    else {
                        setWinOrLose(players.get(0).getName(), true);
                        server.getRoomOperations(room).sendEvent("win_event_tic", players.get(0).getName());
                        removeLobby();

                    }

                }
            }
        });
    }

    private void message() {
        server.addEventListener("message_tic", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String message, AckRequest ackRequest) throws Exception {
                if (server.getRoomOperations(room).getClients().contains(client)) {
                    server.getRoomOperations(room).sendEvent("message_tic", message);
                }
            }
        });
    }

    private void turnEvent() {
        server.addEventListener("turn", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                if (server.getRoomOperations(room).getClients().contains(client)) {
                    JSONObject jsonObject = new JSONObject(data);

                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            System.out.println(board[i][j]);
                        }
                    }

                    char ch;
                    String username = jsonObject.getString("username");
                    if (username.equals(players.get(0).getName())) {
                        ch = 'x';
                    }
                    else {
                        ch = 'o';
                    }
                    int r = jsonObject.getInt("r") - 1;
                    int c = jsonObject.getInt("c") - 1;

                    board[r][c] = ch;
                    turn = !turn;

                    String playerTurn = "";
                    String sign = "";
                    if (turn) {
                        playerTurn = players.get(0).getName();
                        sign = "X";
                    }
                    else {
                        playerTurn = players.get(1).getName();
                        sign = "O";
                    }

                    jsonObject.put("turn", playerTurn);
                    jsonObject.put("sign", sign);

                    if (!checkWin() || !checkDraw()) {
                        server.getRoomOperations(room).sendEvent("turn_back", jsonObject.toString());
                    }
                }

            }
        });
    }

    private boolean checkWin() throws JSONException, SQLException {
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == board[1][i]  && board[1][i] == board[2][i] && board[1][i] != '_') {
                if (board[1][i] == 'x') {
                    server.getRoomOperations(room).sendEvent("win_event_tic", players.get(0).getName());
                    setWinOrLose(players.get(0).getName(), true);
                    setWinOrLose(players.get(1).getName(), false);
                    removeLobby();


                }
                if (board[1][i] == 'o') {
                    server.getRoomOperations(room).sendEvent("win_event_tic", players.get(1).getName());

                    setWinOrLose(players.get(0).getName(), false);
                    setWinOrLose(players.get(1).getName(), true);
                    removeLobby();

                }

                return true;
            }
            if (board[i][0] == board[i][1]  && board[i][1] == board[i][2] && board[i][1] != '_') {
                if (board[i][1] == 'x') {
                    server.getRoomOperations(room).sendEvent("win_event_tic", players.get(0).getName());

                    setWinOrLose(players.get(0).getName(), true);
                    setWinOrLose(players.get(1).getName(), false);
                    removeLobby();

                }
                if (board[i][1] == 'o') {
                    server.getRoomOperations(room).sendEvent("win_event_tic", players.get(1).getName());

                    setWinOrLose(players.get(0).getName(), false);
                    setWinOrLose(players.get(1).getName(), true);
                    removeLobby();

                }

                return true;
            }
        }

        if ( ((board[0][0] == board[1][1] && board[1][1] == board[2][2])
                || (board[0][2] == board[1][1] && board[1][1] == board[2][0])) && board[1][1] != '_') {

            if (board[1][1] == 'x') {
                server.getRoomOperations(room).sendEvent("win_event_tic", players.get(0).getName());
                setWinOrLose(players.get(0).getName(), true);
                setWinOrLose(players.get(1).getName(), false);
                removeLobby();


            }
            if (board[1][1] == 'o') {
                server.getRoomOperations(room).sendEvent("win_event_tic", players.get(1).getName());
                setWinOrLose(players.get(0).getName(), false);
                setWinOrLose(players.get(1).getName(), true);
                removeLobby();

            }

            return true;
        }

        return false;
    }

    private void removeLobby() {
        for (SocketIOClient client : server.getRoomOperations(room).getClients()) {
            client.leaveRoom(room);
        }
        LobbyHandler.remove(lobby);
        lobby.getThread().stop();
        lobby = null;
    }


    private void setWinOrLose(String username, boolean win) throws SQLException {
        String sql = "SELECT * from users where username ='" + username + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        JSONObject json = new JSONObject(resultSet.getString(8));
        JSONObject inner = json.getJSONObject("tic_tac_toe");


        if (win) {
            int number = inner.getInt("win_multi");
            number += 1;
            inner.put("win_multi", number);
        } else {
            int number = inner.getInt("lose_multi");
            number += 1;
            inner.put("lose_multi", number);
        }

        json.put("snake_and_ladder", inner);

        sql = "UPDATE users " + "SET win_lose = ? " + "WHERE username=?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(2, username);
        preparedStatement.setString(1, json.toString());
        preparedStatement.executeUpdate();

    }

    private boolean checkDraw() throws JSONException, SQLException {
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
}
