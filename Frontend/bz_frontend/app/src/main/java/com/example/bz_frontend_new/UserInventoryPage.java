package com.example.bz_frontend_new;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bz_frontend_new.invfragments.InvHatsFragment;

public class UserInventoryPage extends AppCompatActivity {

    // Fields for ViewPager usage
    ViewPager2 viewPager2;
    InvViewPagerAdapter invViewPagerAdapter;

    // Important button fields
    Button return_button;
    Button hats_button;
    Button banners_button;
    Button tags_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inventory_page);

        // Set horizontal orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize ViewPager fields
        viewPager2 = findViewById(R.id.inv_viewpager);
        invViewPagerAdapter = new InvViewPagerAdapter(this);
        viewPager2.setAdapter(invViewPagerAdapter);

        // Initialize important button fields
        return_button = findViewById(R.id.inv_return_button);
        hats_button = findViewById(R.id.inv_hats_button);
        banners_button = findViewById(R.id.inv_banners_button);
        tags_button = findViewById(R.id.inv_tags_button);

        // Set important button onClick listeners
        return_button.setOnClickListener(this::returnToSettings);
        hats_button.setOnClickListener(this::onHatsClicked);
        banners_button.setOnClickListener(this::onBannersClicked);
        tags_button.setOnClickListener(this::onTagsClicked);

        // Because skins fragment is the default, skins button will be silver on activity creation
        hats_button.setBackgroundResource(R.color.silver);

    }

    // Important onClick methods
    public void returnToSettings(View v) {
        Intent i = new Intent(this, SettingsPage.class);
        startActivity(i);
    }
    public void onHatsClicked(View v) {
        // Set color to gray to indicate it has been clicked, set other buttons to white
        hats_button.setBackgroundResource(R.color.silver);
        banners_button.setBackgroundResource(R.color.white);
        tags_button.setBackgroundResource(R.color.white);

        // Set ViewPager fragment to open specific shop tab
        viewPager2.setCurrentItem(0);
    }

    public void onBannersClicked(View v) {
        hats_button.setBackgroundResource(R.color.white);
        banners_button.setBackgroundResource(R.color.silver);
        tags_button.setBackgroundResource(R.color.white);

        viewPager2.setCurrentItem(1);
    }

    public void onTagsClicked(View v) {
        hats_button.setBackgroundResource(R.color.white);
        banners_button.setBackgroundResource(R.color.white);
        tags_button.setBackgroundResource(R.color.silver);

        viewPager2.setCurrentItem(2);
    }

    public void updateInventory(int item) {
        viewPager2.setAdapter(null);
        viewPager2.setAdapter(invViewPagerAdapter);
        viewPager2.setCurrentItem(item, false);
    }
}