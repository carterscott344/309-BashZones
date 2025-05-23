package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;     // define message textview variable
    private Button counterButton;     // define counter button variable

    private TextInputEditText incrementText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        counterButton = findViewById(R.id.main_counter_btn);// link to counter button in the Main activity XML
        incrementText = findViewById(R.id.increment_text_box);

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            messageText.setText("Intent Example");
        } else {
            String number = extras.getString("NUM");  // this will come from LoginActivity
            messageText.setText("The number was " + number);
        }
        counterButton.setVisibility(8);


        incrementText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counterButton.setVisibility(8);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                counterButton.setVisibility(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                counterButton.setVisibility(0);
            }
        });




        /* click listener on counter button pressed */
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when counter button is pressed, use intent to switch to Counter Activity */
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                intent.putExtra("increment_value", Integer.valueOf(incrementText.getText().toString()));
                startActivity(intent);
            }
        });
    }
}