package com.liraf.reader.api;

import androidx.annotation.NonNull;

import com.liraf.reader.repositories.AccountRepository;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private final AccountRepository accountRepository;

    public TokenInterceptor(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        String accessToken = accountRepository.getAccessToken();

        if (accessToken == null) {
            return chain.proceed(chain.request());
        } else {

            Response response = chain.proceed(newRequestWithAccessToken(chain.request(), accessToken));

            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                synchronized (this) {
                    final String newAccessToken = accountRepository.getAccessToken();

                    if (!accessToken.equals(newAccessToken))
                        return chain.proceed(newRequestWithAccessToken(chain.request(), newAccessToken));

                    final String updatedAccessToken = accountRepository.refreshToken();

                    return chain.proceed(newRequestWithAccessToken(chain.request(), updatedAccessToken));
                }
            }

            return response;
        }
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}
