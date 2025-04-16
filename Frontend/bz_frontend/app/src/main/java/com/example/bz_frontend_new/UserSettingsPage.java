package com.example.bz_frontend_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserSettingsPage extends AppCompatActivity {

    // PUT URL Endpoint
    private static final String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/updateUser/";

    // DEL URL Endpoint
    private static final String delUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/deleteUser/";

    // Important views
    Button saveChangesButton;
    EditText email_textEdit;
    EditText username_textEdit;
    EditText password_textEdit;

    Button change_profile_pic;
    Button deleteAccount;

    // Shared preferences
    SharedPreferences sp;

    private long currentAccountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_page);

        // Set orientation to horizontal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize shared preferences
        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // Get accountId from shared preferences
        currentAccountId = sp.getLong("userID", -1);

        // Initialize important views

        email_textEdit = findViewById(R.id.email_textEdit);
        username_textEdit = findViewById(R.id.username_textEdit);
        password_textEdit = findViewById(R.id.password_textEdit);

        change_profile_pic = findViewById(R.id.changeProfilePicButton); //TODO Demo 4: Add functionality


        saveChangesButton = findViewById(R.id.saveChangesButton);
        deleteAccount = findViewById(R.id.deleteAccountButton);
        // Delete Account onClick Listener
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        // Add click listener for save changes button
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
    }

    private void updateSettings() {
        // Get the text from EditText fields
        String newUsername = username_textEdit.getText().toString().trim();
        String newPassword = password_textEdit.getText().toString().trim();
        String newEmail = email_textEdit.getText().toString().trim();

        JSONObject updateBody = new JSONObject();
        try {
            updateBody.put("accountUsername", newUsername);
            updateBody.put("accountPassword", newPassword);
            updateBody.put("accountEmail", newEmail);

            String finalUrl = url + String.valueOf(currentAccountId);
            System.out.println("PUT URL: " + finalUrl);
            System.out.println("PUT Body: " + updateBody.toString());

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    finalUrl,
                    updateBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(UserSettingsPage.this, "Settings updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UserSettingsPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(putRequest);
        } catch (Exception e) {
            Toast.makeText(UserSettingsPage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * DEL JsonObj request method, deletes a specified user
     */
    private void deleteUser() {
        Log.d("URL", delUrl + String.valueOf(currentAccountId));
        JsonObjectRequest delRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                delUrl + String.valueOf(currentAccountId),
                null, // Null body since we aren't adding anything
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(UserSettingsPage.this, "Account Deletion Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(UserSettingsPage.this, LoginPage.class);
                        startActivity(i);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserSettingsPage.this, "Account Deletion Failed", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(delRequest);
    }
}