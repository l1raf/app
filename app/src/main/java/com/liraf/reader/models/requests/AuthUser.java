package com.liraf.reader.models.requests;

public class AuthUser {

    private final String login;
    private final String password;

    public AuthUser(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
