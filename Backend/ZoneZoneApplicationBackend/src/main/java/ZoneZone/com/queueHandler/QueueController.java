package ZoneZone.com.queueHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ZoneZone.com.accountHandler.AccountRepository;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @Autowired
    private lobbyQueue queue;

    @Autowired
    private AccountRepository accountRepository;


    @PostMapping("/join/{userID}")
    public ResponseEntity<String> joinQueue(@PathVariable Long userID) {
        return accountRepository.findById(userID).map(account -> {
            if (account.getIsInQueue() || account.getIsPlaying()) {
                return ResponseEntity.badRequest().body("❌ User already in queue or playing.");
            }

            queue.add(String.valueOf(userID));
            account.setIsInQueue(true);
            account.setIsOnline(true);
            accountRepository.save(account);

            return ResponseEntity.ok("✅ User " + userID + " added to queue and marked inQueue=true.");
        }).orElseGet(() ->
                ResponseEntity.badRequest().body("❌ User ID " + userID + " not found.")
        );
    }

    @PostMapping("/leave/{userID}")
    public ResponseEntity<String> leaveQueue(@PathVariable Long userID) {
        return accountRepository.findById(userID).map(account -> {
            if (!Boolean.TRUE.equals(account.getIsInQueue())) {
                return ResponseEntity.badRequest().body("❌ User " + userID + " is not currently in queue.");
            }

            queue.remove(String.valueOf(userID));
            account.setIsInQueue(false);
            accountRepository.save(account);

            return ResponseEntity.ok("🛑 User " + userID + " removed from queue and marked inQueue=false.");
        }).orElseGet(() ->
                ResponseEntity.badRequest().body("❌ User ID " + userID + " not found.")
        );
    }

    @GetMapping("/size")
    public int queueSize() {
        return queue.size();
    }
}
