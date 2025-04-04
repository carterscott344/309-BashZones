package ZoneZone.com.accountHandler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    // ✅ Find an account by username
    Optional<AccountModel> findByAccountUsername(String accountUsername);

    // ✅ Find an account by email
    Optional<AccountModel> findByAccountEmail(String accountEmail);

    // ✅ Check if an account exists by username
    boolean existsByAccountUsername(String accountUsername);

    // ✅ Check if an account exists by email
    boolean existsByAccountEmail(String accountEmail);

    // ✅ Get all online users
    List<AccountModel> findByIsOnlineTrue();

    // ✅ Get all users currently playing a game
    List<AccountModel> findByIsPlayingTrue();

    // ✅ Get all users currently in matchmaking queue
    List<AccountModel> findByIsInQueueTrue();

}
