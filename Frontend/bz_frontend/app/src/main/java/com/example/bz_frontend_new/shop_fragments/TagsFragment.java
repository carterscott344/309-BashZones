package com.example.bz_frontend_new.shop_fragments;

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

public class TagsFragment extends Fragment {

    // Server URL for shop items
    String url = "";

    // Important fields for shop items
    ArrayList<ShopListData> shopListData;
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize important fields for shop items
        shopListData = new ArrayList<>();
        gridView = gridView.findViewById(R.id.grid_view);

        // Fetch shop data for this fragment
        fetchData();

        // Inflate the layout for this fragment and return
        return inflater.inflate(R.layout.fragment_skins_fragment, container, false);
    }

    // Parses data for every cosmetic in the game (response)
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
                int palette = object.getInt("itemPalette");

                // If the item's type is a hat, then the fragment adds its data
                if (type.equals("tag")) {
                    shopListData.add(new ShopListData(name, type, cost, palette));
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
                url,
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