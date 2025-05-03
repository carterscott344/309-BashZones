package com.example.bz_frontend_new;


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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfilePage extends AppCompatActivity {


    private static final String TAG = "ProfilePage";
    private static final String BASE_URL = "http://coms-3090-046.class.las.iastate.edu:8080";


    SharedPreferences sp;
    long userId;
    private EditText usernameInput;
    private Button friendsButton;
    private Button blockedButton;
    private Button addFriendButton;
    private Button blockPlayerButton;
    private Button returnButton;

    private UserListFragment friendsFragment;
    private UserListFragment blockedFragment;
    private UserListFragment currentFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userId = sp.getLong("userID", -1);



        Log.d(TAG, "ProfilePage initialized with user ID: " + userId);


        // Initialize views
        usernameInput = findViewById(R.id.usernameInput);
        friendsButton = findViewById(R.id.friendsButton);
        blockedButton = findViewById(R.id.blockedButton);
        addFriendButton = findViewById(R.id.addFriendButton);
        blockPlayerButton = findViewById(R.id.blockPlayerButton);
        returnButton = findViewById(R.id.returnButton2);


        friendsFragment = new UserListFragment();
        blockedFragment = new UserListFragment();


        returnButton.setOnClickListener(this::returnToGeneral);

        friendsButton.setOnClickListener(v -> {
            Log.d(TAG, "Friends button clicked");
            loadFriendsList();
            showFragment(friendsFragment);
        });


        blockedButton.setOnClickListener(v -> {
            Log.d(TAG, "Blocked users button clicked");
            loadBlockedList();
            showFragment(blockedFragment);
        });


        addFriendButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            List<String> usernames = Arrays.asList(username.split("\\s*,\\s*"));

            if (!usernames.isEmpty() && !usernames.get(0).isEmpty()) {
                Log.d(TAG, "Attempting to add friends: " + usernames);
                findUserIdsByUsernames(usernames, 1);
            } else {
                Toast.makeText(this, "Please enter at least one username", Toast.LENGTH_SHORT).show();
            }
        });



        blockPlayerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            List<String> usernames = Arrays.asList(username.split("\\s*,\\s*"));

            if (!usernames.isEmpty() && !usernames.get(0).isEmpty()) {
                Log.d(TAG, "Attempting to block player: " + usernames);
                findUserIdsByUsernames(usernames, 2);
            } else {
                Toast.makeText(this, "Please enter at least one username", Toast.LENGTH_SHORT).show();
            }
        });


        loadFriendsList();
        showFragment(friendsFragment);
    }


    public long getUserId() {
        return userId;
    }


    private void showFragment(UserListFragment fragment) {
        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, "current_fragment");
        fragmentTransaction.commit();


        if (fragment.getAdapter() != null) {
            fragment.getAdapter().setParentFragment(fragment);
        }
    }


    public void loadFriendsList() {
        String url = BASE_URL + "/accountUsers/" + String.valueOf(userId) + "/listFriends";
        Log.d(TAG, "Loading friends list from: " + url);
        fetchUserList(url, "friends", friendsFragment);
    }


    public void loadBlockedList() {
        String url = BASE_URL + "/accountUsers/" + String.valueOf(userId) + "/listBlockedUsers";
        Log.d(TAG, "Loading blocked users list from: " + url);
        fetchUserList(url, "blocked", blockedFragment);
    }


    public void returnToGeneral(View v) {
        Intent i = new Intent(this, GeneralPage.class);
        startActivity(i);
    }

    private void fetchUserList(String url, String listType, UserListFragment fragment) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "Received response for " + listType + " list: " + response.toString());
                    try {
                        if (response.length() > 0 && (response.get(0) instanceof Integer || response.get(0) instanceof Long)) {
                            Log.d(TAG, "Response contains simple integer values, fetching usernames");
                            // Collect all user IDs
                            List<Long> userIds = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                userIds.add(response.getLong(i));
                            }
                            // Fetch usernames for these IDs
                            fetchUsernames(userIds, formattedResponse -> {
                                fragment.updateUserList(formattedResponse, listType);
                            });
                        } else {
                            fragment.updateUserList(response, listType);
                        }
                    } catch (Exception e) {
                        String errorMsg = "Error processing " + listType + " data: " + e.getMessage();
                        Toast.makeText(ProfilePage.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMsg, e);
                    }
                },
                error -> {
                    String errorMsg = "Error fetching " + listType + " list: " + error.toString();
                    Toast.makeText(ProfilePage.this, "Error fetching user list", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMsg, error);
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void fetchUsernames(List<Long> userIds, UsernameCallback callback) {
        String url = BASE_URL + "/accountUsers/listUsers";
        
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray formattedResponse = new JSONArray();
                        Map<Long, String> userMap = new HashMap<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject user = response.getJSONObject(i);
                            long userId = user.getLong("accountID");
                            String username = user.getString("accountUsername");
                            userMap.put(userId, username);
                        }

                        for (Long userId : userIds) {
                            JSONObject userObj = new JSONObject();
                            userObj.put("id", userId);
                            String username = userMap.getOrDefault(userId, "Unknown User " + userId);
                            userObj.put("accountUsername", username);
                            formattedResponse.put(userObj);
                        }

                        callback.onUsernamesFetched(formattedResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error formatting user data", e);
                    }
                },
                error -> Log.e(TAG, "Error fetching usernames", error)
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private interface UsernameCallback {
        void onUsernamesFetched(JSONArray userList);
    }

    private void findUserIdsByUsernames(List<String> usernames, int type) {
        String url = BASE_URL + "/accountUsers/listUsers";
        Log.d(TAG, "Finding user IDs for usernames: " + usernames.toString() + " at URL: " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "Received user list response: " + response.toString());
                    try {
                        if (response.length() > 0) {
                            if (response.get(0) instanceof Long) {
                                Toast.makeText(ProfilePage.this, "Cannot look up usernames with current response format", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "API returned integers instead of user objects, cannot lookup by username");
                                return;
                            }

                            Map<String, Long> foundUsers = new HashMap<>();

                            List<Long> targetIds = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject user = response.getJSONObject(i);
                                String accountUsername = user.getString("accountUsername");

                                if (usernames.contains(accountUsername)) {
                                    long targetId = user.getLong("accountID");
                                    foundUsers.put(accountUsername, targetId);
                                    targetIds.add(targetId);
                                    Log.d(TAG, "User found: " + accountUsername + " with ID: " + targetId);
                                }
                            }

                            if (type == 1 && !targetIds.isEmpty()) {
                                if (targetIds.size() == 1) {
                                    // Use POST for single friend
                                    addFriend(targetIds.get(0));
                                } else {
                                    // Use PUT for multiple friends
                                    updateFriendsList(targetIds);
                                }
                            } else if (type == 2) {
                                try {
                                    blockUser(targetIds.get(0));
                                }
                                catch (IndexOutOfBoundsException e){
                                    Toast.makeText(ProfilePage.this, "Username not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            for (String username : usernames) {
                                if (!foundUsers.containsKey(username)) {
                                    Toast.makeText(ProfilePage.this, "User not found: " + username, Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Username not found: " + username);
                                }
                            }
                        } else {
                            Toast.makeText(ProfilePage.this, "No users found", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Empty user list returned from API");
                        }
                    } catch (JSONException e) {
                        Toast.makeText(ProfilePage.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error parsing user list: " + e.getMessage(), e);
                    }
                },
                error -> {
                    Toast.makeText(ProfilePage.this, "Error fetching user list", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user list: " + error.toString(), error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    private void addFriend(long targetId) {
        String url = BASE_URL + "/accountUsers/" + userId + "/addFriend/" + targetId;
        Log.d(TAG, "Adding friend with ID: " + targetId + " at URL: " + url);
        performUserAction(url, "Friend added successfully");
    }


    private void blockUser(long targetId) {
        String url = BASE_URL + "/accountUsers/" + userId + "/addBlockedUser/" + targetId;
        Log.d(TAG, "Blocking user with ID: " + targetId + " at URL: " + url);
        performUserAction(url, "User blocked successfully");
    }

    private void updateFriendsList(List<Long> targetIds) {
        String url = BASE_URL + "/accountUsers/" + userId + "/updateFriendsList";
        
        
        JSONArray jsonArray = new JSONArray(targetIds);
        
        Log.d(TAG, "Sending friend list update request with body: " + jsonArray.toString());

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.PUT,
                url,
                jsonArray,
                response -> {
                    Log.d(TAG, "Friends list updated successfully");
                    Toast.makeText(ProfilePage.this, "Friends added successfully", Toast.LENGTH_SHORT).show();
                    usernameInput.setText("");
                    loadFriendsList();
                    if (currentFragment == friendsFragment) {
                        showFragment(friendsFragment);
                    }
                },
                error -> {
                    if (error instanceof ParseError) {
                        Log.e(TAG, "Parse error, but continuing: " + error.toString(), error);
                        loadFriendsList();
                    } else {
                        String errorMsg = "Failed to update friends list: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                        Toast.makeText(ProfilePage.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to update friends list: " + error.toString(), error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    private void performUserAction(String url, String successMessage) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    Log.d(TAG, "User action successful: " + successMessage + ", Response: " + response.toString());
                    Toast.makeText(ProfilePage.this, successMessage, Toast.LENGTH_SHORT).show();
                    usernameInput.setText("");
                    if (successMessage.contains("Friend")) {
                        loadFriendsList();
                        if (currentFragment == friendsFragment) {
                            showFragment(friendsFragment);
                        }
                    } else {
                        loadBlockedList();
                        if (currentFragment == blockedFragment) {
                            showFragment(blockedFragment);
                        }
                    }
                },
                error -> {
                    // Toast made an error popup from server response but code still ran, this just disables the popup
                    if (error instanceof ParseError) {
                        Log.e(TAG, "Parse error, but continuing: " + error.toString(), error);
                        loadFriendsList();
                    } else {
                        String errorMsg = "Action failed: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                        Toast.makeText(ProfilePage.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Action failed: " + error.toString(), error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
