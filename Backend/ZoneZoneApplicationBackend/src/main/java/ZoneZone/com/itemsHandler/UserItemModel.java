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

    @ManyToOne
    @JoinColumn(name = "belong_to_account_id", nullable = false)
    private AccountModel belongToAccount; // Reference to account

    public UserItemModel() {}

    public UserItemModel(ServerItemModel serverItem, Date datePurchased, boolean isEquipped, AccountModel belongToAccount) {
        this.serverItem = serverItem;
        this.datePurchased = datePurchased;
        this.isEquipped = isEquipped;
        this.belongToAccount = belongToAccount;
    }

    public Long getId() { return id; }

    public ServerItemModel getServerItem() { return serverItem; }
    public void setServerItem(ServerItemModel serverItem) { this.serverItem = serverItem; }

    public Date getDatePurchased() { return datePurchased; }
    public void setDatePurchased(Date datePurchased) { this.datePurchased = datePurchased; }

    public boolean isEquipped() { return isEquipped; }
    public void setEquipped(boolean isEquipped) { this.isEquipped = isEquipped; }

    public AccountModel getBelongToAccount() { return belongToAccount; }
    public void setBelongToAccount(AccountModel belongToAccount) { this.belongToAccount = belongToAccount; }

    // Get cost dynamically from ServerItemModel
    public int getItemCost() {
        return serverItem.getItemCost();
    }
}
