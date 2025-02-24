package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StringReqActivity extends AppCompatActivity {

    private Button btnStringReq;
    private TextView msgResponse;
    private Button returnButton;

    private static final String URL_STRING_REQ = "https://jsonplaceholder.typicode.com/users/1";
    //   public static final String URL_STRING_REQ = "https://2aa87adf-ff7c-45c8-89bc-f3fbfaa16d15.mock.pstmn.io/users/1";
    //   public static final String URL_STRING_REQ = "http://10.0.2.2:8080/users/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_req);

        btnStringReq = (Button) findViewById(R.id.btnStringReq);
        returnButton= (Button) findViewById(R.id.btnReturn);
        msgResponse = (TextView) findViewById(R.id.msgResponse);

        btnStringReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeStringReq();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StringReqActivity.this, MainActivity.class));
            }
        });
    }


    /**
     * Making string request
     **/
    private void makeStringReq() {

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URL_STRING_REQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Extract values
                            int id = jsonObject.getInt("id");
                            String username = jsonObject.getString("username");
                            String name = jsonObject.getString("name");
                            String email = jsonObject.getString("email");

                            // Format and display response
                            String formattedResponse =
                                    "Username: " + username + "\n" +
                                    "AccountID: " + id + "\n" +
                                    "Name: " + name + "\n" +
                                    "Email: " + email;

                            msgResponse.setText(formattedResponse);
                        } catch (JSONException e) {
                            Log.e("JSON Error", "Error parsing JSON", e);
                            msgResponse.setText("Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}