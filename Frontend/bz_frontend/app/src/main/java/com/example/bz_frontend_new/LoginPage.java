package com.example.bz_frontend_new;

import static java.util.logging.Logger.global;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the login page activity for the application.
 * Handles user login validation with remote server,
 * manages shared preferences for storing user credentials,
 * and navigates to other activities based on button press or successful login.
 */
public class LoginPage extends AppCompatActivity {

    /**
     * Shared preferences for storing login information.
     * */
    SharedPreferences sp;

    /**
     * URL endpoint for fetching user accounts.
     * */
    private static final String url =
            "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/listUsers";

    /**
     * EditText for entering username.
     */
    private EditText username;

    /**
     * EditText for entering password.
     */
    private EditText password;

    /**
     * Button for triggering login process.
     */
    private Button login_button;

    /**
     * Stores the user's ID after successful login.
     */
    private long userID;

    /**
     * Button for navigating to the signup page.
     */
    private Button signup_button;

    /**
     * Initializes the activity, sets layout, orientation,
     * and configures UI elements and click listeners.
     *
     * @param savedInstanceState the saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        signup_button = findViewById(R.id.signup_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser(username.getText().toString(), password.getText().toString());
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Validates user credentials by fetching account information
     * from server and comparing against input values.
     *
     * If validation succeeds, stores user info in shared preferences
     * and navigates to the general page. Otherwise, displays an error message.
     *
     * @param usernameT the entered username
     * @param passwordT the entered password
     */
    private void validateUser(final String usernameT, final String passwordT) {
        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            boolean isValidUser = false;
                            boolean isBannedUser = false;

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);
                                String serverUsername = userObject.getString("accountUsername");
                                String serverPassword = userObject.getString("accountPassword");
                                userID = userObject.getLong("accountID");
                                int accountBalance = userObject.getInt("gemBalance");
                                String accountType = userObject.getString("accountType");

                                if(serverUsername.equals(usernameT) && serverPassword.equals(passwordT) && userObject.getBoolean("isBanned")){
                                   isBannedUser = true;
                                }
                                else if (serverUsername.equals(usernameT) && serverPassword.equals(passwordT)) {
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putLong("userID", userID);
                                    editor.putString("username", serverUsername);
                                    editor.putString("password", serverPassword);
                                    editor.putInt("balance", accountBalance);
                                    editor.putString("accountType", accountType);
                                    editor.commit();

                                    isValidUser = true;
                                    break;
                                }
                            }

                            if (isValidUser) {
                                setOnline(userID);

                                Intent intent = new Intent(LoginPage.this, GeneralPage.class);
                                startActivity(intent);
                            }
                            else if (isBannedUser){
                                Toast.makeText(getApplicationContext(), "Your account is banned", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error with response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LOGIN_DEBUG", "Error validating credentials: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error validating credentials: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            /**
             * Returns additional headers to be included with the request.
             *
             * @return map of headers
             * @throws AuthFailureError if authentication fails
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                return headers;
            }

            /**
             * Returns additional parameters to be included with the request.
             *
             * @return map of parameters
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }

    public void setOnline(long ID){
        String url =  "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + ID + "/goOnline";
        JsonObjectRequest putRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LoginPage", "Player "+ ID + ". Set isOnline to true.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {};

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(putRequest);
    }
}
