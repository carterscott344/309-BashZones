package ZoneZone.com.itemHandler;

import jakarta.persistence.*;

@Entity
@Table(name = "GameItems")
public class ItemModel {
    @Id
    private String itemName;
    private String itemType; // hat, banner, tag, etc.
    private int itemCost;

    // GETTER & SETTER METHODS
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getItemCost() {
        return itemCost;
    }
    public void setItemCost(int itemCost) {
        this.itemCost = itemCost;
    }
}