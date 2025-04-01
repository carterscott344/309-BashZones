package com.example.bz_frontend_new;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class LobbyPage extends AppCompatActivity {
    private SharedPreferences sp;

    private ImageView red1Image, red2Image, blue1Image, blue2Image;
    private TextView red1Name, red2Name, blue1Name, blue2Name;
    private Button leaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        red1Image = findViewById(R.id.red_1_image);
        red2Image = findViewById(R.id.red_2_image);
        blue1Image = findViewById(R.id.blu_1_image);
        blue2Image = findViewById(R.id.blu_2_image);

        // Initialize TextViews
        red1Name = findViewById(R.id.red_1_name);
        red2Name = findViewById(R.id.red_2_name);
        blue1Name = findViewById(R.id.blu_1_name);
        blue2Name = findViewById(R.id.blu_2_name);

        leaveButton = findViewById(R.id.leave_button);

    }


}
