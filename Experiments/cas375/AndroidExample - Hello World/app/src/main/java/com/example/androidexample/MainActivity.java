package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextInputEditText textBox;
    private Button confirmName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        textBox = findViewById(R.id.enterName);
        confirmName = findViewById(R.id.confirm_button);
        messageText.setText("Hello World!");

        confirmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textBox.getText().toString().isEmpty()){
                    messageText.setText("Error: Please enter a name");
                }
                else {
                    messageText.setText("Hello " + textBox.getText() + "!");
                }
            }
        });


    }
}