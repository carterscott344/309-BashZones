package com.example.bz_frontend_new.shopfragments;

public class ShopListData {
    private String name, type;
    private int cost;
    private long id;

    ShopListData(String name, String type, int cost, long id) {
        this.name = name;
        this.type = type.toLowerCase(); // in case of mistakes
        this.cost = cost;
        this.id = id;
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

    public long getId() {
        return id;
    }
}
