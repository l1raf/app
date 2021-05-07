package com.liraf.reader.models;

public class User {

    private String userId;
    private String displayName;
    private String login;
    private String password;

    public User(String userId, String displayName, String login, String password) {
        this.userId = userId;
        this.displayName = displayName;
        this.login = login;
        this.password = password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() { }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}