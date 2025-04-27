package ZoneZone.com.webSocketHandler.matchHistoryHandler;

import ZoneZone.com.accountHandler.AccountRepository;
import ZoneZone.com.webSocketHandler.MatchEndPayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchEndController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MatchHistoryRepository matchHistoryRepository;

    @PostMapping("/end")
    public String endMatch(@RequestBody MatchEndPayloadDTO payload) {
        System.out.println("📬 Received end of match: " + payload.matchID);

        // Reset players' playing status
        List<Long> userIDs = payload.players;
        for (Long userID : userIDs) {
            accountRepository.findById(userID).ifPresent(account -> {
                account.setIsPlaying(false);
                accountRepository.save(account);
                System.out.println("🛑 User " + account.getAccountUsername() + " marked isPlaying = false");
            });
        }

        // Save match history
        MatchHistoryModel history = new MatchHistoryModel();
        history.setMatchID(payload.matchID);
        history.setWinningTeam(payload.winningTeam);
        history.setWinningScore(payload.winningScore);
        history.setLosingTeam(payload.losingTeam);
        history.setLosingScore(payload.losingScore);
        history.setTotalKills(payload.totalKills);
        history.setTotalDeaths(payload.totalDeaths);
        history.setFinishedAt(LocalDateTime.now()); // ✅ Save timestamp!

        matchHistoryRepository.save(history);

        return "✅ Match " + payload.matchID + " ended and saved.";
    }

    @DeleteMapping("/deleteHistory/{id}")
    public ResponseEntity<String> deleteMatchHistory(@PathVariable Long id) {
        if (matchHistoryRepository.existsById(id)) {
            matchHistoryRepository.deleteById(id);
            return ResponseEntity.ok("🗑️  Match history with ID " + id + " deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("❌  Match history ID " + id + " not found.");
        }
    }
    @GetMapping("/getHistory/{count}")
    public List<MatchHistoryModel> getRecentMatchHistory(@PathVariable int count) {
        return matchHistoryRepository.findRecentMatches(PageRequest.of(0, count));
    }
}
