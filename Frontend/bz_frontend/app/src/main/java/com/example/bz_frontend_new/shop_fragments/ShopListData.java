package com.example.bz_frontend_new.shop_fragments;

public class ShopListData {
    private String name, type;
    private int cost, palette;

    ShopListData(String name, String type, int cost, int palette) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.palette = palette;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public int getPalette() {
        return palette;
    }
}
