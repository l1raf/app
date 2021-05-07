package com.liraf.reader.models.requests;

public class RefreshTokenRequest {

    private final String accessToken;
    private final String refreshToken;

    public RefreshTokenRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
