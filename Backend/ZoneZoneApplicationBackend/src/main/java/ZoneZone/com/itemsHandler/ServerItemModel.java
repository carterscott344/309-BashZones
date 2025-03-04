package ZoneZone.com.itemsHandler;

import jakarta.persistence.*;

@Entity
@Table(name = "server_items")
public class ServerItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Ensures only one AUTO_INCREMENT key
    private Long serverItemID;

    @Column(nullable = false, unique = true)
    private String serverItemName;

    @Column(nullable = false, unique = true)
    private String serverItemDescription;

    @Column(nullable = false)
    private int itemCost;

    @Column(nullable = false)
    private String serverItemType;

    // Constructors

    public ServerItemModel() {
        // DEFAULT
    }

    public ServerItemModel(String serverItemName, String serverItemDescription, String serverItemType, int itemCost) {
        this.serverItemName = serverItemName;
        this.serverItemDescription = serverItemDescription;
        this.serverItemType = serverItemType;
        this.itemCost = itemCost;
    }

    // Getter And Setter Methods

    public Long getServerItemID() {
        return serverItemID;
    }
    public void setServerItemID(Long serverItemID) {
        this.serverItemID = serverItemID;
    }
    public String getServerItemName() {
        return serverItemName;
    }
    public void setServerItemName(String serverItemName) {
        this.serverItemName = serverItemName;
    }
    public String getServerItemDescription() {
        return serverItemDescription;
    }
    public void setServerItemDescription(String serverItemDescription) {
        this.serverItemDescription = serverItemDescription;
    }
    public int getItemCost() {
        return itemCost;
    }
    public void setItemCost(int itemCost) {
        this.itemCost = itemCost;
    }
    public String getServerItemType() {
        return serverItemType;
    }
    public void setServerItemType(String serverItemType) {
        this.serverItemType = serverItemType;
    }

}
