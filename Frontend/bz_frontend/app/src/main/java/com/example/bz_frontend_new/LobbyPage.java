package com.example.bz_frontend_new;

import static android.view.View.INVISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.os.Handler;
import android.os.Looper;

public class LobbyPage extends AppCompatActivity implements WebSocketListener {
    private SharedPreferences sp;

    private ImageView red1Image, red2Image, blue1Image, blue2Image;
    private TextView red1Name, red2Name, blue1Name, blue2Name, matchmakingText;
    private Button leaveButton;

    private static final String QUEUE_URL = "http://coms-3090-046.class.las.iastate.edu:8080/queue";
    private long userID;
    private String username;
    private Handler handler;
    private static final long POLLING_INTERVAL = 1000; //milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        red1Image = findViewById(R.id.red_1_image);
        red2Image = findViewById(R.id.red_2_image);
        blue1Image = findViewById(R.id.blu_1_image);
        blue2Image = findViewById(R.id.blu_2_image);

        // Initialize TextViews
        red1Name = findViewById(R.id.red_1_name);
        red2Name = findViewById(R.id.red_2_name);
        blue1Name = findViewById(R.id.blu_1_name);
        blue2Name = findViewById(R.id.blu_2_name);
        matchmakingText = findViewById(R.id.matchmaking_text);

        leaveButton = findViewById(R.id.leave_button);

        // Get user info from shared preferences
        userID = sp.getLong("userID", -1);
        username = sp.getString("username", "");

        // Initialize handler for polling
        handler = new Handler(Looper.getMainLooper());

        // Connect to websocket
        WebSocketManager.getInstance().connectWebSocket("ws://coms-3090-046.class.las.iastate.edu/ws/connectToServer");
        WebSocketManager.getInstance().setWebSocketListener(this);

        // Join queue
        joinQueue();

        // Set up leave button listener
        leaveButton.setOnClickListener(v -> leaveQueue());


