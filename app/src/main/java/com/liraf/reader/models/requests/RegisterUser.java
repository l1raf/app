package com.liraf.reader.models.requests;

public class RegisterUser {

    private final String login;
    private final String password;
    private final String name;

    public RegisterUser(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
