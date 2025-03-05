package com.example.bz_frontend_new;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bz_frontend_new.shopfragments.BannersFragment;
import com.example.bz_frontend_new.shopfragments.HatsFragment;
import com.example.bz_frontend_new.shopfragments.TagsFragment;

public class ShopViewPagerAdapter extends FragmentStateAdapter {
    public ShopViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HatsFragment();
            case 1:
                return new BannersFragment();
            case 2:
                return new TagsFragment();
            default:
                return new HatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
