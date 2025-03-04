package com.example.bz_frontend_new.shop_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Path;
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

public class ShopGridViewAdapter extends BaseAdapter {
    ArrayList<ShopListData> shopListData;
    Context context;
    ShopGridViewAdapter(Context context, ArrayList<ShopListData> shopListData) {
        this.shopListData = shopListData;
        this.context = context;
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
            InputStream inputStream = context.getApplicationContext().getAssets().open(
                    "sprites/cosmetics/" + shopListData.get(position).getType() + "/" + shopListData.get(position).getName() + ".jpg"
            );
            Drawable img = Drawable.createFromStream(inputStream, null);
            itemImage.setImageDrawable(img);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set text xml according to the data for that specific item
        itemName.setText(shopListData.get(position).getName());
        itemCost.setText(shopListData.get(position).getCost());

        // Shop item onClick listener
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}
