package ZoneZone.com.itemsHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ServerItemRepository extends JpaRepository<ServerItemModel, Long> {

    /**
     * Find a server item by its unique name.
     *
     * @param serverItemName The name of the server item.
     * @return Optional containing the matching ServerItemModel.
     */
    Optional<ServerItemModel> findByServerItemName(String serverItemName);

    /**
     * Check if a server item exists by name.
     *
     * @param serverItemName The name of the server item.
     * @return true if an item with the given name exists, false otherwise.
     */
    boolean existsByServerItemName(String serverItemName);
}
