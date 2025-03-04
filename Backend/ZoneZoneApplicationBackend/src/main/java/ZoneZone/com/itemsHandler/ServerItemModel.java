package ZoneZone.com.itemsHandler;

import jakarta.persistence.*;

@Entity
@Table(name = "server_items")
public class ServerItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Ensures only one AUTO_INCREMENT key
    private Long itemID;

    @Column(nullable = false, unique = true)
    private String itemName;

    @Column(nullable = false)
    private String itemType;

    @Column(nullable = false)
    private int cost;

    public ServerItemModel() {}

    public ServerItemModel(String itemName, String itemType, int cost) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.cost = cost;
    }

    public Long getItemID() {
        return itemID;
    }

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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
