package com.example.bz_frontend_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserStats extends AppCompatActivity {
    private ImageView pic;
    private TextView name;
    private TextView totalUserPlayTime;
    private TextView userLevel;
    private TextView currentLevelXP;
    private TextView gemBalance;
    private TextView numKills;
    private TextView numDeaths;
    private TextView killDeathRatio;
    private Button backButton;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        pic = findViewById(R.id.pic);
        name = findViewById(R.id.name);
        totalUserPlayTime = findViewById(R.id.totalUserPlayTime);

        userLevel = findViewById(R.id.userLevel);
        currentLevelXP = findViewById(R.id.currentLevelXP);

        gemBalance = findViewById(R.id.gemBalance);

        numKills = findViewById(R.id.numKills);
        numDeaths = findViewById(R.id.numDeaths);
        killDeathRatio = findViewById(R.id.killDeathRatio);

        backButton = findViewById(R.id.back);



        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        loadStats(sp.getLong("userID",-1));



        backButton.setOnClickListener(this::returnToSettings);
    }




    public void loadStats(Long ID){
        String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/listUser/" + ID;
        requestAndParse(url, ID);

        loadPfp(ID);
    }

    public void returnToSettings(View v) {
        Intent i = new Intent(this, SettingsPage.class);
        startActivity(i);
    }

    public void requestAndParse(String url, Long ID) {
        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // No need to get by index since response is already the user object
                            String username = response.getString("accountUsername");
                            int userLevel1 = response.getInt("userLevel");
                            long currentLevelXP1 = response.getLong("currentLevelXP");
                            int gemBalance1 = response.getInt("gemBalance");
                            int numDeaths1 = response.getInt("numDeaths");
                            int numKills1 = response.getInt("numKills");
                            double killDeathRatio1 = response.getDouble("killDeathRatio");

                            JSONObject playTimeObject = response.getJSONObject("totalUserPlayTime");
                            int days = playTimeObject.getInt("days");
                            int hours = playTimeObject.getInt("hours");
                            int minutes = playTimeObject.getInt("minutes");
                            int seconds = playTimeObject.getInt("seconds");

                            name.setText(username);

                            String text = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
                            totalUserPlayTime.setText(text);

                            text = "Level: " + String.valueOf(userLevel1);
                            userLevel.setText(text);

                            text = "Current Level XP: " + Long.toString(currentLevelXP1);
                            currentLevelXP.setText(text);

                            text = "Gem Balance: " + Integer.toString(gemBalance1);
                            gemBalance.setText(text);

                            text = "Number of Kills: " +Integer.toString(numKills1);
                            numKills.setText(text);

                            text = "Number of Deaths: " +Integer.toString(numDeaths1);
                            numDeaths.setText(text);

                            text = "Kill Death Ratio: " + String.format("%.2f", killDeathRatio1);
                            killDeathRatio.setText(text);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error with response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(UserStats.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }

    public void loadPfp(Long ID){
        Request<byte[]> byteRequest = new Request<byte[]>(
                Request.Method.GET,
                "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + ID + "/profilePicture",
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ProfilePicture", "Error loading profile picture: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(byte[] response) {
                new UserStats.ImageLoaderTask(pic).execute(response);
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(byteRequest);
    }

    private static class ImageLoaderTask extends android.os.AsyncTask<byte[], Void, Bitmap> {
        private final ImageView imageView;

        public ImageLoaderTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(byte[]... data) {
            if (data == null || data.length == 0 || data[0] == null) {
                return null;
            }
            return BitmapFactory.decodeByteArray(data[0], 0, data[0].length);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }




}