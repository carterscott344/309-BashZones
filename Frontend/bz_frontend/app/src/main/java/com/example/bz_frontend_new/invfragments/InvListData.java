package com.example.bz_frontend_new.invfragments;

public class InvListData {
    private String name, type;
    private int cost;
    private long belongsTo;
    private boolean isEquipped;

    InvListData(String name, String type, int cost, long belongsTo, boolean isEquipped) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.belongsTo = belongsTo;
        this.isEquipped = isEquipped;
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
}
