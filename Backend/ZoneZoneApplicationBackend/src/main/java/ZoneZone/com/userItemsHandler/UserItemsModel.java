package ZoneZone.com.userItemsHandler;

import jakarta.persistence.*;
//import java.util.List;
import ZoneZone.com.itemHandler.ItemModel;

@Entity
@Table(name = "ItemsList")
public class UserItemsModel {
    private String itemName;
    private String itemType;
    private int itemCost;

//    private List<ItemModel> items;
//    @Id
//    private ItemModel item;
    private long belongsTo; // accountID
    private boolean isEquipped;

    // GETTERS & SETTERS
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

    public long getBelongsTo() {
        return belongsTo;
    }
    public void setBelongsTo(long belongsTo) {
        this.belongsTo = belongsTo;
    }

    public boolean isEquipped() {
        return isEquipped;
    }
    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }
}