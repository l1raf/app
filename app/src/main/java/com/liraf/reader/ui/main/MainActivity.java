package com.liraf.reader.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.liraf.reader.R;
import com.liraf.reader.data.UserPreferences;
import com.liraf.reader.ui.auth.AuthActivity;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MainTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_view);
        navController = Navigation.findNavController(this, R.id.fragment);

        Bundle args = null;

        if (getIntent().getType() != null && getIntent().getType().equals("text/plain")) {
            args = new Bundle();
            args.putString("url", getIntent().getExtras().getString("android.intent.extra.TEXT"));
        }

        navController.setGraph(R.navigation.main_bottom_nav_graph, args);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(Set.of(R.id.homeFragment, R.id.favoritesFragment, R.id.settingsFragment)).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        Bundle args = null;

        if (i.getType() != null && i.getType().equals("text/plain")) {
            args = new Bundle();
            args.putString("url", i.getExtras().getString("android.intent.extra.TEXT"));
        }

        navController.navigate(R.id.homeFragment, args);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserPreferences userPreferences = new UserPreferences(this);

        if (userPreferences.getAccessToken() == null) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
        }
    }
}