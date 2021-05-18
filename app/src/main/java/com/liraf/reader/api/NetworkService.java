package com.liraf.reader.api;

import android.content.Context;

import com.liraf.reader.repositories.AccountRepository;
import com.liraf.reader.utils.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final String BASE_URL = "https://readerbackend.azurewebsites.net/";
    private static Retrofit retrofit;

    private NetworkService() {
    }

    private static Retrofit getRetroClient(Context context) {
        if (retrofit == null) {
            AccountRepository accountRepository = AccountRepository.getInstance(context);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .addInterceptor(new TokenInterceptor(accountRepository))
                    .authenticator(new TokenAuthenticator(accountRepository))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .build();
        }

        return retrofit;
    }

    public static WebService getWebService(Context context) {
        return getRetroClient(context).create(WebService.class);
    }
}
