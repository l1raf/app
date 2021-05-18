package com.liraf.reader.repositories;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.liraf.reader.R;
import com.liraf.reader.api.NetworkService;
import com.liraf.reader.data.UserPreferences;
import com.liraf.reader.models.User;
import com.liraf.reader.models.requests.AuthUser;
import com.liraf.reader.models.requests.RefreshTokenRequest;
import com.liraf.reader.models.requests.RegisterUser;
import com.liraf.reader.models.requests.UpdateUser;
import com.liraf.reader.models.responses.AuthUserResponse;
import com.liraf.reader.utils.Resource;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {
    private static volatile AccountRepository instance;
    private final MutableLiveData<Resource<AuthUserResponse>> loginResult = new MutableLiveData<>();
    private final Context context;
    private final UserPreferences userPreferences;

    private AccountRepository(Context context) {
        this.context = context;
        this.userPreferences = new UserPreferences(context);
    }

    public static AccountRepository getInstance(Context context) {
        if (instance == null)
            instance = new AccountRepository(context);

        return instance;
    }

    public MutableLiveData<Resource<AuthUserResponse>> getLoginResult() {
        return loginResult;
    }

    public String getAccessToken() {
        return userPreferences.getAccessToken();
    }

    public void saveTokens(String accessToken, String refreshToken) {
        userPreferences.saveToken(accessToken, refreshToken);
    }

    public void updateUser(User user) {
        userPreferences.saveUser(user);

        NetworkService.getWebService(context).updateUser(new UpdateUser(
                user.getDisplayName(),
                user.getLogin(),
                user.getPassword())
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    Toast.makeText(context, R.string.updated_user, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, R.string.failed_to_update_user, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public User getUser() {
        return userPreferences.getUser();
    }

    public void saveUser(User user) {
        userPreferences.saveUser(user);
    }

    public String refreshToken() {
        String accessToken = userPreferences.getAccessToken();
        String refreshToken = userPreferences.getRefreshToken();

        try {
            Response<AuthUserResponse> response = NetworkService.getWebService(context).refreshToken(new RefreshTokenRequest(accessToken, refreshToken)).execute();

            if (response.isSuccessful()) {
                AuthUserResponse result = response.body();

                if (result != null) {
                    saveTokens(result.getAccessToken(), result.getRefreshToken());

                    return result.getAccessToken();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void register(String name, String email, String password) {
        NetworkService.getWebService(context).registerUser(new RegisterUser(name, email, password))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            try {
                                assert response.errorBody() != null;
                                loginResult.postValue(Resource.error(response.errorBody().string(), null));
                            } catch (Exception e) {
                                loginResult.postValue(Resource.error(context.getResources().getString(R.string.unknown_error), null));
                            }
                        } else {
                            login(email, password);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loginResult.postValue(Resource.error(context.getResources().getString(R.string.register_failed), null));
                    }
                });
    }

    public void login(String username, String password) {
        NetworkService.getWebService(context).authUser(new AuthUser(username, password))
                .enqueue(new Callback<AuthUserResponse>() {
                    @Override
                    public void onResponse(Call<AuthUserResponse> call, Response<AuthUserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            loginResult.postValue(Resource.success(response.body()));
                        } else if (response.code() == 404) {
                            loginResult.postValue(Resource.error(context.getResources().getString(R.string.wrong_credentials), null));
                        } else {
                            loginResult.postValue(Resource.error(context.getResources().getString(R.string.wrong_credentials), null));
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthUserResponse> call, Throwable t) {
                        loginResult.postValue(Resource.error(context.getResources().getString(R.string.auth_failed), null));
                    }
                });
    }

    public void removeUser() {
        userPreferences.removeUser();
    }
}