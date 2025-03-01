package com.example.bz_frontend_new.shop_fragments;

public class ShopListData {
    private String image, name;

    ShopListData(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
