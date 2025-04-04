package ZoneZone.com.itemsHandler;

import ZoneZone.com.accountHandler.AccountModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "user_items")
@JsonIgnoreProperties({"belongToAccount"}) // ✅ Prevent infinite recursion globally
public class UserItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemID;

    @ManyToOne
    @JoinColumn(name = "server_item_id", nullable = false)
    private ServerItemModel serverItem;

    @Column(name = "date_purchased", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePurchased;

    @Column(name = "is_equipped", nullable = false)
    private boolean isEquipped;

    @ManyToOne
    @JoinColumn(name = "belong_to_accountid", nullable = false)
    private AccountModel belongToAccount;

    // ✅ Default constructor
    public UserItemModel() {
        this.datePurchased = new Date();
        this.isEquipped = false;
    }

    // ✅ Ensure default values before saving
    @PrePersist
    protected void onCreate() {
        if (this.datePurchased == null) {
            this.datePurchased = new Date();
        }
        this.isEquipped = false;
    }

    // ✅ Getters & Setters
    public Long getItemID() {
        return itemID;
    }

    @JsonProperty("belongsToAccountID")
    public Long getBelongToAccountID() {
        return (belongToAccount != null) ? belongToAccount.getAccountID() : -1L;
    }

    public ServerItemModel getServerItem() {
        return serverItem;
    }

    public void setServerItem(ServerItemModel serverItem) {
        this.serverItem = serverItem;
    }

    public Date getDatePurchased() {
        return datePurchased;
    }

    public void setDatePurchased(Date datePurchased) {
        this.datePurchased = (datePurchased != null) ? datePurchased : new Date();
    }

    public boolean getIsEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    public AccountModel getBelongToAccount() {
        return belongToAccount;
    }

    public void setBelongToAccount(AccountModel belongToAccount) {
        this.belongToAccount = belongToAccount;
    }

    @Override
    public String toString() {
        return String.format(
                "UserItemModel{itemID=%d, serverItemID=%s, datePurchased=%s, isEquipped=%b, belongToAccountID=%s}",
                itemID,
                (serverItem != null) ? serverItem.getServerItemID() : "No Server Item",
                datePurchased,
                isEquipped,
                getBelongToAccountID()
        );
    }

}
