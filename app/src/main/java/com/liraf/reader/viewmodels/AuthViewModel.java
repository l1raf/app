package com.liraf.reader.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.app.Application;

import com.liraf.reader.models.User;
import com.liraf.reader.models.responses.AuthUserResponse;
import com.liraf.reader.repositories.AccountRepository;
import com.liraf.reader.utils.Resource;

public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<Resource<AuthUserResponse>> loginResult;
    private final AccountRepository accountRepository;

    public LiveData<Resource<AuthUserResponse>> getLoginResult() {
        return loginResult;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = AccountRepository.getInstance(application);
        loginResult = accountRepository.getLoginResult();
    }

    public void resetLoginResult() {
        loginResult.postValue(null);
    }

    public void login(String username, String password) {
        loginResult.setValue(Resource.loading(null));
        accountRepository.login(username, password);
    }

    public void register(String name, String username, String password) {
        loginResult.setValue(Resource.loading(null));
        accountRepository.register(name, username, password);
    }

    public void saveUser(User user) {
        accountRepository.saveUser(user);
    }

    public void saveTokens(String accessToken, String refreshToken) {
        accountRepository.saveTokens(accessToken, refreshToken);
    }
}