        startPolling();
    }

    private void joinQueue() {
        StringRequest joinRequest = new StringRequest(
            Request.Method.POST,
            QUEUE_URL + "/join/" + userID,
            response -> Log.d("Queue", "Joined queue successfully"),
            error -> Toast.makeText(this, "Error joining queue", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(this).addToRequestQueue(joinRequest);
    }

    private void leaveQueue() {
        StringRequest leaveRequest = new StringRequest(
                Request.Method.POST,
                QUEUE_URL + "/leave/" + userID,
                response -> {
                    stopPolling();
                    finish();
                },
                error -> Toast.makeText(this, "Error leaving queue", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(this).addToRequestQueue(leaveRequest);

        // Disconnect from websocket
        WebSocketManager.getInstance().disconnectWebSocket();
    }

    private void startPolling() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkQueueStatus();
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        }, POLLING_INTERVAL);
    }

    private void stopPolling() {
        handler.removeCallbacksAndMessages(null);
    }

    private void checkQueueStatus() {
        StringRequest usersRequest = new StringRequest(
            Request.Method.GET,
            "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/listUsers",
            response -> {
                try {
                    // Parse the response as a JSON array directly
                    JSONArray allUsers = new JSONArray(response);
                    JSONArray queuedPlayers = new JSONArray();
                    
                    // Filter for players in queue
                    for (int i = 0; i < allUsers.length(); i++) {
                        JSONObject user = allUsers.getJSONObject(i);
                        if (user.getBoolean("isInQueue")) {
                            queuedPlayers.put(user);
                        }
                    }
                    
                    // Create response object with queued players
                    JSONObject queueResponse = new JSONObject();
                    queueResponse.put("size", queuedPlayers.length());
                    queueResponse.put("players", queuedPlayers);
                    
                    updateLobbyUI(queueResponse);
                } catch (Exception e) {
                    Log.e("Queue", "Error processing users: " + e.getMessage());
                }
            },
            error -> Log.e("Queue", "Error getting users: " + error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(usersRequest);
    }

    private void updateLobbyUI(JSONObject response) {
        try {
            JSONArray players = response.getJSONArray("players");
            int lobbySize = players.length();

            // Update matchmaking text based on lobby size
            if (lobbySize == 4) {
                leaveButton.setVisibility(INVISIBLE);
                matchmakingText.setText("Game Found!");



//                // Remove all players from queue by sending requests for each player
//                for (int i = 0; i < players.length(); i++) {
//                    JSONObject player = players.getJSONObject(i);
//                    long playerID = player.getLong("accountID");
//                    removePlayerFromQueue(playerID);
//                }
//                removePlayerFromQueue(userID);
//
                stopPolling(); // Stop the polling to prevent further requests
//

            } else {
                matchmakingText.setText("Searching for players...");
            }

            // Rest of the method (clearing slots and filling player information)
            clearPlayerSlots();

            // Track which slots are filled
            boolean red1Filled = false;
            boolean red2Filled = false;
            boolean blue1Filled = false;
            boolean blue2Filled = false;

            // First pass: Fill existing slots
            for (int i = 0; i < players.length(); i++) {
                JSONObject player = players.getJSONObject(i);
                String playerUsername = player.getString("accountUsername");
                long playerID = player.getLong("accountID");

                // Check if this is the current user
                boolean isCurrentUser = playerID == this.userID;

                // Assign players to slots based on position
                if (i == 0) {
                    red1Name.setText(playerUsername);
                    red1Image.setImageResource(R.drawable.wacky_pfp);
                    red1Filled = true;
                    if (isCurrentUser) {
                        red1Name.setTextColor(Color.GREEN); // Highlight current user
                    }
                } else if (i == 1) {
                    red2Name.setText(playerUsername);
                    red2Image.setImageResource(R.drawable.wacky_pfp);
                    red2Filled = true;
                    if (isCurrentUser) {
                        red2Name.setTextColor(Color.GREEN);
                    }
                } else if (i == 2) {
                    blue1Name.setText(playerUsername);
                    blue1Image.setImageResource(R.drawable.wacky_pfp);
                    blue1Filled = true;
                    if (isCurrentUser) {
                        blue1Name.setTextColor(Color.GREEN);
                    }
                } else if (i == 3) {
                    blue2Name.setText(playerUsername);
                    blue2Image.setImageResource(R.drawable.wacky_pfp);
                    blue2Filled = true;
                    if (isCurrentUser) {
                        blue2Name.setTextColor(Color.GREEN);
                    }
                }
            }

            // Second pass: If current user not assigned, assign to first available slot
            boolean currentUserAssigned = false;
            for (int i = 0; i < players.length(); i++) {
                JSONObject player = players.getJSONObject(i);
                long playerID = player.getLong("accountID");
                if (playerID == this.userID) {
                    currentUserAssigned = true;
                    break;
                }
            }

            if (!currentUserAssigned) {
                // Try to assign to first available slot
                if (!red1Filled) {
                    red1Name.setText(username);
                    red1Name.setTextColor(Color.GREEN);
                    red1Filled = true;
                } else if (!red2Filled) {
                    red2Name.setText(username);
                    red2Name.setTextColor(Color.GREEN);
                    red2Filled = true;
                } else if (!blue1Filled) {
                    blue1Name.setText(username);
                    blue1Name.setTextColor(Color.GREEN);
                    blue1Filled = true;
                } else if (!blue2Filled) {
                    blue2Name.setText(username);
                    blue2Name.setTextColor(Color.GREEN);
                    blue2Filled = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removePlayerFromQueue(long playerID) {
        StringRequest leaveRequest = new StringRequest(
                Request.Method.POST,
                QUEUE_URL + "/leave/" + playerID,
                response -> Log.d("Queue", "Player " + playerID + " removed from queue"),
                error -> Log.e("Queue", "Error removing player " + playerID + " from queue: " + error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(leaveRequest);
    }

    private void clearPlayerSlots() {
        red1Name.setText("Searching...");
        red2Name.setText("Searching...");
        blue1Name.setText("Searching...");
        blue2Name.setText("Searching...");
        red1Image.setImageResource(R.drawable.default_profile);
        red2Image.setImageResource(R.drawable.default_profile);
        blue1Image.setImageResource(R.drawable.default_profile);
        blue2Image.setImageResource(R.drawable.default_profile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPolling();
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {

    }

    // Listens for message giving match ID when lobby is full
    @Override
    public void onWebSocketMessage(String message) {
        try {
            JSONObject messageObj = new JSONObject(message);
            if (messageObj.getString("type").equals("matchBroadcast")) {
                // Get match ID
                String matchID = messageObj.getString("matchID");
                // Store match ID for strings
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("currentMatchID", matchID);
                editor.commit();

                // Send server message to join game
                JSONObject joinObj = new JSONObject();
                joinObj.put("type", "join");
                joinObj.put("matchID", matchID);
                joinObj.put("userID", userID);
                String joinMessage = joinObj.toString();
                WebSocketManager.getInstance().sendMessage(joinMessage);

                // Go to game launcher
                Intent gameIntent = new Intent(LobbyPage.this, GamePanelLauncher.class);
                startActivity(gameIntent);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        // Go back to general page after websocket close
        Intent i = new Intent(LobbyPage.this, GeneralPage.class);
        startActivity(i);
    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}
