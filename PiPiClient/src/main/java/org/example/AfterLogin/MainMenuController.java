package org.example.AfterLogin;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.example.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    public JFXListView<User> listView;

    public JFXHamburger hamburger;


    @FXML
    private JFXButton add_friend;

    @FXML
    private JFXButton add_group;

    @FXML
    private JFXNodesList nodes_list;

    @FXML
    private AnchorPane anchor_pane;

    @FXML
    private JFXButton toggle;

    @FXML
    private JFXButton play_game;

    @FXML
    private VBox chat_box;

    @FXML
    private JFXDrawer drawer;

    private static ObservableList<User> users;
    private static ObservableList<User> groups;

    public static ObservableList<User> getUsers() {
        return users;
    }

    public static void setUsers(ObservableList<User> users) {
        MainMenuController.users = users;
    }

    Socket socket;
    private static boolean group = false;

    public static boolean isGroup() {
        return group;
    }

    private static String username;
    public static String clickedName = "";

    public static String getClickedName() {
        return clickedName;
    }

    public static void setUsername(String username) {
        MainMenuController.username = username;
    }

    public static String getUsername() {
        return username;
    }


    public MainMenuController() {
        groups = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();
        socket = App.getSocket();
    }

    //main page of my app
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        users.removeAll();
        groups.removeAll();
        App.resizeStage(1020, 755);
        drawer.setDisable(true);

        //float button
        nodes_list.setRotate(180);
        nodes_list.setSpacing(5);

        //get contacts from server
        socket.emit("give_contacts_data", username);
        getFriends();

        //get groups from server
        socket.emit("give_groups_data", username);
        getGroups();

        //get online people
        getOnlinePeople();

        //check and add if a friend added while we r online
        friendAddedWhileOnline();

        //same thing for groups
        groupAddedWhileOnline();

        //hamburger menu transition
        hamburgerTransition();

        //check and update unread messages
        updateUnread();

        //set users to the list view and set our custom list cell
        listView.setCellFactory(piPiUsersListView -> new UsersListCell());
        listView.setItems(users);

        //click listener for list view
        listView.setOnMouseClicked(mouseEvent -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                clickedName = listView.getSelectionModel().getSelectedItem().getUsername();

                System.out.println("clicked on : " + clickedName);
                System.out.println(group);

                try {
                    VBox secPane = FXMLLoader.load(App.class.getResource("chat_page.fxml"));
                    chat_box.getChildren().setAll(secPane);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //change view of friends and groups
        toggle.setOnAction(actionEvent ->  {
            if(!group) {
                listView.setItems(groups);
                nodes_list.animateList(false);
                group = !group;
            }
            else {
                listView.setItems(users);
                nodes_list.animateList(false);
                group = !group;
            }
        });

        //load drawer for hamburger menu
        try {
            VBox toolbar = FXMLLoader.load(App.class.getResource("drawer_content.fxml"));
            drawer.setSidePane(toolbar);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //add friend button
        add_friend.setOnAction(actionEvent ->  {
            VBox secPane = null;
            try {
                secPane = FXMLLoader.load(App.class.getResource("add_friend.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            chat_box.getChildren().setAll(secPane);

            nodes_list.animateList(false);
        });

        //add group button
        add_group.setOnAction(actionEvent ->  {
            VBox secPane = null;
            try {
                secPane = FXMLLoader.load(App.class.getResource("add_group.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            chat_box.getChildren().setAll(secPane);

            nodes_list.animateList(false);

        });

        //show games page
        play_game.setOnAction(actionEvent ->  {
            VBox secPane = null;
            try {
                secPane = FXMLLoader.load(App.class.getResource("games.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            chat_box.getChildren().setAll(secPane);

            nodes_list.animateList(false);
        });
    }


    private void getGroups() {
        socket.on("groups_json_string", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();

                if (!json.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);

                        JSONArray jsonArray = jsonObject.getJSONArray("groups");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            User group = new User(jsonArray.getString(i), new Image(App.class.getResource("images/main_two.jpg").toExternalForm()), true);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    groups.add(group);
                                }
                            });
                        }

                        socket.off("groups_json_string");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void groupAddedWhileOnline() {
        socket.on("group_added_while_online", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String groupName = objects[0].toString();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        groups.add(new User(groupName, true));
                    }
                });
            }
        });
    }

    private void friendAddedWhileOnline() {
        socket.on("friend_added_while_online", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String friendName = objects[0].toString();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        users.add(new User(friendName, false));
                    }
                });
            }
        });
    }

    private void getFriends() {
        //get friends info from server
        socket.on("friends_json_string", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();

                if (!json.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);

                        JSONArray jsonArray = jsonObject.getJSONArray("contacts");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            User piPiUsers = new User(jsonArray.getString(i), new Image(App.class.getResource("images/main_two.jpg").toExternalForm()), false);

                            checkForUnreadMessages(MainMenuController.getUsername(), jsonArray.getString(i), false, piPiUsers);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    users.add(piPiUsers);
                                }
                            });

                            socket.off("friends_json_string");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void checkForUnreadMessages(String username, String friend, boolean isGroup, User user) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("is_group", isGroup);
        if(isGroup) {
            jsonObject.put("username", username);
            jsonObject.put("group_name", friend);
        }
        else {
            jsonObject.put("username", username);
            jsonObject.put("friend_name", friend);
        }
        socket.emit("unread", jsonObject.toString(), new Ack() {
            @Override
            public void call(Object... objects) {
                int unread = Integer.parseInt(objects[0].toString());

                System.out.println(friend + " : " + unread);
                user.setUnread("" + unread);

            }
        });
    }

    private void updateUnread() {
        socket.on("update_unread", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                for (User user : users) {
                    try {
                        checkForUnreadMessages(MainMenuController.getUsername(), user.getUsername(), false, user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void getOnlinePeople() {
        socket.on("online_people", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                String json = objects[0].toString();
                ArrayList<String> onlineUsers = new ArrayList<>();
                //System.out.println("jesus");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("usernames");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        onlineUsers.add(jsonArray.getString(i));
                    }

                    for (String user : onlineUsers) {
                        for (User piPiUsers : users) {
                            if (piPiUsers.getUsername().equals(user)) {
                                piPiUsers.setOnline(true);
                                //System.out.println(piPiUsers.getUsername());
                            }
                        }
                    }

                    for (User piPi : users) {
                        boolean stillOnline = false;
                        //System.out.println(onlineUsers);
                        //System.out.println(piPi.getUsername());
                        for (String username : onlineUsers) {

                            if (piPi.getUsername().equals(username)) {
                                stillOnline = true;
                                break;
                            }
                        }

                        if (!stillOnline) {
                            piPi.setOnline(false);
                        }

                    }

                    listView.refresh();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void hamburgerTransition() {
        HamburgerBackArrowBasicTransition hamburgerBackArrowBasicTransition = new HamburgerBackArrowBasicTransition(hamburger);
        hamburgerBackArrowBasicTransition.setRate(-1);
        hamburger.setOnMouseClicked(mouseEvent -> {
            hamburgerBackArrowBasicTransition.setRate(hamburgerBackArrowBasicTransition.getRate() * -1);
            hamburgerBackArrowBasicTransition.play();

            if (!drawer.isOpened()) {
                drawer.setDisable(false);
                drawer.open();
            } else {
                drawer.close();
                drawer.setDisable(true);
            }
        });
    }
}
