package ZoneZone.com.itemsHandler;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "account_items")
public class UserItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Ensures only one AUTO_INCREMENT key
    private Long accountItemID;

    @Column(nullable = false)
    private Long serverItemTypeID;

    @Column(nullable = false)
    private Long belongsToAccountID;

    @Column(nullable = false)
    private String datePurchased;

    @Column(nullable = false)
    private Boolean isEquipped;

    // Constructors

    public UserItemModel() {
        // DEFAULT
    }

    public UserItemModel(Long serverItemTypeID, Long belongsToAccountID, String datePurchased, Boolean isEquipped) {
        this.serverItemTypeID = serverItemTypeID;
        this.belongsToAccountID = belongsToAccountID;
        this.datePurchased = LocalDate.now().toString();
        this.isEquipped = isEquipped;
    }

    // On Create Method
    @PrePersist
    protected void onCreate() {
    
    }
    public Long getAccountItemID() {
        return accountItemID;
    }

    public Long getServerItemTypeID() {
        return serverItemTypeID;
    }
    public void setServerItemTypeID(Long serverItemTypeID) {
        this.serverItemTypeID = serverItemTypeID;
    }

    public Long getBelongsToAccountID() {
        return belongsToAccountID;
    }
    public String getDatePurchased() {
        return datePurchased;
    }
    public Boolean getIsEquipped() {
        return isEquipped;
    }
    public void setIsEquipped(Boolean isEquipped) {
        this.isEquipped = isEquipped;
    }
    // Getter And Setter Methods

}
