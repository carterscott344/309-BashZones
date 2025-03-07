package com.example.bz_frontend_new.invfragments;

public class InvListData {
    private String name, type;
    private int cost;
    private long belongsTo;
    private boolean isEquipped;
    private long itemID;

    InvListData(String name, String type, int cost, long belongsTo, boolean isEquipped, long itemID) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.belongsTo = belongsTo;
        this.isEquipped = isEquipped;
        this.itemID = itemID;
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

    public long getBelongsTo() {
        return belongsTo;
    }

    public boolean getIsEquipped(){
        return isEquipped;
    }

    public long getItemID() {
        return itemID;
    }
}
