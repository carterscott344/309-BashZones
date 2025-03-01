package com.example.bz_frontend_new;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpPage extends AppCompatActivity {

    // Testing field to remove later
    JSONObject requestBody;

    // Constant fields
    private static final String url = "https://37a0dabe-3419-41ff-bd25-2c6f490a1b79.mock.pstmn.io/users";

    // Needed views for this activity
    EditText username;
    EditText password;
    EditText email;
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
        signUpButton = findViewById(R.id.signUpButton);

        // Set onClick listener for the sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate JSONObject for posts
                requestBody = new JSONObject();
                try {
                    requestBody.put("username", username.getText().toString());
                    requestBody.put("password", password.getText().toString());
                    requestBody.put("email", email.getText().toString());
                    // Add more key-value pairs as needed
                } catch (Exception e) {
                    e.printStackTrace();
                }

                createUser();
            }
        });
    }

    /**
     * POST JsonObj request method, Creates a new user
     */
    private void createUser() {
        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
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