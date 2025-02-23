package com.example.bz_frontend_new;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserSettingsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_page);

        // Set orientation to horizontal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }
}