package com.example.bz_frontend_new.invfragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bz_frontend_new.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class InvGridViewAdapter extends BaseAdapter {

    // Server URL for user items
    String userItemsUrl = "http://coms-3090-046.class.las.iastate.edu:8080/userItems";

    // Shared preferences
    SharedPreferences sp;
    // UserID
    private long userID;

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

        // Shop item onClick listener
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
        return root;
    }

    // Show options window when an item is tapped
    public void showDialog(int position) {

    }
}
