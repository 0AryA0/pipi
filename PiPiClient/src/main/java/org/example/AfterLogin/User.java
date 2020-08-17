package org.example.AfterLogin;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class User {

    private String username;
    private  String unread;
    private Image photo;
    private String bio;
    private boolean isOnline;
    private boolean isGroup;

    private static ArrayList<User> groups = new ArrayList<>();
    private static ArrayList<User> users = new ArrayList<>();

    public User(String username, boolean isGroup) {
        this.username = username;
        if(isGroup) {
            groups.add(this);
        }
        else {
            users.add(this);
        }
    }

    public User(String username, Image image, boolean isGroup) {
        this.username = username;
        this.photo = image;
        if(isGroup) {
            groups.add(this);
        }
        else {
            users.add(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static void setUsers(ArrayList<User> users) {
        User.users = users;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public static User search (String username) {
        for(User piPiUsers : users) {
            if(piPiUsers.getUsername().equals(username)) {
                return piPiUsers;
            }
        }
        return null;
    }

    public boolean isGroup() {
        return isGroup;
    }



    public void setGroup(boolean group) {
        isGroup = group;
    }

    public static ArrayList<User> getGroups() {
        return groups;
    }

    public static void setGroups(ArrayList<User> groups) {
        User.groups = groups;
    }
}
