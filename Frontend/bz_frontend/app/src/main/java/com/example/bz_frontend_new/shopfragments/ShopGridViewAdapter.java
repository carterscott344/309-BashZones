package com.example.bz_frontend_new.shopfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bz_frontend_new.R;
import com.example.bz_frontend_new.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ShopGridViewAdapter extends BaseAdapter {

    // Server URL for shop items
    String shopItemsUrl = "http://coms-3090-046.class.las.iastate.edu:8080/serverItems";
    // Server URL for user items
    String userItemsUrl = "http://coms-3090-046.class.las.iastate.edu:8080/userItems";
    // Server URL for user accounts
    String userAccountsUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers";

    // Shared preferences
    SharedPreferences sp;
    // UserID
    private long userID;
    // Gem Balance
    private int gemBalance;

    ArrayList<ShopListData> shopListData;
    Context context;
    ShopGridViewAdapter(Context context, ArrayList<ShopListData> shopListData) {
        this.shopListData = shopListData;
        this.context = context;
        sp = context.getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userID = sp.getLong("userID", -1);
        gemBalance = sp.getInt("balance", 0);
    }

    @Override
    public int getCount() {
        return shopListData.toArray().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Grab important xml
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        @SuppressLint("ViewHolder") View root = layoutInflater.inflate(R.layout.shop_gridview_items, parent, false);
        RelativeLayout itemLayout = root.findViewById(R.id.shop_gridview_items);
        ImageView itemImage = root.findViewById(R.id.shop_item_image);
        TextView itemName = root.findViewById(R.id.shop_item_text);
        TextView itemCost = root.findViewById(R.id.shop_item_cost);

        // Set image xml according to the data for that specific item
        try {
            // Get type string and image string (name with caps and spaces removed)
            String typeString = shopListData.get(position).getType();
            String imageString = shopListData.get(position).getName().toLowerCase();
            imageString = imageString.replaceAll("\\s+", "");

            // Open image
            InputStream inputStream = context.getApplicationContext().getAssets().open(
                    "sprites/cosmetics/" + typeString + "/" + imageString + ".jpg"
            );
            Drawable img = Drawable.createFromStream(inputStream, null);
            itemImage.setImageDrawable(img);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set text xml according to the data for that specific item
        itemName.setText(shopListData.get(position).getName());
        itemCost.setText(String.valueOf(shopListData.get(position).getCost()) + " Gems");

        // Shop item onClick listener
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
        return root;
    }

    // Show purchase window when a button is tapped
    public void showDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.purchase_window);

        ImageView image = dialog.findViewById(R.id.item_image_purchase_window);
        TextView text = dialog.findViewById(R.id.purchase_window_text);
        Button no = dialog.findViewById(R.id.purchase_no_button);
        Button yes = dialog.findViewById(R.id.purchase_yes_button);

        text.setText("Do you want to purchase\n" +
                shopListData.get(position).getName() + "?");

        // Set image xml according to the data for that specific item
        try {
            // Get type string and image string (name with caps and spaces removed)
            String typeString = shopListData.get(position).getType();
            String imageString = shopListData.get(position).getName().toLowerCase();
            imageString = imageString.replaceAll("\\s+", "");

            // Open image
            InputStream inputStream = context.getApplicationContext().getAssets().open(
                    "sprites/cosmetics/" + typeString + "/" + imageString + ".jpg"
            );
            Drawable img = Drawable.createFromStream(inputStream, null);
            image.setImageDrawable(img);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check balance if confirmed, will purchase if balance is enough
                checkBalance(position, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_square);
        dialog.show();
    }

    // Method to check user account's balance (GET) on confirmed purchase
    public void checkBalance(int position, Dialog dialog) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,
                        userAccountsUrl + "/listUser/" + String.valueOf(userID),
                        null,
                        new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get user's gems
                            int userGems = response.getInt("gemBalance");
                            // Compare to cost of item
                            if (userGems >= shopListData.get(position).getCost()) {
                                purchaseItem(position, dialog);
                            }
                            else {
                                dialog.cancel();
                                Toast.makeText(context, "Insufficient Gems!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.cancel();
                        Toast.makeText(context, "Account Error", Toast.LENGTH_SHORT).show();
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    // Method to purchase item (uses PUT -> userAccounts and POST -> userItems)
    public void purchaseItem(int position, Dialog dialog) throws JSONException {
        // Create JSONObject to update user's gem balance
        JSONObject editedUserObject = new JSONObject();
        editedUserObject.put("gemBalance", 127);

        StringRequest purchaseRequest = new StringRequest(
                Request.Method.POST,
                userItemsUrl + "/" + String.valueOf(userID) + "/addItem/" + shopListData.get(position).getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();
                        Toast.makeText(context, "Purchase Successful", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Toast.makeText(context, "Purchase Error", Toast.LENGTH_SHORT).show();
            }
        });

        JsonObjectRequest userPutRequest = new JsonObjectRequest(
                Request.Method.PUT,
                userAccountsUrl + "/listUser/" + String.valueOf(userID),
                editedUserObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // If balanced is edited successfully, then we can purchase item
                        VolleySingleton.getInstance(context).addToRequestQueue(purchaseRequest);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Toast.makeText(context, "Balance Error", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(userPutRequest);
    }
}
