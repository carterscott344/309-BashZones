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
    private static final String url = "";

    // Important views TODO: Carter add your EditText and Button objects here
    Button deleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_page);

        // Set orientation to horizontal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize important views
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
            requestBody.put("username", "John");
            requestBody.put("password", "J0hn");
            requestBody.put("email", "Anon@test.com");
            // Add more key-value pairs as needed
        } catch (Exception e) {
            e.printStackTrace();
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