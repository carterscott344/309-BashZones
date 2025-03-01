package com.example.bz_frontend_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserSettingsPage extends AppCompatActivity {

    // Test JsonObject TODO: Remove this later
    JSONObject requestBody;

    // String constant for volley requests, TODO: this will eventually be permanent but change it as needed
    private static final String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/updateUser/";

    // Important views
    Button saveChangesButton;
    EditText email_textEdit;
    EditText username_textEdit;
    EditText password_textEdit;
    EditText old_password_textEdit; //Use for validation later
    Button deleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_page);

        // Set orientation to horizontal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize important views

        email_textEdit = findViewById(R.id.email_textEdit);
        username_textEdit = findViewById(R.id.username_textEdit);
        password_textEdit = findViewById(R.id.password_textEdit);
        old_password_textEdit = findViewById(R.id.oldpassword_textEdit);

        saveChangesButton = findViewById(R.id.saveChangesButton);
        deleteAccount = findViewById(R.id.deleteAccountButton);
        // Delete Account onClick Listener
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        // Test JsonObject TODO: Remove this later
        requestBody = new JSONObject();
        try {
            requestBody.put("accountUsername", "John");
            requestBody.put("accountPassword", "J0hn");
            requestBody.put("accountEmail", "Anon@test.com");
            // Add more key-value pairs as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSettings() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("accountUsername", username_textEdit.getText().toString().trim());
            requestBody.put("accountPassword", email_textEdit.getText().toString().trim());
            requestBody.put("accountEmail", password_textEdit.getText().toString().trim());

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(UserSettingsPage.this, "Settings updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UserSettingsPage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json"); // Ensure proper content type
                    return headers;
                }
            };

            // Add request to queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(putRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(UserSettingsPage.this, "Error creating request body", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * DEL JsonObj request method, deletes a specified user
     */
    private void deleteUser() {
        JsonObjectRequest delRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                requestBody, // TODO: Change null to actual value
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(UserSettingsPage.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserSettingsPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(delRequest);
    }
}