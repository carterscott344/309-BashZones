package ZoneZone.com.accountHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OnlinePlayTimeTrackerService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountController accountController;

    /**
     * Runs every 30 seconds and adds 30 seconds of playtime
     * to all currently online users.
     */
    @Scheduled(fixedRate = 30000) // every 30 seconds
    public void incrementOnlinePlaytime() {
        List<AccountModel> onlineUsers = accountRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getIsOnline()))
                .toList();

        for (AccountModel account : onlineUsers) {
            Long userID = account.getAccountID();

            // Add 30 seconds to total and session playtime
            AccountPlayTime sessionPlayTime = accountController.sessionPlayTimeCache
                    .getOrDefault(userID, new AccountPlayTime());

            sessionPlayTime.addTime(0, 0, 0, 30); // Add 30s to session
            account.getTotalUserPlayTime().addTime(0, 0, 0, 30); // Add 30s to total

            accountController.sessionPlayTimeCache.put(userID, sessionPlayTime);
            accountRepository.save(account);
        }
    }
}
