package com.example.bz_frontend_new;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bz_frontend_new.invfragments.InvBannersFragment;
import com.example.bz_frontend_new.invfragments.InvHatsFragment;
import com.example.bz_frontend_new.invfragments.InvTagsFragment;

public class InvViewPagerAdapter extends FragmentStateAdapter {
    public InvViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new InvHatsFragment();
            case 1:
                return new InvBannersFragment();
            case 2:
                return new InvTagsFragment();
            default:
                return new InvHatsFragment();
        }
    }



    @Override
    public int getItemCount() {
        return 3;
    }
}
