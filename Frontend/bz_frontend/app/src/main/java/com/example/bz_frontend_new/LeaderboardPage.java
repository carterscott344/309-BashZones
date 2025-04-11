package com.example.bz_frontend_new;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardPage extends AppCompatActivity {
    private RecyclerView leaderboardRecyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private Button return_button;
    private static final String API_URL = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/listUsers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize RecyclerView
        leaderboardRecyclerView = findViewById(R.id.leaderboard_recycler_view);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardAdapter = new LeaderboardAdapter();
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

        return_button = findViewById(R.id.return_button);
        return_button.setOnClickListener(this::returnToGeneral);

        fetchLeaderboardData();
    }

    private void fetchLeaderboardData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            API_URL,
            null,
            response -> {
                try {
                    List<LeaderboardEntry> entries = new ArrayList<>();
                    
                    // Process each user in the response
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject user = response.getJSONObject(i);
                        entries.add(new LeaderboardEntry(
                            0, // temporary rank
                            user.getString("accountUsername"),
                            user.getInt("numKills"),
                            user.getInt("numDeaths"),
                            user.getDouble("killDeathRatio")
                        ));
                    }

                    // Sort by K/D ratio
                    entries.sort((a, b) -> Double.compare(b.getKdRatio(), a.getKdRatio()));

                    // Assign ranks
                    List<LeaderboardEntry> rankedEntries = new ArrayList<>();
                    for (int i = 0; i < entries.size(); i++) {
                        LeaderboardEntry entry = entries.get(i);
                        rankedEntries.add(new LeaderboardEntry(
                            i + 1,
                            entry.getUsername(),
                            entry.getKills(),
                            entry.getDeaths(),
                            entry.getKdRatio()
                        ));
                    }

                    leaderboardAdapter.updateEntries(rankedEntries);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            },
            error -> {
                error.printStackTrace();
                Toast.makeText(this, "Error fetching leaderboard data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return new HashMap<>();
            }

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    public void returnToGeneral(View v) {
        Intent i = new Intent(this, GeneralPage.class);
        startActivity(i);
    }

    // Inner class for LeaderboardEntry
    private static class LeaderboardEntry {
        private int rank;
        private String username;
        private int kills;
        private int deaths;
        private double kdRatio;

        public LeaderboardEntry(int rank, String username, int kills, int deaths, double kdRatio) {
            this.rank = rank;
            this.username = username;
            this.kills = kills;
            this.deaths = deaths;
            this.kdRatio = kdRatio;
        }

        public int getRank() { return rank; }
        public String getUsername() { return username; }
        public int getKills() { return kills; }
        public int getDeaths() { return deaths; }
        public double getKdRatio() { return kdRatio; }
    }

    // Inner class for LeaderboardAdapter
    private class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
        private List<LeaderboardEntry> entries = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView rankText, usernameText, killsText, deathsText, kdRatioText;

            public ViewHolder(View itemView) {
                super(itemView);
                rankText = itemView.findViewById(R.id.rank_number);
                usernameText = itemView.findViewById(R.id.username);
                killsText = itemView.findViewById(R.id.kills_text);
                deathsText = itemView.findViewById(R.id.deaths_text);
                kdRatioText = itemView.findViewById(R.id.kd_ratio);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.leaderboard_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LeaderboardEntry entry = entries.get(position);
            holder.rankText.setText(String.valueOf(entry.getRank()));
            holder.usernameText.setText(entry.getUsername());
            holder.killsText.setText(String.valueOf(entry.getKills()));
            holder.deathsText.setText(String.valueOf(entry.getDeaths()));
            holder.kdRatioText.setText(String.format("%.2f", entry.getKdRatio()));
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        public void updateEntries(List<LeaderboardEntry> newEntries) {
            entries = newEntries;
            notifyDataSetChanged();
        }
    }
}
