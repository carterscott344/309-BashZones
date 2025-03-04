package ZoneZone.com.itemsHandler;

import jakarta.persistence.*;

@Entity
@Table(name = "user_items")
public class UserItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountOwnerID;  // Stores the user ID of the owner

    private String itemName;
    private String itemType;

    public UserItemModel() {}

    public UserItemModel(Long accountOwnerID, String itemName, String itemType) {
        this.accountOwnerID = accountOwnerID;
        this.itemName = itemName;
        this.itemType = itemType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountOwnerID() {
        return accountOwnerID;
    }

    public void setAccountOwnerID(Long accountOwnerID) {
        this.accountOwnerID = accountOwnerID;
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
}
