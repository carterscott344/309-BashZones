package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable

    private TextView secondMessageText; // Second message textview for experimentation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        messageText.setText("Hello Coms 309!");
        messageText.setTextColor(Color.BLUE);
        messageText.setTextSize(12);

        secondMessageText = findViewById(R.id.secondary_txt);
        secondMessageText.setText("This is a Hello World App.");
        secondMessageText.setX(240);
    }
}