package com.example.bz_frontend_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;
import com.android.volley.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the User Settings Page where users can:
 * - Update their email, username, and password
 * - Upload a profile picture
 * - Delete their account
 *
 */
public class UserSettingsPage extends AppCompatActivity {

    /**
     * URL endpoint for updating user information on the server.
     */
    private static final String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/updateUser/";

    /**
     * Request code for selecting an image from the device gallery.
     */
    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * URL endpoint for uploading a profile picture to the server.
     */
    private static final String deletePfpURL = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/uploadProfilePicture/";

    /**
     * URL endpoint for deleting a user account from the server.
     */
    private static final String delUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/deleteUser/";

    /**
     * URI of the selected image from the device gallery.
     */
    private static Uri selectedImageUri = null;

    /**
     * Byte array representation of the selected image.
     */
    private byte[] selectedImageBytes = null;

    /**
     * Button for saving changes made by the user.
     */
    Button saveChangesButton;

    /**
     * EditText field for entering or displaying the user's email address.
     */
    EditText email_textEdit;

    /**
     * EditText field for entering or displaying the user's username.
     */
    EditText username_textEdit;

    /**
     * EditText field for entering or displaying the user's password.
     */
    EditText password_textEdit;

    /**
     * Button to trigger the action of changing the profile picture.
     */
    Button change_profile_pic;

    /**
     * Button to trigger the action of deleting the user's account.
     */
    Button deleteAccount;

    /**
     * SharedPreferences instance for storing user data locally on the device.
     */
    SharedPreferences sp;

    /**
     * ID of the currently logged-in user, retrieved from SharedPreferences.
     */
    private long currentAccountId;

    /**
     * Initializes the activity, setting up the layout, listeners, and fetching user data.
     *
     * @param savedInstanceState the saved instance state bundle q
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_page);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        currentAccountId = sp.getLong("userID", -1);

        email_textEdit = findViewById(R.id.email_textEdit);
        username_textEdit = findViewById(R.id.username_textEdit);
        password_textEdit = findViewById(R.id.password_textEdit);
        change_profile_pic = findViewById(R.id.changeProfilePicButton);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        deleteAccount = findViewById(R.id.deleteAccountButton);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();

                if (selectedImageBytes != null) {
                    String uploadUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + currentAccountId + "/uploadProfilePicture";

                    VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                            Request.Method.POST,
                            uploadUrl,
                            selectedImageBytes,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(UserSettingsPage.this, "Profile picture uploaded!", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(UserSettingsPage.this, "Profile picture upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
                }
            }
        });

        change_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    /**
     * Updates the user's email, username, and password on the server.
     */
    private void updateSettings() {
        String newUsername = username_textEdit.getText().toString().trim();
        String newPassword = password_textEdit.getText().toString().trim();
        String newEmail = email_textEdit.getText().toString().trim();

        JSONObject updateBody = new JSONObject();
        try {
            updateBody.put("accountUsername", newUsername);
            updateBody.put("accountPassword", newPassword);
            updateBody.put("accountEmail", newEmail);

            String finalUrl = url + currentAccountId;

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    finalUrl,
                    updateBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(UserSettingsPage.this, "Settings updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UserSettingsPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(putRequest);
        } catch (Exception e) {
            Toast.makeText(UserSettingsPage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends a DELETE request to remove the user's account from the server.
     */
    private void deleteUser() {
        Log.d("URL", delUrl + currentAccountId);
        JsonObjectRequest delRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                delUrl + currentAccountId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(UserSettingsPage.this, "Account Deletion Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(UserSettingsPage.this, LoginPage.class);
                        startActivity(i);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserSettingsPage.this, "Account Deletion Failed", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(delRequest);
    }

    /**
     * Opens the device gallery allowing the user to select a profile picture.
     */
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result from the image picker activity.
     *
     * @param requestCode The request code passed when starting the activity.
     * @param resultCode The result code returned by the child activity.
     * @param data The Intent returned from the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                selectedImageBytes = getBytes(inputStream);
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Converts an InputStream into a byte array.
     *
     * @param inputStream The InputStream to convert.
     * @return Byte array of the input stream's contents.
     * @throws IOException if an I/O error occurs.
     */
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
