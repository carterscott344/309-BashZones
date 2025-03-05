package com.example.bz_frontend_new.invfragments;

import android.os.Bundle;

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

public class InvHatsFragment extends Fragment {

    // Server URL for user items
    String userUrl = "http://coms-3090-046.class.las.iastate.edu:8080/userItems";

    // Important fields for inventory items
    ArrayList<InvListData> shopListData;
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize important fields for inventory items
        shopListData = new ArrayList<>();
        gridView = gridView.findViewById(R.id.hats_grid_view);

        // Fetch inventory data for this fragment
        fetchData();

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
                String name = object.getString("itemName");
                String type = object.getString("itemType");
                int cost = object.getInt("itemCost");
                long belongsTo = object.getLong("belongsTo");
                boolean isEquipped = object.getBoolean("isEquipped");

                // If the item's type is a hat, then the fragment adds its data
                if (type.equals("hat")) {
                    shopListData.add(new InvListData(name, type, cost, belongsTo, isEquipped));
                }
            }
            InvGridViewAdapter invGridViewAdapter = new InvGridViewAdapter(getContext(), shopListData);
            gridView.setAdapter(invGridViewAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Gets inv data for this fragment
    public void fetchData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                userUrl + "/listItems",
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