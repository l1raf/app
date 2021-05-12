package com.liraf.reader.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.liraf.reader.R;
import com.liraf.reader.databinding.FragmentSettingsBinding;
import com.liraf.reader.ui.auth.AuthActivity;
import com.liraf.reader.viewmodels.SettingsViewModel;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding.btnSaveChanges.setEnabled(false);
        binding.emailEdit.setText(settingsViewModel.getEmail());
        binding.nameEdit.setText(settingsViewModel.getUsername());
        binding.passwordEdit.setText(settingsViewModel.getPassword());

        addOnTextChangedListeners();

        binding.btnSaveChanges.setOnClickListener(v -> {
            settingsViewModel.updateUser(
                    binding.nameEdit.getText().toString(),
                    binding.emailEdit.getText().toString(),
                    binding.passwordEdit.getText().toString()
            );

            binding.btnSaveChanges.setEnabled(false);
        });

        return view;
    }

    private void addOnTextChangedListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnSaveChanges.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.nameEdit.addTextChangedListener(textWatcher);
        binding.emailEdit.addTextChangedListener(textWatcher);
        binding.passwordEdit.addTextChangedListener(textWatcher);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_menu_item) {
            settingsViewModel.logout();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}