package com.liraf.reader.utils;

import android.util.Patterns;

public final class CredentialsValidator {

    public static boolean isUserNameValid(String username) {
        if (username == null)
            return false;

        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,30}$");
    }
}
