package ZoneZone.com.webSocketHandler.matchHistoryHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchHistoryRepository extends JpaRepository<MatchHistoryModel, Long> {

    // 🆕 Custom query: Get most recent N matches
    List<MatchHistoryModel> findTop10ByOrderByIdDesc();
}
