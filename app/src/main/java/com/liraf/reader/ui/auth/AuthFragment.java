package com.liraf.reader.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.navigation.fragment.*;

import com.liraf.reader.R;

public class AuthFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController =  NavHostFragment.findNavController(this);

        view.findViewById(R.id.btn_sign_in).setOnClickListener(v -> navController.navigate(R.id.action_authFragment_to_loginFragment));
        view.findViewById(R.id.btn_sign_up).setOnClickListener(v -> navController.navigate(R.id.action_authFragment_to_registrationFragment));
    }
}