package com.example.bz_frontend_new.shop_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bz_frontend_new.R;

public class SkinsFragment extends Fragment {

    // Server URL for shop items
    String url = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skins_fragment, container, false);
    }
}