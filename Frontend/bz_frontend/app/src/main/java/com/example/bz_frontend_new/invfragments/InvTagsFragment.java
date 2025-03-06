package com.example.bz_frontend_new.invfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bz_frontend_new.R;
import com.example.bz_frontend_new.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvTagsFragment extends Fragment {

    // Server URL for user items
    String userUrl = "http://coms-3090-046.class.las.iastate.edu:8080/userItems";

    // Shared preferences
    SharedPreferences sp;

    // UserID
    long userID;

    // Important fields for inventory items
    ArrayList<InvListData> invListData;
    GridView gridView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize important fields for shop items
        invListData = new ArrayList<>();
        gridView = view.findViewById(R.id.hats_grid_view);

        // Initialize shared preferences and userID
        sp = getContext().getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userID = sp.getLong("userID", -1);

        // Fetch shop data for this fragment
        fetchData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment and return
        return inflater.inflate(R.layout.fragment_inv_hats, container, false);
    }

    // Parses data for every cosmetic the user owns
    public void parseJSON(JSONArray response) {
        try {
            JSONArray jsonArray = response;
            // Iterate through all cosmetics in table
            for (int i = 0; i < jsonArray.length(); i++) {
                // Get information for the current cosmetic
                JSONObject object = jsonArray.getJSONObject(i);
                // Server object INSIDE of current cosmetic
                JSONObject serverItemObject = object.getJSONObject("serverItem");
                String name = serverItemObject.getString("serverItemName");
                String type = serverItemObject.getString("serverItemType");
                int cost = serverItemObject.getInt("itemCost");
                // Information in the original object
                long belongsTo = object.getLong("belongToAccount");
                boolean isEquipped = object.getBoolean("equipped");

                // If the item's type is a tag, then the fragment adds its data
                if (type.equals("tag")) {
                    invListData.add(new InvListData(name, type, cost, belongsTo, isEquipped));
                }
            }
            InvGridViewAdapter invGridViewAdapter = new InvGridViewAdapter(getContext(), invListData);
            gridView.setAdapter(invGridViewAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Gets inv data for this fragment
    public void fetchData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                userUrl + "/" + String.valueOf(userID) + "/itemInventory",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }
}