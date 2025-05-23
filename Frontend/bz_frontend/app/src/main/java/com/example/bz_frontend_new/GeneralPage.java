package com.example.bz_frontend_new;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GeneralPage extends AppCompatActivity {

    // URL for user accounts
    String userAccountsUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers";

    // Shared preferences
    SharedPreferences sp;

    // UserID
    private long userID;

    // User Gem Balance
    private int balance;

    // Important views
    ImageButton settings_button;
    ImageButton profile_button;
    ImageButton leaderboard_button;
    ImageButton shop_button;
    ImageButton rndm_mm_button;
    ImageButton admin_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_page);

        // Initialize important views
        settings_button = findViewById(R.id.settings_button);
        profile_button = findViewById(R.id.profile_button);
        leaderboard_button = findViewById(R.id.leaderboard_button);
        shop_button = findViewById(R.id.shop_button);
        rndm_mm_button = findViewById(R.id.random_mm_button);
        admin_button = findViewById(R.id.admin_button);

        // Set horizontal orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize shared preferences
        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        String accountType = sp.getString("accountType", "Standard");

        if(accountType.equals("Standard") || accountType.equals("Child")){
            admin_button.setVisibility(View.GONE);
        }
        else{
            admin_button.setVisibility(View.VISIBLE);
        }

        // UserID
        userID = sp.getLong("userID", -1);

        // Gem Balance
        balance = sp.getInt("balance", 0);

        // Set onClick listeners
        settings_button.setOnClickListener(this::launchSettings);
        profile_button.setOnClickListener(this::launchProfile);
        leaderboard_button.setOnClickListener(this::launchLeaderboard);

        shop_button.setOnClickListener(this::launchShop);
        rndm_mm_button.setOnClickListener(this::giveMoney);
        rndm_mm_button.setOnClickListener(this::launchLobby);

        admin_button.setOnClickListener(this::launchAdmin);
    }
    // Activity launching methods
    public void launchSettings(View v) {
        Intent i = new Intent(this, SettingsPage.class);
        startActivity(i);
    }

    public void launchAdmin(View v){
        Intent i = new Intent(this, AdminPage.class);
        startActivity(i);
    }

    public void launchProfile(View v) {
        Intent i = new Intent(this, ProfilePage.class);
        i.putExtra("USER_ID", getIntent().getIntExtra("USER_ID",-1));
        startActivity(i);
    }

    public void launchLeaderboard(View v){
        Intent i = new Intent(this, LeaderboardPage.class);
        startActivity(i);
    }

    public void launchShop(View v) {
        Intent i = new Intent(this, ShopPage.class);
        startActivity(i);
    }

    public void launchLobby(View v){
        Intent i = new Intent(this, LobbyPage.class);
        startActivity(i);
    }

    // Placeholder method to give the user gems for clicking on matchmaking
    public void giveMoney(View v) {
        JSONObject moneyAddObj = new JSONObject();

        balance = sp.getInt("balance", 0);

        try {
            moneyAddObj.put("gemBalance", balance + 2000);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest moneyAddReq = new JsonObjectRequest(
                Request.Method.PUT,
                userAccountsUrl + "/updateUser/" + String.valueOf(userID),
                moneyAddObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Added Money!", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("balance", balance + 2000);
                        editor.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Giving Money!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(moneyAddReq);
    }
}