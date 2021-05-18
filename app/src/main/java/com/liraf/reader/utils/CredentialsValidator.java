package com.liraf.reader.utils;

public final class CredentialsValidator {

    public static boolean isUserNameValid(String username) {
        return !(username == null || username.length() < 4);
    }

    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,30}$");
    }
}
