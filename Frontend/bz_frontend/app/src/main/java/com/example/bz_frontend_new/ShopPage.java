package com.example.bz_frontend_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

public class ShopPage extends AppCompatActivity {

    // Fields for ViewPager usage
    ViewPager2 viewPager2;
    ShopViewPagerAdapter shopViewPagerAdapter;

    // Important button fields
    Button return_button;
    Button hats_button;
    Button banners_button;
    Button tags_button;

    // TextView field for gemBalance
    TextView gem_balance_text;

    // Shared preferences
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page);

        // Set horizontal orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialize shared preferences
        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // Initialize ViewPager fields
        viewPager2 = findViewById(R.id.shop_viewpager);
        shopViewPagerAdapter = new ShopViewPagerAdapter(this);
        viewPager2.setAdapter(shopViewPagerAdapter);

        // Initialize important button fields
        return_button = findViewById(R.id.shop_return_button);
        hats_button = findViewById(R.id.shop_hats_button);
        banners_button = findViewById(R.id.shop_banners_button);
        tags_button = findViewById(R.id.shop_tags_button);

        // Initialize gemBalance TextView
        gem_balance_text = findViewById(R.id.gems_balance_text);
        updateBalanceText();

        // Set important button onClick listeners
        return_button.setOnClickListener(this::returnToGeneral);
        hats_button.setOnClickListener(this::onHatsClicked);
        banners_button.setOnClickListener(this::onBannersClicked);
        tags_button.setOnClickListener(this::onTagsClicked);

        // Because hats fragment is the default, button is be silver on activity creation
        hats_button.setBackgroundResource(R.color.silver);
    }

    // Important onClick methods
    public void returnToGeneral(View v) {
        Intent i = new Intent(this, GeneralPage.class);
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

    public void updateBalanceText() {
        gem_balance_text.setText("Gems: " + String.valueOf(sp.getInt("balance", 0)));
    }
}