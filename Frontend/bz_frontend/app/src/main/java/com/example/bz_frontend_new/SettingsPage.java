package com.example.bz_frontend_new;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsPage extends AppCompatActivity {

    // Button fields
    Button user_settings_button;
    Button return_button;
    Button game_settings_button;
    Button inventory_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // Set horizontal orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize buttons
        user_settings_button = findViewById(R.id.user_settings_button);
        return_button = findViewById(R.id.return_button);
        game_settings_button = findViewById(R.id.game_settings_button);
        inventory_button = findViewById(R.id.inventory_button);

        // Set onClick listeners
        user_settings_button.setOnClickListener(this::openUserSettings);
        return_button.setOnClickListener(this::returnToGeneral);
        game_settings_button.setOnClickListener(this::openGameSettings);
        inventory_button.setOnClickListener(this::openInventory);
    }

    // onClick listeners methods
    public void openUserSettings(View v){
        Intent i = new Intent(this, UserSettingsPage.class);
        startActivity(i);
    }

    public void returnToGeneral(View v){
        Intent i = new Intent(this, GeneralPage.class);
        startActivity(i);
    }

    public void openGameSettings(View v){
        Intent i = new Intent(this, GameSettingsPage.class);
        startActivity(i);
    }

    public void openInventory(View v){
        Intent i = new Intent(this, UserInventoryPage.class);
        startActivity(i);
    }
}