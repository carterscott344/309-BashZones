package ZoneZone.com.webSocketHandler.matchHistoryHandler;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchHistoryRepository extends JpaRepository<MatchHistoryModel, Long> {

    @Query("SELECT m FROM MatchHistoryModel m ORDER BY m.finishedAt DESC")
    List<MatchHistoryModel> findRecentMatches(Pageable pageable);
}
