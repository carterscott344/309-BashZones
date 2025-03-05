package ZoneZone.com.userItemsHandler;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserItemsRepository extends JpaRepository<UserItemsModel, Long> {}