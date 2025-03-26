package ZoneZone.com.queueHandler;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String joinQueue(@PathVariable Long userID) {
        queue.add(String.valueOf(userID)); // keep as string for queue system

        accountRepository.findById(userID).ifPresent(account -> {
            account.setIsInQueue(true);
            account.setIsOnline(true); // optional: assume online if queuing
            accountRepository.save(account);
        });

        return "✅ User " + userID + " added to queue and marked as inQueue=true.";
    }

    @PostMapping("/leave/{userID}")
    public String leaveQueue(@PathVariable Long userID) {
        queue.remove(String.valueOf(userID));

        accountRepository.findById(userID).ifPresent(account -> {
            account.setIsInQueue(false);
            accountRepository.save(account);
        });

        return "🛑 User " + userID + " removed from queue and marked as inQueue=false.";
    }

    @GetMapping("/size")
    public int queueSize() {
        return queue.size();
    }
}
