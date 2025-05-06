package com.example.bz_frontend_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminPage extends AppCompatActivity {

    private final String BASE_URL = "http://coms-3090-046.class.las.iastate.edu:8080";
    private Button returnButton;
    private UserListFragment playerFragment;

    private EditText searchBar;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        returnButton = findViewById(R.id.backButton);
        returnButton.setOnClickListener(this::returnToGeneral);

        searchBar = findViewById(R.id.username_input);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadPlayerList(); // reload list when text changes
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        playerFragment = new UserListFragment();
        showFragment(playerFragment);

        loadPlayerList();
        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
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
                        String searchQuery = searchBar.getText().toString().toLowerCase(); // New line
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject user = response.getJSONObject(i);
                            String username = user.getString("accountUsername"); // New line
                            if(user.getLong("accountID") == sp.getLong("userID",-1)){
                                continue;
                            }
                            if (!username.toLowerCase().contains(searchQuery)) { // New line
                                continue; // New line
                            }

                            JSONObject formattedUser = new JSONObject();
                            formattedUser.put("id", user.getLong("accountID"));
                            formattedUser.put("accountUsername", username);
                            formattedUser.put("isBanned", user.getBoolean("isBanned"));
                            formattedResponse.put(formattedUser);
                        }
                        playerFragment.updateUserList(formattedResponse, "players");
                        playerFragment.setAdminMode(true);
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

        if (fragment.getAdapter() != null) {
            fragment.getAdapter().setParentFragment(fragment);
            fragment.getAdapter().setAdminMode(true);
        }
    }

    void ban_user(long ID) {
        String url = BASE_URL + "/accountUsers/" + Long.toString(ID) +"/ban";
        Log.d("AdminPage", "Banning user with ID: " + ID);
        performBanAction(url, "User banned successfully");
    }

    void unban_user(long ID) {
        String url = BASE_URL + "/accountUsers/" + Long.toString(ID) +"/unban";
        Log.d("AdminPage", "Unbanning user with ID: " + ID);
        performBanAction(url, "User unbanned successfully");
    }

    void delete_pfp(long ID){
        String url = BASE_URL + "/accountUsers/" + Long.toString(ID) + "/deleteProfilePicture";
        Log.d("AdminPage", "Deleted user profile picture");
        performPfpAction(url, "Deleted user profile picture");
    }

    private void performBanAction(String url, String successMessage) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
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

    private void performPfpAction(String url, String successMessage) {
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    Log.d("AdminPage", successMessage + ", Response: " + response.toString());
                    Toast.makeText(AdminPage.this, successMessage, Toast.LENGTH_SHORT).show();
                    loadPlayerList(); // Refresh the list
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


}
