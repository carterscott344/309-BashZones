package com.example.bz_frontend_new.shopfragments;

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

public class HatsFragment extends Fragment {

    // Server URL for shop items
    String url = "http://coms-3090-046.class.las.iastate.edu:8080/serverItems";
    // Server URL for user items
    String userUrl = "http://coms-3090-046.class.las.iastate.edu:8080/userItems";

    // Important fields for shop items
    ArrayList<ShopListData> shopListData;
    GridView gridView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize important fields for shop items
        shopListData = new ArrayList<>();
        gridView = view.findViewById(R.id.hats_grid_view);

        // Fetch shop data for this fragment
        fetchData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and return
        return inflater.inflate(R.layout.fragment_hats_fragment, container, false);
    }

    // Parses data for every cosmetic in the game (response)
    public void parseJSON(JSONArray response) {
        try {
            JSONArray jsonArray = response;
            // Iterate through all cosmetics in table
            for (int i = 0; i < jsonArray.length(); i++) {
                // Get information for the current cosmetic
                JSONObject object = jsonArray.getJSONObject(i);
                String name = object.getString("serverItemName");
                String type = object.getString("serverItemType");
                int cost = object.getInt("itemCost");
                long id = object.getLong("serverItemID");

                // If the item's type is a hat, then the fragment adds its data
                if (type.equals("hat")) {
                    shopListData.add(new ShopListData(name, type, cost, id));
                }
            }
            ShopGridViewAdapter shopGridViewAdapter = new ShopGridViewAdapter(getContext(), shopListData);
            gridView.setAdapter(shopGridViewAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Gets shop data for this fragment
    public void fetchData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "/listItems",
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