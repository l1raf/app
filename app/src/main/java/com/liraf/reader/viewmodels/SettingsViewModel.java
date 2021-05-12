package com.liraf.reader.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.liraf.reader.models.User;
import com.liraf.reader.repositories.AccountRepository;
import com.liraf.reader.repositories.ArticleRepository;

public class SettingsViewModel extends AndroidViewModel {

    private final ArticleRepository articleRepository;
    private final AccountRepository accountRepository;
    private final User user;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        articleRepository = ArticleRepository.getInstance(application);
        accountRepository = AccountRepository.getInstance(application);
        user = accountRepository.getUser();
    }

    public void updateUser(String username, String email, String password) {
        user.setDisplayName(username);
        user.setLogin(email);
        user.setPassword(password);

        accountRepository.updateUser(user);
    }

    public void logout() {
        articleRepository.deleteAllArticlesFromDb();
        accountRepository.removeUser();
    }

    public String getUsername() {
        return user.getDisplayName();
    }

    public String getPassword() {
        return user.getPassword();
    }

    public String getEmail() {
        return user.getLogin();
    }
}
