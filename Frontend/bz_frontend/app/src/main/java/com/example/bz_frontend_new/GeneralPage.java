package com.example.bz_frontend_new;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GeneralPage extends AppCompatActivity {

    // Important views
    ImageButton settings_button;
    ImageButton profile_button;
    ImageButton shop_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_page);

        // Initialize important views
        settings_button = findViewById(R.id.settings_button);
        profile_button = findViewById(R.id.profile_button);
        shop_button = findViewById(R.id.shop_button);

        // Set horizontal orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Set onClick listeners
        settings_button.setOnClickListener(this::launchSettings);
        profile_button.setOnClickListener(this::launchProfile);
        shop_button.setOnClickListener(this::launchShop);
    }

    // Activity launching methods
    public void launchSettings(View v) {
        Intent i = new Intent(this, SettingsPage.class);
        startActivity(i);
    }

    public void launchProfile(View v) {
        Intent i = new Intent(this, ProfilePage.class);
        startActivity(i);
    }

    public void launchShop(View v) {
        Intent i = new Intent(this, ShopPage.class);
        startActivity(i);
    }
}