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

public class ShopPage extends AppCompatActivity {

    // Important button fields
    Button return_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page);

        // Set horizontal orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize important button fields
        return_button = findViewById(R.id.shop_return_button);

        // Set important button onClick listeners
        return_button.setOnClickListener(this::returnToGeneral);
    }

    // Important onClick methods
    public void returnToGeneral(View v) {
        Intent i = new Intent(this, GeneralPage.class);
        startActivity(i);
    }

    // Makes an individual shop item from an array of item attributes
    public void createShopItem() {

    }

    // Takes all item arrays in an array and creates shop items for them
    public void createShopItems() {

    }
}