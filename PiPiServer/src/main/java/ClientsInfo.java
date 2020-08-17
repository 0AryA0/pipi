import com.corundumstudio.socketio.SocketIOClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientsInfo {

    private static JSONObject clients = new JSONObject();

    private static HashMap<String, SocketIOClient> onlineClients = new HashMap<>();

    public static SocketIOClient getClient(String username) {
        return (SocketIOClient) clients.get(username);
    }

    public static void addClient(String username, SocketIOClient client) {
        clients.put(username, client);
    }

    public static void addAnOnline(String username, SocketIOClient client) {
        onlineClients.put(username, client);
    }
}
