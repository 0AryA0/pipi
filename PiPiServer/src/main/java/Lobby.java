import java.util.ArrayList;

public class Lobby {

    private String lobbyName;
    private String gameName;
    private int numberOfPlayers;
    private ArrayList<String> players = new ArrayList<>();
    private int currentPlayers;
    private boolean isGameStarted = false;
    Thread thread;

    public Lobby(String lobbyName, String gameName, int numberOfPlayers) {
        this.lobbyName = lobbyName;
        this.gameName = gameName;
        this.numberOfPlayers = numberOfPlayers;
        currentPlayers = 0;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void addPlayer(String username) {
        this.players.add(username);
        this.currentPlayers++;

        if(currentPlayers == numberOfPlayers) {
            isGameStarted = true;
        }
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }
}

