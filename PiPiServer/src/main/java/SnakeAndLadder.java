import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class SnakeAndLadder implements Runnable {
    private Lobby lobby;
    private SocketIOServer server;
    private ArrayList<Player> players;
    private String room;
    private static Connection connection;

    int turn = 0;

    public SnakeAndLadder(Lobby lobby, SocketIOServer server) {
        connection = Server.getConnection();
        this.lobby = lobby;
        this.server = server;
        players = new ArrayList<>();
        room = lobby.getLobbyName();
        for (String player : lobby.getPlayers()) {
            players.add(new Player(player, 1));
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
        server.getRoomOperations(room).sendEvent("start_game", players.get(turn));
        handleTurnEvent();
        messageHandler();
        exitEvent();
    }

    private void exitEvent() {
        server.addEventListener("exit_snake_and_ladder", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String username, AckRequest ackRequest) throws Exception {
                if (server.getRoomOperations(room).getClients().contains(client)) {
                    client.leaveRoom(room);
                    players.remove(search(username));
                    lobby.setNumberOfPlayers(lobby.getNumberOfPlayers() - 1);
                    setWinOrLose(username, false);
                    ackRequest.sendAckData("exit");

                    server.getRoomOperations(room).sendEvent("finish", "somebody_left");

                    for (SocketIOClient ioClient : server.getRoomOperations(room).getClients()) {
                        String name = ioClient.get("username");
                        setWinOrLose(name, true);
                        ioClient.leaveRoom(room);
                    }
                    LobbyHandler.remove(lobby);
                    lobby.getThread().stop();
                    lobby = null;
                }
            }
        });
    }

    private void setWinOrLose(String username, boolean win) throws SQLException {
        String sql = "SELECT * from users where username ='" + username + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        JSONObject json = new JSONObject(resultSet.getString(8));
        JSONObject inner = json.getJSONObject("snake_and_ladder");


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


    private void messageHandler() {
        server.addEventListener("snake_and_ladder_message", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                if (server.getRoomOperations(room).getClients().contains(client)) {
                    JSONObject jsonObject = new JSONObject(data);
                    String message = jsonObject.getString("sender") + " : " + jsonObject.getString("message");
                    server.getRoomOperations(room).sendEvent("snake_message", message);
                }
            }
        });
    }

    private void handleTurnEvent() {
        server.addEventListener("turn_event", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String username, AckRequest ackRequest) throws Exception {
                if (server.getRoomOperations(room).getClients().contains(client)) {
                    int diceNumber = (int) (Math.random() * (6 - 1 + 1)) + 1;
                    Player player = search(username);
                    int position = Objects.requireNonNull(player).getPosition();

                    JSONObject jsonObject = new JSONObject();
                    if (position + diceNumber < 100) {
                        player.setPosition(position + diceNumber);
                        jsonObject.put("username", username);
                        jsonObject.put("dice_number", diceNumber);
                        jsonObject.put("new_place", player.getPosition());

                        server.getRoomOperations(room).sendEvent("player_moved", jsonObject.toString());
                    } else if (position + diceNumber == 100) {
                        player.setPosition(position + diceNumber);
                        setWinOrLose(player.getName(), true);

                        players.remove(player);

                        for (Player p : players) {
                            setWinOrLose(p.getName(), false);
                        }
                        jsonObject.put("username", player.getName());
                        jsonObject.put("dice_number", diceNumber);
                        jsonObject.put("new_place", player.getPosition());

                        server.getRoomOperations(room).sendEvent("player_won", jsonObject.toString());

                        LobbyHandler.remove(lobby);
                        for (SocketIOClient ioClient : server.getRoomOperations(room).getClients()) {
                            ioClient.leaveRoom(room);
                        }


                    } else {
                        server.getRoomOperations(room).sendEvent("bigger100", diceNumber);
                    }

                    if (player.getPosition() == 2) {
                        player.setPosition(23);
                    } else if (player.getPosition() == 6) {
                        player.setPosition(45);
                    } else if (player.getPosition() == 20) {
                        player.setPosition(59);
                    } else if (player.getPosition() == 52) {
                        player.setPosition(72);
                    } else if (player.getPosition() == 57) {
                        player.setPosition(96);
                    } else if (player.getPosition() == 71) {
                        player.setPosition(92);
                    } else if (player.getPosition() == 43) {
                        player.setPosition(17);
                    } else if (player.getPosition() == 50) {
                        player.setPosition(5);
                    } else if (player.getPosition() == 56) {
                        player.setPosition(8);
                    } else if (player.getPosition() == 73) {
                        player.setPosition(15);
                    } else if (player.getPosition() == 87) {
                        player.setPosition(49);
                    } else if (player.getPosition() == 84) {
                        player.setPosition(58);
                    } else if (player.getPosition() == 98) {
                        player.setPosition(40);
                    }

                    turnHandle();

                }
            }
        });
    }

    private void turnHandle() {
        turn++;
        int number = lobby.getNumberOfPlayers();


        if (turn == number) {
            turn = 0;
        }
        server.getRoomOperations(room).sendEvent("next_turn", players.get(turn).getName());
    }

    private Player search(String username) {
        for (Player player : players) {
            if (player.getName().equals(username)) {
                return player;
            }
        }
        return null;
    }
}
