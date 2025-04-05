package ZoneZone.com.itemsHandler;

import jakarta.persistence.*;

/**
 * Represents an item in the server shop that can be owned by users.
 * This model is mapped to the "server_items" table in the database.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "server_items")
public class ServerItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Auto-increment primary key
    private Long serverItemID;

    @Column(nullable = false, unique = true)
    private String serverItemName;

    @Column(nullable = false, unique = true, length = 255) // ✅ Prevents duplicate descriptions
    private String serverItemDescription;

    @Column(nullable = false)
    private String serverItemType;

    @Column(nullable = false)
    private int itemCost;

    // ✅ Default Constructor
    public ServerItemModel() {}

    /**
     * Constructs a new ServerItemModel with specified attributes.
     *
     * @param serverItemName        Name of the server item (must be unique).
     * @param serverItemDescription Description of the server item (must be unique).
     * @param serverItemType        Type/category of the server item.
     * @param itemCost              Cost of the item (must be non-negative).
     */
    public ServerItemModel(String serverItemName, String serverItemDescription, String serverItemType, int itemCost) {
        this.serverItemName = serverItemName;
        this.serverItemDescription = serverItemDescription;
        this.serverItemType = serverItemType;
        this.itemCost = Math.max(0, itemCost); // ✅ Prevents negative cost
    }

    // ✅ Getters
    public Long getServerItemID() {
        return serverItemID;
    }

    public String getServerItemName() {
        return serverItemName;
    }

    public String getServerItemDescription() {
        return serverItemDescription;
    }

    public String getServerItemType() {
        return serverItemType;
    }

    public int getItemCost() {
        return itemCost;
    }

    // ✅ Setters (Ensuring Data Validity)
    public void setServerItemID(Long serverItemID) {
        this.serverItemID = serverItemID;
    }

    public void setServerItemName(String serverItemName) {
        this.serverItemName = serverItemName;
    }

    public void setServerItemDescription(String serverItemDescription) {
        this.serverItemDescription = serverItemDescription;
    }

    public void setServerItemType(String serverItemType) {
        this.serverItemType = serverItemType;
    }

    public void setItemCost(int itemCost) {
        this.itemCost = Math.max(0, itemCost); // ✅ Prevents negative cost
    }

    // ✅ Improved toString() for Debugging
    @Override
    public String toString() {
        return String.format(
                "ServerItemModel{id=%d, name='%s', description='%s', type='%s', cost=%d}",
                serverItemID, serverItemName, serverItemDescription, serverItemType, itemCost
        );
    }
}
