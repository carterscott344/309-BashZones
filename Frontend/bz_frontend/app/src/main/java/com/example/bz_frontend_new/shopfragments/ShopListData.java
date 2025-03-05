package com.example.bz_frontend_new.shopfragments;

public class ShopListData {
    private String name, type;
    private int cost;

    ShopListData(String name, String type, int cost) {
        this.name = name;
        this.type = type.toLowerCase(); // in case of mistakes
        this.cost = cost;
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
}
