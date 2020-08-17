import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OnlineClients implements Runnable{

    SocketIOServer server;
    Iterator<SocketIOClient> clients;


    public OnlineClients(SocketIOServer server) {
        this.server = server;
    }


    @Override
    public void run() {
        while (true) {

            if(!server.getAllClients().isEmpty()) {
                ArrayList<String> usernames = new ArrayList<>();
                JSONObject jsonObject = new JSONObject();
                ArrayList<SocketIOClient> clientArrayList = new ArrayList<>();
                clients = server.getAllClients().iterator();
                while (clients.hasNext()) {
                    SocketIOClient client = clients.next();

                    if(client.get("username") != null) {
                        //System.out.println(client.get("username").toString());
                        usernames.add(client.get("username").toString());
                        clientArrayList.add(client);
                    }
                }

                jsonObject.put("usernames", usernames);
                for(SocketIOClient client : clientArrayList) {
                    client.sendEvent("online_people", jsonObject.toString());

                }

            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
