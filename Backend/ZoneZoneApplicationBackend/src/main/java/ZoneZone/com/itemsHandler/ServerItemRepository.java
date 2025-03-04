package ZoneZone.com.itemsHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ServerItemRepository extends JpaRepository<ServerItemModel, Long> {
    Optional<ServerItemModel> findByItemName(String itemName); // ✅ Find a global item by name
}
