package com.liraf.reader.ui.auth;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.liraf.reader.R;
import com.liraf.reader.models.User;
import com.liraf.reader.ui.main.MainActivity;
import com.liraf.reader.utils.CredentialsValidator;
import com.liraf.reader.utils.Resource;
import com.liraf.reader.viewmodels.AuthViewModel;

public class LoginFragment extends Fragment {

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText usernameEditText = view.findViewById(R.id.username);
        final EditText passwordEditText = view.findViewById(R.id.password);
        final Button loginButton = view.findViewById(R.id.btn_login);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.loading);

        User user = new User();

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            boolean isValidPassword = CredentialsValidator.isPasswordValid(password);
            boolean isValidEmail = CredentialsValidator.isUserNameValid(email);

            if (!isValidEmail)
                usernameEditText.setError(getResources().getString(R.string.invalid_login));

            if (!isValidPassword)
                passwordEditText.setError(getResources().getString(R.string.invalid_password));

            if (isValidEmail && isValidPassword) {
                user.setPassword(password);
                user.setLogin(email);
                authViewModel.login(email, password);
            }
        });

        authViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult != null) {
                if (loginResult.status == Resource.Status.LOADING) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginButton.setEnabled(false);
                } else if (loginResult.status == Resource.Status.SUCCESS) {
                    loadingProgressBar.setVisibility(View.GONE);

                    if (loginResult.data != null) {
                        user.setUserId(loginResult.data.getId());
                        user.setDisplayName(loginResult.data.getName());

                        authViewModel.saveUser(user);
                        authViewModel.saveTokens(loginResult.data.getAccessToken(), loginResult.data.getRefreshToken());

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        authViewModel.resetLoginResult();
                    }
                } else {
                    loadingProgressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    Toast.makeText(getContext(), loginResult.message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}