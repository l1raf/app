package com.liraf.reader.api;

import androidx.annotation.NonNull;

import com.liraf.reader.repositories.AccountRepository;

import org.jetbrains.annotations.Nullable;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private final AccountRepository accountRepository;

    public TokenAuthenticator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) {
        final String accessToken = accountRepository.getAccessToken();

        if (!isRequestWithAccessToken(response) || accessToken == null)
            return null;

        synchronized (this) {
            final String newAccessToken = accountRepository.getAccessToken();

            if (!accessToken.equals(newAccessToken))
                return newRequestWithAccessToken(response.request(), newAccessToken);

            final String updatedAccessToken = accountRepository.refreshToken();
            return newRequestWithAccessToken(response.request(), updatedAccessToken);
        }
    }

    private boolean isRequestWithAccessToken(Response response) {
        String header = response.request().header("Authorization");
        return header != null && header.startsWith("Bearer");
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}
