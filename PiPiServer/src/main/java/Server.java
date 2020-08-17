import java.sql.*;
import java.util.*;
import java.util.Date;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONArray;
import org.json.JSONObject;

public class Server {

    private static final int PORT = 8585;
    private static final String HOSTNAME = "127.0.0.1";
    private static SocketIOServer server;
    private static String url = "jdbc:sqlite:/home/arya/Projects/JavaProjects/PiPiServer/pipi.db";
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static void main(String[] args) throws Exception {

        //configure server
        server();

        //configure database
        connection = DriverManager.getConnection(url);

        //handle online clients
        Thread thread = new Thread(new OnlineClients(server));
        thread.start();

        Thread lobbyThread = new Thread(new LobbyHandler(server));
        lobbyThread.start();

        //handle login event
        login();

        //handle server event
        register();

        //handle froget password event
        forgotPass();

        //handle friend requests
        handleFriendReq();

        //give contacts data to clients
        giveContactsData();

        //handle incoming messages from clients
        handleMessages();

        //create groups event
        createGroup();

        //join group event
        joinGroup();

        //give groups data and information to the clients
        giveGroupsData();

        //single player games handle lose and win
        singlePlay();

        //handle unread messages
        unread();

        //update unread messages
        updateUnread();

        //win and lose event
        winLoseEvent();
    }

