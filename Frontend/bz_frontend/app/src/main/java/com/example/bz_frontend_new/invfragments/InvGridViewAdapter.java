package com.example.bz_frontend_new.invfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.example.bz_frontend_new.UserInventoryPage;
import com.example.bz_frontend_new.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class InvGridViewAdapter extends BaseAdapter {

    // Server URL for user items
    String userItemsUrl = "http://coms-3090-046.class.las.iastate.edu:8080/userItems";
    // URL for user accounts
    String userAccountsUrl = "http://coms-3090-046.class.las.iastate.edu:8080/accountUsers";

    // Shared preferences
    SharedPreferences sp;
    // UserID
    private long userID;
    // Gem Balance
    private int gemBalance;

    ArrayList<InvListData> invListData;
    Context context;
    InvGridViewAdapter(Context context, ArrayList<InvListData> shopListData) {
        this.invListData = shopListData;
        this.context = context;
        sp = context.getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userID = sp.getLong("userID", -1);
    }

    @Override
    public int getCount() {
        return invListData.toArray().length;
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
        @SuppressLint("ViewHolder") View root = layoutInflater.inflate(R.layout.inv_gridview_item, parent, false);
        RelativeLayout itemLayout = root.findViewById(R.id.inv_gridview_item);
        ImageView itemImage = root.findViewById(R.id.inv_item_image);
        TextView itemName = root.findViewById(R.id.inv_item_text);

        // Set image xml according to the data for that specific item
        try {
            // Get type string and image string (name with caps and spaces removed)
            String typeString = invListData.get(position).getType();
            String imageString = invListData.get(position).getName().toLowerCase();
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
        itemName.setText(invListData.get(position).getName());

        // Set equipped border if the item is equipped
        if (invListData.get(position).getIsEquipped()) {
            itemImage.setBackground(context.getDrawable(R.drawable.item_equipped_border));
            String typeString = invListData.get(position).getType();
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(typeString + "ID", invListData.get(position).getItemID());
            editor.commit();
        }

        // Shop item onClick listener
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position, itemImage);
            }
        });
        return root;
    }

    // Show options window when an item is tapped
    public void showDialog(int position, ImageView itemImage) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.inv_window);

        ImageView image = dialog.findViewById(R.id.item_image_inv_window);
        TextView text = dialog.findViewById(R.id.inv_window_text);
        Button back = dialog.findViewById(R.id.inv_back_button);
        Button equip = dialog.findViewById(R.id.inv_equip_button);
        Button sell = dialog.findViewById(R.id.inv_sell_button);

        text.setText(invListData.get(position).getName());

        // Set image xml according to the data for that specific item
        try {
            // Get type string and image string (name with caps and spaces removed)
            String typeString = invListData.get(position).getType();
            String imageString = invListData.get(position).getName().toLowerCase();
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        equip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equipItem(position, dialog, itemImage);
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellItem(position, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_square);
        dialog.show();
    }

    public void equipItem(int position, Dialog dialog, ImageView itemImage) {
        JSONObject equipObj = new JSONObject();
        try {
            equipObj.put("isEquipped", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest equipRequest = new JsonObjectRequest(
                Request.Method.PUT,
                userItemsUrl + "/" + String.valueOf(userID) + "/editItem/" + String.valueOf(invListData.get(position).getItemID()),
                equipObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String typeString = invListData.get(position).getType();
                        // If we have any equipped items of that type
                        if (sp.contains(typeString + "ID")) {
                            long toUnequip = sp.getLong(typeString + "ID", -1);
                            unequipItem(toUnequip, dialog, itemImage);
                        }
                        else {
                            notifyDataSetChanged();
                            Toast.makeText(context, "Equipped Item", Toast.LENGTH_SHORT).show();
                        }
                        // In both cases, update the shared preferences
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putLong(typeString + "ID", invListData.get(position).getItemID());
                        editor.commit();
                        // Refresh inventory after equip changes are made
                        Log.d("type", typeString);
                        if (typeString.equals("hat")) {
                            ((UserInventoryPage) context).updateInventory(0);
                        }
                        else if (typeString.equals("banner")) {
                            ((UserInventoryPage) context).updateInventory(1);
                        }
                        else if (typeString.equals("tag")) {
                            ((UserInventoryPage) context).updateInventory(2);
                        }
                        else {
                            ((UserInventoryPage) context).updateInventory(0);
                        }
                        dialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error Equipping Item", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                dialog.cancel();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(equipRequest);
    }

    public void unequipItem(long toUnequip, Dialog dialog, ImageView itemImage) {
        JSONObject unequipObj = new JSONObject();
        try {
            unequipObj.put("isEquipped", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest unequipRequest = new JsonObjectRequest(
                Request.Method.PUT,
                userItemsUrl + "/" + String.valueOf(userID) + "/editItem/" + String.valueOf(toUnequip),
                unequipObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        notifyDataSetChanged();
                        Toast.makeText(context, "Equipped New Item", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error Unequipping Item", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                dialog.cancel();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(unequipRequest);
    }

    public void sellItem(int position, Dialog dialog) {
        JsonObjectRequest delRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                userItemsUrl + "/" + String.valueOf(userID) + "/removeItem/" + String.valueOf(invListData.get(position).getItemID()),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // If sold and is stored as the equipped item, remove key value pair
                        String typeString = invListData.get(position).getType();
                        if (sp.getLong(typeString + "ID", -1) == (invListData.get(position).getItemID())) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(typeString + "ID");
                            editor.commit();
                        }

                        // If sold successfully, give user the money earned
                        giveMoney(position, dialog);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error Selling Item", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(delRequest);
    }

    public void giveMoney(int position, Dialog dialog) {
        JSONObject moneyAddObj = new JSONObject();

        int balance = sp.getInt("balance", 0);
        int toEarn = invListData.get(position).getCost() / 2;

        try {
            moneyAddObj.put("gemBalance", balance + toEarn);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest moneyAddReq = new JsonObjectRequest(
                Request.Method.PUT,
                userAccountsUrl + "/updateUser/" + String.valueOf(userID),
                moneyAddObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Item Sold Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("balance", balance + toEarn);
                        editor.commit();
                        invListData.remove(position);
                        notifyDataSetChanged();
                        dialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error Giving Money!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(moneyAddReq);
    }
}
