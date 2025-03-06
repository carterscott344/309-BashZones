package ZoneZone.com.itemsHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerItemRepository extends JpaRepository<ServerItemModel, Long> {

    // Find by exact item name
    Optional<ServerItemModel> findByServerItemID(Long serverItemID);

    // Check if a server item exists by name
    boolean existsByServerItemName(String serverItemName);
}
