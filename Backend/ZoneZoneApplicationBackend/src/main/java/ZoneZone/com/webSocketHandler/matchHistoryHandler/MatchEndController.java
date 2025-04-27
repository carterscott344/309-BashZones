package ZoneZone.com.webSocketHandler.matchHistoryHandler;

import ZoneZone.com.accountHandler.AccountRepository;
import ZoneZone.com.webSocketHandler.MatchEndPayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        List<MatchHistoryModel> allMatches = matchHistoryRepository.findAll();
        allMatches.sort((a, b) -> Long.compare(b.getId(), a.getId())); // Sort descending by ID (latest first)

        int fromIndex = Math.max(0, allMatches.size() - count);
        return allMatches.subList(fromIndex, allMatches.size());
    }
}
