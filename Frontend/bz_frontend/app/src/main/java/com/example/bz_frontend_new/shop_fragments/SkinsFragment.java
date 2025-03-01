package com.example.bz_frontend_new.shop_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bz_frontend_new.R;
import com.example.bz_frontend_new.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SkinsFragment extends Fragment {

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

    public void parseJSON(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String name = object.getString("name");
                String image = object.getString("image");
                shopListData.add(new ShopListData(name, image));
            }
            ShopGridViewAdapter shopGridViewAdapter = new ShopGridViewAdapter(getContext(), shopListData);
            gridView.setAdapter(shopGridViewAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchData() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}