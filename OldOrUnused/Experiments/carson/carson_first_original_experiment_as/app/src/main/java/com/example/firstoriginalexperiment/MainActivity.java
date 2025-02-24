package com.example.firstoriginalexperiment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public TextView helloText;
    public Button cornerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helloText = findViewById(R.id.hello);
        cornerButton = findViewById(R.id.button1);
        cornerButton.setText("Blue!");
    }

    public void disable(View v) {
        v.setEnabled(false);
        helloText.setTextColor(Color.BLUE);
    }
}