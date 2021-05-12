package com.liraf.reader.models.requests;

public class UpdateUser {

    private final String name;
    private final String login;
    private final String password;

    public UpdateUser(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }
}
