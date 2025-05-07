package com.example.bz_frontend_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsPage extends AppCompatActivity {

    // Button fields
    Button user_settings_button;
    Button return_button;
    Button game_settings_button;
    Button inventory_button;
    Button logout_button;
    SharedPreferences sp;
    Button user_stats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // Set horizontal orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize buttons
        user_settings_button = findViewById(R.id.user_settings_button);
        return_button = findViewById(R.id.returnButton2);
        game_settings_button = findViewById(R.id.game_settings_button);
        inventory_button = findViewById(R.id.inventory_button);
        logout_button = findViewById(R.id.logout_button);
        user_stats = findViewById(R.id.userStats_button);

        // Set onClick listeners
        user_settings_button.setOnClickListener(this::openUserSettings);
        return_button.setOnClickListener(this::returnToGeneral);
        game_settings_button.setOnClickListener(this::openGameSettings);
        inventory_button.setOnClickListener(this::openInventory);
        logout_button.setOnClickListener((this::logout));
        user_stats.setOnClickListener((this::openUserStats));

        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
    }

    // onClick listeners methods
    public void openUserSettings(View v){
        Intent i = new Intent(this, UserSettingsPage.class);
        startActivity(i);
    }

    public void returnToGeneral(View v){
        Intent i = new Intent(this, GeneralPage.class);
        startActivity(i);
    }

    public void openGameSettings(View v){
        Intent i = new Intent(this, GameSettingsPage.class);
        startActivity(i);
    }

    public void openInventory(View v){
        Intent i = new Intent(this, UserInventoryPage.class);
        startActivity(i);
    }

    public void logout(View v){

        setOffline(sp.getLong("userID", -1));

        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(this, LoginPage.class);
        startActivity(i);
    }

    public void openUserStats(View v){
        Intent i = new Intent(this, UserStats.class);
        startActivity(i);
    }


    public void setOffline(long ID){
        String url =  "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + ID + "/goOffline";
        JsonObjectRequest putRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SettingsPage", "Player "+ ID + ". Set isOnline to false.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SettingsPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {};

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(putRequest);
    }
}