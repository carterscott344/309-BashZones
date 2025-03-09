package ZoneZone.com.itemsHandler;

import ZoneZone.com.accountHandler.AccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItemModel, Long> {
    // ✅ Find all items for a specific user
    List<UserItemModel> findByBelongToAccount(AccountModel belongToAccount);

    // ✅ Paginated version for large inventories
    Page<UserItemModel> findByBelongToAccount(AccountModel belongToAccount, Pageable pageable);

    // ✅ Find only equipped items
    List<UserItemModel> findByBelongToAccountAndIsEquipped(AccountModel belongToAccount, boolean isEquipped);

    // ✅ Find all items by a specific serverItemID
    List<UserItemModel> findByServerItem(ServerItemModel serverItem);

    // ✅ Finds if a user already owns an item with the same ServerItemID
    boolean existsByBelongToAccountAndServerItem(AccountModel belongToAccount, ServerItemModel serverItem);

}
