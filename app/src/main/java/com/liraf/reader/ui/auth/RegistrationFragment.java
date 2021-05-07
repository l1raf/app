package com.liraf.reader.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

public class RegistrationFragment extends Fragment {
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User user = new User();

        final Button registerButton = view.findViewById(R.id.btn_login);
        final EditText nameEditText = view.findViewById(R.id.name);
        final EditText emailEditText = view.findViewById(R.id.username);
        final EditText passwordEditText = view.findViewById(R.id.password);
        final EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        final ProgressBar progressBar = view.findViewById(R.id.r_loading);

        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            boolean isValidName = name.trim().length() > 3;
            boolean isValidEmail = CredentialsValidator.isUserNameValid(email);
            boolean isValidPassword = CredentialsValidator.isPasswordValid(password);
            boolean passwordsMatch = password.equals(confirmPassword);

            if (!isValidName)
                nameEditText.setError(getResources().getString(R.string.invalid_name));

            if (!isValidEmail)
                emailEditText.setError(getResources().getString(R.string.invalid_username));

            if (!isValidPassword)
                passwordEditText.setError(getResources().getString(R.string.invalid_password));

            if (!passwordsMatch)
                confirmPasswordEditText.setError(getResources().getString(R.string.not_confirmed));

            if (isValidName && isValidEmail && isValidPassword && passwordsMatch) {

                authViewModel.register(name, email, password);
            }
        });

        authViewModel.getLoginResult().observe(getViewLifecycleOwner(), registerResult -> {
            if (registerResult != null) {
                if (registerResult.status == Resource.Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                    registerButton.setEnabled(false);
                } else if (registerResult.status == Resource.Status.SUCCESS) {
                    progressBar.setVisibility(View.GONE);

                    if (registerResult.data != null) {
                        user.setUserId(registerResult.data.getId());
                        user.setDisplayName(registerResult.data.getName());

                        authViewModel.saveUser(user);
                        authViewModel.saveTokens(registerResult.data.getAccessToken(), registerResult.data.getRefreshToken());

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    registerButton.setEnabled(true);
                    Toast.makeText(getContext(), registerResult.message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}