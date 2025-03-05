package ZoneZone.com.itemsHandler;

import ZoneZone.com.accountHandler.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItemModel, Long> {
    List<UserItemModel> findByBelongToAccount(AccountModel belongToAccount);
}
