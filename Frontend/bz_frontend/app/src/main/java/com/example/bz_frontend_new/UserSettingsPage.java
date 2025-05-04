package com.example.bz_frontend_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class UserSettingsPage extends AppCompatActivity {

    private static final String url = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/updateUser/";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String delUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/deleteUser/";
    private static Uri selectedImageUri = null;
    private File selectedImageFile = null;

    Button saveChangesButton;
    EditText email_textEdit;
    EditText username_textEdit;
    EditText password_textEdit;
    Button change_profile_pic;
    Button deleteAccount;
    ImageView profilePicture;
    SharedPreferences sp;
    Button backkers;
    private long currentAccountId;

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
        profilePicture = findViewById((R.id.currentPfp));
        backkers = findViewById(R.id.backkers);

        backkers.setOnClickListener(this::returnToGeneral);

        // Load current profile picture using ByteArrayRequest
        Request<byte[]> byteRequest = new Request<byte[]>(
                Request.Method.GET,
                "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + currentAccountId + "/profilePicture",
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
                new ImageLoaderTask(profilePicture).execute(response);
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(byteRequest);

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

                if (selectedImageFile != null) {
                    String uploadUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + currentAccountId + "/uploadProfilePicture";

                    VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                            Request.Method.POST,
                            uploadUrl,
                            selectedImageFile,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(UserSettingsPage.this, "Profile picture uploaded!", Toast.LENGTH_SHORT).show();
                                    // Refresh the profile picture using ByteArrayRequest
                                    Request<byte[]> refreshRequest = new Request<byte[]>(
                                            Request.Method.GET,
                                            "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + currentAccountId + "/profilePicture",
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e("ProfilePicture", "Error refreshing profile picture: " + error.getMessage());
                                                }
                                            }
                                    ) {
                                        @Override
                                        protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                                            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
                                        }

                                        @Override
                                        protected void deliverResponse(byte[] response) {
                                            new ImageLoaderTask(profilePicture).execute(response);
                                        }
                                    };
                                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(refreshRequest);
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

    public void returnToGeneral(View v) {
        Intent i = new Intent(this, GeneralPage.class);
        startActivity(i);
    }

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
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
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

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                profilePicture.setImageURI(selectedImageUri);
                selectedImageFile = createTempImageFile(selectedImageUri);
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createTempImageFile(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        File file = new File(getCacheDir(), "temp_profile_pic.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        outputStream.flush();
        outputStream.close();

        return file;
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