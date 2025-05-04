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

/**
 * Represents the Lobby Page of the application where users wait in a matchmaking queue.
 * Handles joining, leaving, polling, and updating the lobby UI based on queue status.
 */
public class LobbyPage extends AppCompatActivity implements WebSocketListener {
    /** Shared preferences for storing login information. */
    private SharedPreferences sp;

    /** ImageViews for each user profile picture */
    private ImageView red1Image, red2Image, blue1Image, blue2Image;
    /** TextViews for each username */
    private TextView red1Name, red2Name, blue1Name, blue2Name, matchmakingText;
    /** Button for leaving the queue */
    private Button leaveButton;

    /** URL and endpoint for accessing queue and information */
    private static final String QUEUE_URL = "http://coms-3090-046.class.las.iastate.edu:8080/queue";
    /** Long used to reference unique User ID number */
    private long userID;
    /** String to reference user's username */
    private String username;
    /** Polling Handler*/
    private Handler handler;
    /** Interval at which the poller polls */
    private static final long POLLING_INTERVAL = 1000; //milliseconds

    /**
     * Called when the activity is created.
     * Initializes the UI, sets up the WebSocket connection, and joins the matchmaking queue.
     *
     * @param savedInstanceState the saved instance state bundle.
     */
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

    /**
     * Sends a POST request to the server to join the matchmaking queue.
     * Registers the user for matchmaking.
     */
    private void joinQueue() {
        StringRequest joinRequest = new StringRequest(
            Request.Method.POST,
            QUEUE_URL + "/join/" + userID,
            response -> Log.d("Queue", "Joined queue successfully"),
            error -> Toast.makeText(this, "Error joining queue", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(this).addToRequestQueue(joinRequest);
    }

    /**
     * Sends a POST request to the server to leave the matchmaking queue.
     * Also disconnects from the WebSocket and finishes the activity.
     */
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

    /**
     * Starts a recurring task that polls the server at a fixed interval to check the queue status.
     */
    private void startPolling() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkQueueStatus();
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        }, POLLING_INTERVAL);
    }

    /**
     * Stops the polling task that checks queue status updates.
     */
    private void stopPolling() {
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a network request to retrieve all users, filters those currently in the queue,
     * and updates the lobby UI accordingly.
     */
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

    /**
     * Updates the lobby UI based on the current players in the queue.
     * Assigns player names and images to the corresponding team slots.
     *
     * @param response JSONObject containing the players currently queued.
     */
    private void updateLobbyUI(JSONObject response) {
        try {
            JSONArray players = response.getJSONArray("players");
            int lobbySize = players.length();

            // Update matchmaking text based on lobby size
            if (lobbySize == 4) {
                leaveButton.setVisibility(INVISIBLE);
                matchmakingText.setText("Game Found!");
                stopPolling(); // Stop the polling to prevent further requests
            } else {
                matchmakingText.setText("Searching for players...");
            }

            // Clear all player slots first
            clearPlayerSlots();

            // Fill red team first (players 0 and 1), then blue team (players 2 and 3)
            for (int i = 0; i < players.length(); i++) {
                JSONObject player = players.getJSONObject(i);
                String playerUsername = player.getString("accountUsername");
                long playerID = player.getLong("accountID");
                boolean isCurrentUser = (playerID == this.userID);

                if (i < 2) {
                    // First two players go to Red team
                    if (i == 0) {
                        // Red 1
                        red1Name.setText(playerUsername);
                        red1Image.setImageResource(R.drawable.wacky_pfp);
                        if (isCurrentUser) {
                            red1Name.setTextColor(Color.GREEN);
                        }
                    } else {
                        // Red 2
                        red2Name.setText(playerUsername);
                        red2Image.setImageResource(R.drawable.wacky_pfp);
                        if (isCurrentUser) {
                            red2Name.setTextColor(Color.GREEN);
                        }
                    }
                } else {
                    // Next two players go to Blue team
                    if (i == 2) {
                        // Blue 1
                        blue1Name.setText(playerUsername);
                        blue1Image.setImageResource(R.drawable.wacky_pfp);
                        if (isCurrentUser) {
                            blue1Name.setTextColor(Color.GREEN);
                        }
                    } else {
                        // Blue 2
                        blue2Name.setText(playerUsername);
                        blue2Image.setImageResource(R.drawable.wacky_pfp);
                        if (isCurrentUser) {
                            blue2Name.setTextColor(Color.GREEN);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a POST request to remove a specific player from the matchmaking queue.
     *
     * @param playerID The ID of the player to remove from the queue.
     */
    private void removePlayerFromQueue(long playerID) {
        StringRequest leaveRequest = new StringRequest(
                Request.Method.POST,
                QUEUE_URL + "/leave/" + playerID,
                response -> Log.d("Queue", "Player " + playerID + " removed from queue"),
                error -> Log.e("Queue", "Error removing player " + playerID + " from queue: " + error.getMessage())
        );
        VolleySingleton.getInstance(this).addToRequestQueue(leaveRequest);
    }


    /**
     * Clears all player slots in the lobby, resetting names and images to default.
     */
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

    /**
     * Called when the activity is destroyed.
     * Ensures that polling is stopped when the activity is no longer active.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPolling();
    }

    /**
     * Callback method triggered when the WebSocket connection is successfully opened.
     *
     * @param handshakedata Data associated with the WebSocket handshake (unused).
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {

    }


    /**
     * Handles incoming WebSocket messages.
     *
     * @param message the WebSocket message received as a JSON string
     */
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

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code the status code representing the reason for closure
     * @param reason a brief description of the reason for closure
     * @param remote true if the closure was initiated remotely (e.g., by the server)
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        // Go back to general page after websocket close
        Intent i = new Intent(LobbyPage.this, GeneralPage.class);
        startActivity(i);
    }

    /**
     * Called when an error occurs during WebSocket communication.
     *
     * @param ex the exception thrown during WebSocket communication
     */
    @Override
    public void onWebSocketError(Exception ex) {

    }
}
