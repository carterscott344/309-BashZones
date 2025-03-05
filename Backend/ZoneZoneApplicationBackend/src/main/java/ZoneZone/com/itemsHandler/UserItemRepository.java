package ZoneZone.com.itemsHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserItemRepository extends JpaRepository<UserItemModel, Long> {
    List<UserItemModel> findByBelongsToAccountID(Long belongsToAccountID);
}
