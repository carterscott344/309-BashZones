package ZoneZone.com.itemsHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItemModel, Long> {
    List<UserItemModel> findByBelongToAccountID(Long belongToAccountID);
}