    private static void winLoseEvent() {
        server.addEventListener("win_lose_information", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String username, AckRequest ackRequest) throws Exception {
                String sql = "SELECT * from users where username ='" + username + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                String json = resultSet.getString(8);

                ackRequest.sendAckData(json);

            }
        });
    }

    private static void joinGroup() {
        server.addEventListener("join_group", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);

                String username = jsonObject.getString("username");
                String groupName = jsonObject.getString("group_name");

                addGroupToUser(username, groupName);
                String status = addUserToGroup(username, groupName);

                ackRequest.sendAckData(status);
            }
        });
    }

    private static void login() {
        server.addEventListener("login", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws SQLException {


                JSONObject jsonObject = new JSONObject(data);


                String sql = "SELECT * from users where username ='" + jsonObject.getString("username") + "'";

                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery(sql);


                if (!resultSet.next()) {
                    client.sendEvent("login_info", "not_found");
                } else {
                    if (!jsonObject.getString("password").equals(resultSet.getString(2))) {
                        client.sendEvent("login_info", "password_incorrect");
                    }

                    if (jsonObject.getString("username").equals(resultSet.getString(1))
                            && jsonObject.getString("password").equals(resultSet.getString(2))) {

                        client.sendEvent("login_info", "success");
                        System.out.println(jsonObject.get("username") + " connected");

                        ClientsInfo.addAnOnline(jsonObject.getString("username"), client);
                        client.set("username", jsonObject.getString("username"));


                    }

                }

            }
        });
    }

    private static void register() {
        server.addEventListener("register_event", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws SQLException {
                JSONObject jsonObject = new JSONObject(data);

                String sql = "SELECT * from users where username ='" + jsonObject.getString("username") + "'";
                String sqlEmail = "SELECT * from users where email ='" + jsonObject.getString("email") + "'";
                Statement statement = connection.createStatement();


                ResultSet resultSet = statement.executeQuery(sql);
                String duplicateUsername = null;
                String duplicatedEmail = null;

                while (resultSet.next()) {
                    duplicateUsername = resultSet.getString(1);
                }

                resultSet = statement.executeQuery(sqlEmail);
                while (resultSet.next()) {
                    duplicatedEmail = resultSet.getString(3);
                }


                if (!(duplicateUsername == null)) {
                    System.out.println("duplicated username : " + jsonObject.getString("username"));
                    client.sendEvent("get_reg_confirmation", "duplicate_username");
                }

                if (!(duplicatedEmail == null)) {
                    System.out.println("duplicated email : " + jsonObject.getString("email"));
                    client.sendEvent("get_reg_confirmation", "duplicated_email");
                }

                if (duplicatedEmail == null && duplicateUsername == null) {
                    addUser(jsonObject, connection, client);
                    System.out.println("register successful");
                    client.sendEvent("get_reg_confirmation", "register_successful");
                }

            }
        });
    }

    private static void singlePlay() {
        server.addEventListener("single_player_stat", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);
                String gameName = jsonObject.getString("game");
                String username = jsonObject.getString("username");
                boolean win = jsonObject.getBoolean("won");

                String sql = "SELECT * from users where username ='" + username + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);


                JSONObject json = new JSONObject(resultSet.getString(8));
                JSONObject inner = json.getJSONObject(gameName);
                if (win) {
                    int number = inner.getInt("win_solo");
                    number += 1;
                    inner.put("win_solo", number);
                } else {

                    System.out.println("pipi");
                    int number = inner.getInt("lose_solo");
                    number += 1;
                    inner.put("lose_solo", number);
                    System.out.println(number);
                    System.out.println(inner.getInt("lose_solo"));
                }

                json.put(gameName, inner);

                sql = "UPDATE users " + "SET win_lose = ? " + "WHERE username=?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(2, username);
                preparedStatement.setString(1, json.toString());
                preparedStatement.executeUpdate();


            }
        });
    }

    private static void createGroup() {
        server.addEventListener("create_group", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("users");

                String groupName = jsonObject.getString("group_name");

                String sql = "SELECT * from groups where group_name ='" + groupName + "'";

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                String duplicatedGroupName = null;

                while (resultSet.next()) {
                    duplicatedGroupName = resultSet.getString(1);
                }

                if (!(duplicatedGroupName == null)) {
                    System.out.println("duplicated group : " + groupName);
                    ackRequest.sendAckData("duplicated");
                }

                if (duplicatedGroupName == null) {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO groups (group_name, members) VALUES (?, ?)");

                    preparedStatement.setString(1, groupName);
                    preparedStatement.setString(2, "");

                    preparedStatement.execute();
                    System.out.println("group : " + groupName + " created");
                    ackRequest.sendAckData("group_created");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        System.out.println(jsonArray.getString(i));
                        addUserToGroup(jsonArray.getString(i), groupName);
                        addGroupToUser(jsonArray.getString(i), groupName);
                    }
                }
            }
        });
    }

    private static String addUserToGroup(String username, String groupName) throws SQLException {

        String sql = "SELECT * from groups where group_name ='" + groupName + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);


        if (resultSet.next()) {

            String json = resultSet.getString(2);

            JSONObject jsonObject;

            ArrayList<String> members;


            if (json.equals("")) {
                jsonObject = new JSONObject();
                members = new ArrayList<>();
                members.add(username);
                jsonObject.put("members", members);

                sql = "UPDATE groups " + "SET members = ? " + "WHERE group_name=?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(2, groupName);
                preparedStatement.setString(1, jsonObject.toString());
                preparedStatement.executeUpdate();

                return "successful";

            } else {
                jsonObject = new JSONObject(json);

                JSONArray jsonArray = jsonObject.getJSONArray("members");
                members = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    members.add(jsonArray.getString(i));
                }

                System.out.println(members);

                boolean isDuplicated = false;

                for (String s : members) {
                    if (s.equals(username)) {
                        isDuplicated = true;
                        return "duplicated";
                    }
                }

                if (!isDuplicated) {
                    members.add(username);

                    jsonObject.put("members", members);


                    sql = "UPDATE groups " + "SET members = ? " + "WHERE group_name=?";

                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setString(2, groupName);
                    preparedStatement.setString(1, jsonObject.toString());
                    preparedStatement.executeUpdate();

                    return "successful";

                }

            }
        }
        return "unexpected";

    }

    private static String addGroupToUser(String username, String groupName) throws SQLException {
        String sql = "SELECT * from users where username ='" + username + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);


        if (resultSet.next()) {

            String json = resultSet.getString(7);

            JSONObject jsonObject;

            ArrayList<String> groups;


            if (json.equals("")) {
                jsonObject = new JSONObject();
                groups = new ArrayList<>();
                groups.add(groupName);
                jsonObject.put("groups", groups);

                sql = "UPDATE users " + "SET groups = ? " + "WHERE username=?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(2, username);
                preparedStatement.setString(1, jsonObject.toString());
                preparedStatement.executeUpdate();

                for (SocketIOClient socketIOClient : server.getAllClients()) {
                    if (socketIOClient.get("username").equals(username)) {
                        socketIOClient.sendEvent("group_added_while_online", groupName);
                    }
                }

                return "successful";

            } else {
                jsonObject = new JSONObject(json);

                JSONArray jsonArray = jsonObject.getJSONArray("groups");
                groups = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    groups.add(jsonArray.getString(i));
                }

                System.out.println(groups);

                boolean isDuplicated = false;

                for (String s : groups) {
                    if (s.equals(groupName)) {
                        isDuplicated = true;
                        return "duplicated";
                    }
                }

                if (!isDuplicated) {
                    groups.add(groupName);

                    jsonObject.put("groups", groups);


                    sql = "UPDATE users " + "SET groups= ? " + "WHERE username=?";

                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setString(2, username);
                    preparedStatement.setString(1, jsonObject.toString());
                    preparedStatement.executeUpdate();

                    for (SocketIOClient socketIOClient : server.getAllClients()) {
                        if (socketIOClient.get("username").equals(username)) {
                            socketIOClient.sendEvent("group_added_while_online", groupName);
                        }
                    }

                    return "successful";

                }

            }

        }

        return "unexpected";
    }

    private static void handleMessages() throws SQLException {


        server.addEventListener("message_sent_from_user", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);

                boolean isGroup = jsonObject.getBoolean("is_group");

                String clientUsername = jsonObject.getString("username");
                String friendUsername = jsonObject.getString("friendName");
                String messageText = jsonObject.getString("message");

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO message (message_text, message_sender, message_reciever, date, seen) VALUES (?, ?, ?, ?, ?)");

                preparedStatement.setString(1, messageText);
                preparedStatement.setString(2, clientUsername);
                preparedStatement.setString(3, friendUsername);

                Date date = new Date();
                preparedStatement.setString(4, date.toString());
                preparedStatement.setInt(5, 0);

                preparedStatement.executeUpdate();

                JSONObject message = new JSONObject();

                message.put("message", messageText);
                message.put("sender", clientUsername);
                message.put("receiver", friendUsername);
                message.put("time", date.toString());


                if (isGroup) {
                    //friend username is group name here
                    String sql = "SELECT * from groups where group_name ='" + friendUsername + "'";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);

                    String jsonString = resultSet.getString(2);
                    JSONObject json = new JSONObject(jsonString);
                    JSONArray jsonArray = json.getJSONArray("members");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        String member = jsonArray.getString(i);

                        for (SocketIOClient ioClient : server.getAllClients()) {
                            if (ioClient.get("username").equals(member)) {
                                ioClient.sendEvent("message_sent_when_online", message.toString());
                            }
                        }
                    }

                } else {
                    client.sendEvent("message_sent_when_online", message.toString());

                    for (SocketIOClient ioClient : server.getAllClients()) {
                        if (ioClient.get("username").toString().equals(friendUsername)) {
                            ioClient.sendEvent("message_sent_when_online", message.toString());
                            ioClient.sendEvent("update_unread");
                        }
                    }

                }

            }
        });

        server.addEventListener("give_messages", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);

                boolean isGroup = jsonObject.getBoolean("is_group");

                String sql;
                String receiver;
                String sender = "";

                if (isGroup) {
                    String groupName = jsonObject.getString("group_name");
                    sql = "SELECT * from message where message_reciever ='" + groupName + "'";
                    receiver = groupName;
                } else {
                    String username = jsonObject.getString("username");
                    String friendName = jsonObject.getString("friend_name");

                    sql = "SELECT * from message where message_sender ='" + username + "'" + "and message_reciever ='" +
                            friendName + "' or message_sender = '" + friendName + "' and message_reciever = '" + username + "'";

                    receiver = username;
                    sender = friendName;
                }


                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                ArrayList<String> jsonObjects = new ArrayList<>();

                while (resultSet.next()) {
                    JSONObject message = new JSONObject();
                    message.put("message", resultSet.getString(1));
                    message.put("sender", resultSet.getString(2));
                    message.put("receiver", resultSet.getString(3));
                    message.put("time", resultSet.getString(4));
                    message.put("number", resultSet.getInt(5));
                    message.put("seen", resultSet.getInt(6));

                    System.out.println(message.getString("message"));

                    jsonObjects.add(message.toString());
                }
                JSONObject toSend = new JSONObject();
                toSend.put("messages", jsonObjects);
                ackRequest.sendAckData(toSend.toString());

                /*sql = "UPDATE message " + "SET seen = ? " + "WHERE message_reciever= ? " + "and message_sender = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(2, receiver);
                preparedStatement.setString(3, sender);
                preparedStatement.setInt(1, 1);
                preparedStatement.executeUpdate();

                client.sendEvent("update_unread");*/

            }
        });
    }

    private static void updateUnread() {
        server.addEventListener("update_unread_messages", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(data);
                String receiver = jsonObject.getString("receiver");
                String sender = jsonObject.getString("sender");

                String sql = "UPDATE message " + "SET seen = ? " + "WHERE message_reciever= ? " + "and message_sender = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(2, receiver);
                preparedStatement.setString(3, sender);
                preparedStatement.setInt(1, 1);
                preparedStatement.executeUpdate();

                client.sendEvent("update_unread");


            }
        });
    }

    private static void unread() {
        server.addEventListener("unread", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String json, AckRequest ackRequest) throws Exception {
                JSONObject jsonObject = new JSONObject(json);
                boolean isGroup = jsonObject.getBoolean("is_group");
                String sql = "";

                if (isGroup) {

                } else {
                    String username = jsonObject.getString("username");
                    String friendName = jsonObject.getString("friend_name");

                    sql = "SELECT * from message where message_sender ='" + friendName + "'" + "and message_reciever ='" +
                            username + "'";
                }

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                int unread = 0;
                while (resultSet.next()) {
                    int read = resultSet.getInt(6);
                    if (read == 0) {
                        unread++;
                    }
                }

                //just number of unread messages
                ackRequest.sendAckData(unread);
            }
        });
    }

    private static void handleFriendReq() {
        server.addEventListener("friend_request", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                String myUsername = client.get("username");
                String friendUsername = data;

                addUsernameToContacts(myUsername, friendUsername, true);

                addUsernameToContacts(friendUsername, myUsername, false);
            }
        });
    }

    public static void addUsernameToContacts(String myUsername, String friendUsername, boolean isClient) throws SQLException {

        String sql = "SELECT * from users where username ='" + myUsername + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        SocketIOClient client = null;
        if (isClient) {

            for (SocketIOClient socketIOClient : server.getAllClients()) {
                if (socketIOClient.get("username").equals(myUsername)) {
                    client = socketIOClient;
                    break;
                }
            }

        }

        ArrayList<String> contacts;

        if (resultSet.next()) {

            String json = resultSet.getString(6);

            JSONObject jsonObject;

            if (checkForUsername(friendUsername)) {
                if (json.equals("")) {
                    jsonObject = new JSONObject();
                    contacts = new ArrayList<>();
                    contacts.add(friendUsername);
                    jsonObject.put("contacts", contacts);

                    sql = "UPDATE users " + "SET contacts = ? " + "WHERE username=?";

                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setString(2, myUsername);
                    preparedStatement.setString(1, jsonObject.toString());
                    preparedStatement.executeUpdate();

                    for (SocketIOClient socketIOClient : server.getAllClients()) {
                        if (socketIOClient.get("username").equals(myUsername)) {
                            socketIOClient.sendEvent("friend_added_while_online", friendUsername);
                        }
                    }

                } else {
                    jsonObject = new JSONObject(json);

                    JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                    contacts = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        contacts.add(jsonArray.getString(i));
                    }

                    System.out.println(contacts);

                    boolean isDuplicated = false;

                    for (String s : contacts) {
                        if (s.equals(friendUsername)) {
                            isDuplicated = true;
                            break;
                        }
                    }

                    if (!isDuplicated) {
                        contacts.add(friendUsername);

                        jsonObject.put("contacts", contacts);


                        sql = "UPDATE users " + "SET contacts = ? " + "WHERE username=?";

                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        preparedStatement.setString(2, myUsername);
                        preparedStatement.setString(1, jsonObject.toString());
                        preparedStatement.executeUpdate();

                        System.out.println("added");

                        for (SocketIOClient socketIOClient : server.getAllClients()) {
                            if (socketIOClient.get("username").equals(myUsername)) {
                                socketIOClient.sendEvent("friend_added_while_online", friendUsername);
                            }
                        }

                    } else {
                        if (isClient && client != null) {
                            client.sendEvent("add_friend_status", "duplicated");
                            System.out.println("already exists");
                        }

                    }

                }

            } else {
                if (isClient && client != null) {
                    client.sendEvent("add_friend_status", "username_not_found");
                }
            }

        } else {
            if (isClient && client != null) {
                client.sendEvent("add_friend_status", "unexpected_error");
            }
        }


    }

    public static boolean checkForUsername(String username) throws SQLException {
        String sql = "SELECT * from users where username ='" + username + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        return resultSet.next();
    }


    private static void forgotPass() {
        server.addEventListener("forgot_pass_username", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {

                String sql = "SELECT * from users where username ='" + data + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                if (!resultSet.next()) {
                    client.sendEvent("forgot_user_info", "not_found");
                } else {
                    client.sendEvent("forgot_user_info", resultSet.getString(4));
                    System.out.println(resultSet.getString(4));
                }
            }
        });

        server.addEventListener("forgot_answer_info", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {

                JSONObject jsonObject = new JSONObject(data);
                String sql = "SELECT * from users where username ='" + jsonObject.getString("username") + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                if (!resultSet.next()) {
                    client.sendEvent("forgot_answer_status", "not_found");
                } else {
                    if (jsonObject.getString("answer").equals(resultSet.getString(5))) {
                        client.sendEvent("forgot_answer_status", "answer_correct", resultSet.getString(2));
                        client.sendEvent("password_is", resultSet.getString(2));
                    } else {
                        client.sendEvent("forgot_answer_status", "answer_incorrect");
                    }
                }

            }
        });
    }

    public static void giveContactsData() {
        server.addEventListener("give_contacts_data", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                String sql = "SELECT * from users where username ='" + data + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);


                client.sendEvent("friends_json_string", resultSet.getString(6));

            }
        });
    }

    public static void giveGroupsData() {
        server.addEventListener("give_groups_data", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                String sql = "SELECT * from users where username ='" + data + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                client.sendEvent("groups_json_string", resultSet.getString(7));
            }
        });
    }

    public static void addUser(JSONObject jsonObject, Connection connection, SocketIOClient client) throws SQLException {

        System.out.println("adding user : " + jsonObject.get("username"));
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO users (username, password, email, question, answer, contacts, groups, win_lose) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");


        preparedStatement.setString(1, jsonObject.getString("username"));
        preparedStatement.setString(2, jsonObject.getString("password"));
        preparedStatement.setString(3, jsonObject.getString("email"));
        preparedStatement.setString(4, jsonObject.getString("question"));
        preparedStatement.setString(5, jsonObject.getString("answer"));
        preparedStatement.setString(6, "");
        preparedStatement.setString(7, "");

        JSONObject json = new JSONObject();
        JSONObject snake_and_ladder = new JSONObject();
        snake_and_ladder.put("lose_solo", 0);
        snake_and_ladder.put("win_solo", 0);
        snake_and_ladder.put("lose_multi", 0);
        snake_and_ladder.put("win_multi", 0);

        JSONObject tic_tac_toe = new JSONObject();
        tic_tac_toe.put("lose_solo", 0);
        tic_tac_toe.put("win_solo", 0);
        tic_tac_toe.put("lose_multi", 0);
        tic_tac_toe.put("win_multi", 0);
        json.put("snake_and_ladder", snake_and_ladder);
        json.put("tic_tac_toe", tic_tac_toe);

        preparedStatement.setString(8, json.toString());


        preparedStatement.execute();

        System.out.println("user : " + jsonObject.get("username") + "added");

    }

    public static void server() throws Exception {
        Configuration config = new Configuration();
        config.setHostname(HOSTNAME);
        config.setPort(PORT);

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        server = new SocketIOServer(config);
        server.start();

        System.out.println("server started");

    }

}

