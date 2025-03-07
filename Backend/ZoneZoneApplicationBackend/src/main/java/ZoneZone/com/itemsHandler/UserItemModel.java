package ZoneZone.com.itemsHandler;

import ZoneZone.com.accountHandler.AccountModel;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_items")
public class UserItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_item_id", nullable = false)
    private ServerItemModel serverItem; // Reference instead of storing separate fields

    @Column(name = "date_purchased", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePurchased;

    @Column(name = "is_equipped", nullable = false)
    private boolean isEquipped;

    @JoinColumn(name = "belong_to_account_id", nullable = false)
    private Long belongToAccountID; // Store only the account ID

    public UserItemModel() {}

    public UserItemModel(ServerItemModel serverItem, Date datePurchased, boolean isEquipped, Long belongToAccountID) {
        this.serverItem = serverItem;
        this.datePurchased = datePurchased;
        this.isEquipped = isEquipped;
        this.belongToAccountID = belongToAccountID;
    }

    public Long getId() {
        return id;
    }

    public ServerItemModel getServerItem() { return serverItem; }
    public void setServerItem(ServerItemModel serverItem) { this.serverItem = serverItem; }

    public Date getDatePurchased() { return datePurchased; }
    public void setDatePurchased(Date datePurchased) { this.datePurchased = datePurchased; }

    public boolean getIsEquipped() { return isEquipped; }
    public void setEquipped(boolean isEquipped) { this.isEquipped = isEquipped; }

    public Long getBelongToAccount() { return belongToAccountID; }
    public void setBelongToAccount(Long belongToAccountID) { this.belongToAccountID = belongToAccountID; }

}
