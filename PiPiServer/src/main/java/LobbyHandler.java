import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class LobbyHandler implements Runnable{

    private static ArrayList<Lobby> lobbies;
    private SocketIOServer server;
    public LobbyHandler(SocketIOServer server) {
        this.server = server;
        lobbies = new ArrayList<>();
    }

    @Override
    public void run() {

        createLobby();
        joinLobby();
        giveLobbyPlayers();

        server.addEventListener("lobby_info", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                ArrayList<JSONObject> gameLobbies = searchForGame(data);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lobbies", gameLobbies);
                ackRequest.sendAckData(jsonObject.toString());
            }
        });
    }

    //create lobby if it doesnt exist
    private void createLobby() {
        server.addEventListener("lobby_creation", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);
                String lobbyName = jsonObject.getString("lobby_name");
                String player = jsonObject.getString("username");
                int numberOfPlayers = jsonObject.getInt("number_of_players");
                String gameName = jsonObject.getString("game_name");

                if (!isDuplicated(lobbyName)) {
                    Lobby lobby = new Lobby(lobbyName, gameName, numberOfPlayers);
                    lobby.addPlayer(player);
                    jsonObject = new JSONObject();
                    jsonObject.put("status", "joined_successfully");
                    jsonObject.put("lobby", lobbyName);
                    client.sendEvent("join_lobby_info", jsonObject.toString());

                    lobbies.add(lobby);


                    jsonObject = new JSONObject();
                    jsonObject.put("lobby_name", lobby.getLobbyName());
                    jsonObject.put("current", lobby.getCurrentPlayers());
                    jsonObject.put("number_of_players", lobby.getNumberOfPlayers());
                    jsonObject.put("game_name", lobby.getGameName());

                    for(SocketIOClient socketIOClient : server.getAllClients()) {
                        socketIOClient.sendEvent("lobby_created_while_online", jsonObject.toString());
                    }
                    System.out.println(jsonObject.toString());
                }

            }
        });
    }

    //return true if a lobby already exists
    private boolean isDuplicated(String name) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    //return lobbies with the specific game
    private ArrayList<JSONObject> searchForGame(String gameName) {
        ArrayList<JSONObject> returnLobby = new ArrayList<>();
        for(Lobby lobby : lobbies) {
            if(lobby.getGameName().equals(gameName) && !lobby.isGameStarted()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lobby_name", lobby.getLobbyName());
                jsonObject.put("current", lobby.getCurrentPlayers());
                jsonObject.put("number_of_players", lobby.getNumberOfPlayers());
                jsonObject.put("current", lobby.getCurrentPlayers());
                System.out.println(jsonObject);
                returnLobby.add(jsonObject);

            }
        }
        return returnLobby;
    }

    //listen to join event of the game lobbies
    private void joinLobby() {
        server.addEventListener("join_lobby", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);
                String username = jsonObject.getString("username");
                String gameName = jsonObject.getString("game_name");
                String lobbyName = jsonObject.getString("lobby_name");

                for (Lobby lobby : lobbies) {
                    if (lobby.getGameName().equals(gameName) && lobby.getLobbyName().equals(lobbyName) &&
                            lobby.getCurrentPlayers() < lobby.getNumberOfPlayers()) {

                        ArrayList<String> players = lobby.getPlayers();
                        boolean playerFound = false;

                        //tell client that this lobby has stated the game and failed to connect
                        if(lobby.isGameStarted()) {
                            jsonObject = new JSONObject();
                            jsonObject.put("status", "failed");
                            client.sendEvent("join_lobby_info", jsonObject.toString());
                        }
                        else {
                            for (String player : players) {
                                if (player.equals(username)) {
                                    playerFound = true;
                                    jsonObject = new JSONObject();
                                    jsonObject.put("status", "already_joined");
                                    jsonObject.put("lobby", lobbyName);
                                    client.sendEvent("join_lobby_info", jsonObject.toString());
                                }
                            }

                            //add player to the lobby
                            if(!playerFound) {
                                lobby.addPlayer(username);
                                jsonObject = new JSONObject();
                                jsonObject.put("status", "joined_successfully");
                                jsonObject.put("lobby", lobbyName);
                                client.sendEvent("join_lobby_info", jsonObject.toString());


                                //tell other clients that another player joined the game
                                for(String player : lobby.getPlayers()) {
                                    for (SocketIOClient socketIOClient : server.getAllClients()) {
                                        if (player.equals(socketIOClient.get("username")) && !username.equals(socketIOClient.get("username"))) {
                                            socketIOClient.sendEvent("player_joined_game", username);
                                        }
                                    }
                                }

                                //tell clients to refresh lobby list
                                JSONObject json = new JSONObject();
                                json.put("lobby_name", lobby.getLobbyName());
                                json.put("game_name", lobby.getGameName());
                                json.put("current", lobby.getCurrentPlayers());
                                for(SocketIOClient socketIOClient : server.getAllClients()) {
                                    socketIOClient.sendEvent("refresh_lobby", json.toString());
                                }

                                //start a lobby if number of players reached to the pref size
                                if (lobby.isGameStarted() && lobby.getGameName().equals("snake and ladder")) {

                                    new java.util.Timer().schedule(new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            Thread thread = new Thread(new SnakeAndLadder(lobby, server));
                                            thread.start();
                                            lobby.setThread(thread);

                                        }
                                    },5000);

                                    for (SocketIOClient ioClient : server.getAllClients()) {
                                        ioClient.sendEvent("refresh_lobby_for_delete", lobbyName);
                                    }
                                }
                                else if (lobby.isGameStarted() && lobby.getGameName().equals("tic tac toe")) {
                                    new java.util.Timer().schedule(new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            Thread thread = new Thread(new TicTacToe(server, lobby));
                                            thread.start();
                                            lobby.setThread(thread);
                                        }
                                    },5000);

                                    for (SocketIOClient ioClient : server.getAllClients()) {
                                        ioClient.sendEvent("refresh_lobby_for_delete", lobbyName);
                                    }
                                }

                            }
                        }

                    }
                }
            }
        });
    }

    //give client, players in his/her lobby
    private void giveLobbyPlayers() {
        server.addEventListener("player_info", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String lobbyName, AckRequest ackRequest) throws Exception {
                for(Lobby lobby : lobbies) {
                    if(lobby.getLobbyName().equals(lobbyName)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("players", lobby.getPlayers());
                        ackRequest.sendAckData(jsonObject.toString());
                    }
                }
            }
        });
    }

    public static void remove(Lobby lobby) {
        lobbies.remove(lobby);
    }

}
