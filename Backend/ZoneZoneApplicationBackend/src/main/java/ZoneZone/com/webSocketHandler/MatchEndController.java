package ZoneZone.com.webSocketHandler;

import ZoneZone.com.accountHandler.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchEndController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/end")
    public String endMatch(@RequestBody MatchEndPayloadDTO payload) {
        System.out.println("📬 Received end of match: " + payload.matchID);

        List<Long> userIDs = payload.players;
        for (Long userID : userIDs) {
            accountRepository.findById(userID).ifPresent(account -> {
                account.setIsPlaying(false);
                accountRepository.save(account);
                System.out.println("🛑 User " + account.getAccountUsername() + " marked isPlaying = false");
            });
        }

        return "✅ Match " + payload.matchID + " ended.";
    }
}
