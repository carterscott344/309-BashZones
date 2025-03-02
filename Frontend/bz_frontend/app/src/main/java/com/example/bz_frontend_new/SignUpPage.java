package com.example.bz_frontend_new;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignUpPage extends AppCompatActivity {

    // Constant fields
    final Calendar calendar = Calendar.getInstance();

    // Testing field to remove later
    JSONObject requestBody;

    // Constant fields
    private static final String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/createUser";

    // Needed views for this activity
    EditText username;
    EditText password;
    EditText email;
    EditText birthday;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        // Set screen orientation to landscape when creating the activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initializing important views
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        birthday = findViewById(R.id.birthday);
        signUpButton = findViewById(R.id.signUpButton);

        // Date Picker listener for birthday
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        // Set onClick listener for birthday text
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignUpPage.this,
                        date,calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Set onClick listener for the sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate JSONObject for posts
                requestBody = new JSONObject();
                try {
                    requestBody.put("accountUsername", username.getText().toString());
                    requestBody.put("accountPassword", password.getText().toString());
                    requestBody.put("accountEmail", email.getText().toString());
                    requestBody.put("userBirthday", birthday.getText().toString());
                    JSONArray items = new JSONArray();
                    requestBody.put("itemsList", items);
                    createUser();
                } catch (Exception e) {
                    Toast.makeText(SignUpPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Update label
    private void updateLabel(){
        String myFormat="MM/dd/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        birthday.setText(dateFormat.format(calendar.getTime()));
    }

    /**
     * POST JsonObj request method, Creates a new user
     */
    private void createUser() {
        Log.d("Object", requestBody.toString());
        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SignUpPage.this, "Account Created!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignUpPage.this, LoginPage.class);
                        startActivity(i);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
    }

}