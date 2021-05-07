package com.liraf.reader.data;

import android.content.Context;
import android.content.SharedPreferences;


import com.liraf.reader.models.User;

public class UserPreferences {

    private static final String SHARED_PREF_NAME = "user_data";
    private static final String EMAIL_KEY = "user_email";
    private static final String PASSWORD_KEY = "user_password";
    private static final String NAME_KEY = "user_name";
    private static final String ID_KEY = "user_id";
    private static final String ACCESS_TOKEN = "token";
    private static final String REFRESH_TOKEN = "r_token";

    private final SharedPreferences sharedPreferences;

    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, null);
    }

    public void saveToken(String token, String refreshToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ACCESS_TOKEN, token);
        editor.putString(REFRESH_TOKEN, refreshToken);

        editor.apply();
    }

    public User getUser() {
        return new User(
                sharedPreferences.getString(ID_KEY, null),
                sharedPreferences.getString(NAME_KEY, null),
                sharedPreferences.getString(EMAIL_KEY, null),
                sharedPreferences.getString(PASSWORD_KEY, null)
        );
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ID_KEY, user.getUserId());
        editor.putString(EMAIL_KEY, user.getLogin());
        editor.putString(NAME_KEY, user.getDisplayName());
        editor.putString(PASSWORD_KEY, user.getPassword());

        editor.apply();
    }
}
