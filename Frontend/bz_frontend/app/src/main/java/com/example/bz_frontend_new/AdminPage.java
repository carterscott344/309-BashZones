package com.example.bz_frontend_new;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminPage extends AppCompatActivity {

    private final String BASE_URL = "http://coms-3090-046.class.las.iastate.edu:8080";
    private Button returnButton;
    private UserListFragment playerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        returnButton = findViewById(R.id.backButton);
        returnButton.setOnClickListener(this::returnToGeneral);

        playerFragment = new UserListFragment() {
            @Override
            public void updateUserList(JSONArray jsonArray, String type) {
                super.updateUserList(jsonArray, type);
                // Override to customize the list items for admin view
                if (getAdapter() != null) {
                    getAdapter().setParentFragment(this);
                }
            }
        };

        loadPlayerList();
        showFragment(playerFragment);
    }

    public void returnToGeneral(View v) {
        Intent i = new Intent(this, GeneralPage.class);
        startActivity(i);
    }

    private void loadPlayerList() {
        String url = BASE_URL + "/accountUsers/listUsers";
        Log.d("AdminPage", "Loading player list from: " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("AdminPage", "Received player list: " + response.toString());
                    try {
                        JSONArray formattedResponse = new JSONArray();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject user = response.getJSONObject(i);
                            JSONObject formattedUser = new JSONObject();
                            formattedUser.put("id", user.getLong("accountID"));
                            formattedUser.put("accountUsername", user.getString("accountUsername"));
                            formattedUser.put("isBanned", user.getBoolean("isBanned"));
                            formattedResponse.put(formattedUser);
                        }
                        playerFragment.updateUserList(formattedResponse, "players");
                    } catch (JSONException e) {
                        Toast.makeText(AdminPage.this, "Error parsing player data", Toast.LENGTH_SHORT).show();
                        Log.e("AdminPage", "Error parsing player data", e);
                    }
                },
                error -> {
                    Toast.makeText(AdminPage.this, "Error fetching player list", Toast.LENGTH_SHORT).show();
                    Log.e("AdminPage", "Error fetching player list: " + error.toString());
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void showFragment(UserListFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, "player_fragment");
        fragmentTransaction.commit();

        // Create a custom adapter for admin view
        if (fragment.getAdapter() != null) {
            fragment.getAdapter().setParentFragment(fragment);
            fragment.getAdapter().setAdminMode(true);
        }
    }

    void ban_user(long ID) {
        String url = BASE_URL + "/accountUsers/banUser/" + ID;
        Log.d("AdminPage", "Banning user with ID: " + ID);
        performBanAction(url, "User banned successfully", ID, true);
    }

    void unban_user(long ID) {
        String url = BASE_URL + "/accountUsers/unbanUser/" + ID;
        Log.d("AdminPage", "Unbanning user with ID: " + ID);
        performBanAction(url, "User unbanned successfully", ID, false);
    }

    private void performBanAction(String url, String successMessage, long userId, boolean isBan) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    Log.d("AdminPage", successMessage + ", Response: " + response.toString());
                    Toast.makeText(AdminPage.this, successMessage, Toast.LENGTH_SHORT).show();
                    loadPlayerList(); // Refresh the list after ban/unban
                },
                error -> {
                    String errorMsg = "Action failed: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(AdminPage.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("AdminPage", "Action failed: " + error.toString());
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}