package com.example.bz_frontend_new.shop_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bz_frontend_new.R;
import com.squareup.picasso.Picasso;

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
        RelativeLayout relativeLayout = root.findViewById(R.id.shop_gridview_items);
        ImageView imageView = root.findViewById(R.id.shop_item_image);
        TextView textView = root.findViewById(R.id.shop_item_text);

        // Set shop item's unique xml
        textView.setText(shopListData.get(position).getName());
        Picasso.get().load(shopListData.get(position).getImage()).into(imageView);

        // Shop item onClick listener
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }
}
