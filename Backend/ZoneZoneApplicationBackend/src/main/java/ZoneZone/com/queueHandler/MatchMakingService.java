package ZoneZone.com.queueHandler;

import ZoneZone.com.accountHandler.AccountRepository;
import ZoneZone.com.matchRoomHandler.MatchRoom;
import ZoneZone.com.webSocketHandler.GameServerWebSocketClient;
import ZoneZone.com.webSocketHandler.WebSocketMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchMakingService {

    private final lobbyQueue queue;

    @Autowired
    private AccountRepository accountRepository;

    public MatchMakingService(lobbyQueue queue) {
        this.queue = queue;
    }

    @Scheduled(fixedRate = 2000) // every 2 seconds
    public void checkQueue() {
        if (queue.size() >= 4) {
            List<String> players = queue.popFirstFour();
            List<String> validPlayers = new ArrayList<>();

            for (String userID : players) {
                accountRepository.findById(Long.parseLong(userID)).ifPresent(account -> {
                    if (Boolean.TRUE.equals(account.getIsInQueue())) {
                        validPlayers.add(userID);
                    }
                });
            }

            if (validPlayers.size() < 4) {
                // Put valid players back into queue if not enough for a match
                for (String userID : validPlayers) {
                    queue.add(userID);
                }
                return;
            }

            List<String> teamA = validPlayers.subList(0, 2);
            List<String> teamB = validPlayers.subList(2, 4);

            MatchRoom matchRoom = new MatchRoom(teamA, teamB);

            WebSocketMessageDTO payload = new WebSocketMessageDTO(
                    matchRoom.getMatchID(),
                    matchRoom.getTeamA(),
                    matchRoom.getTeamB(),
                    matchRoom.getGameMode()
            );

            GameServerWebSocketClient.sendMatchPayload(payload);
            System.out.println("✅ Match sent to game server: " + matchRoom.getMatchID());

            // Update status in DB
            for (String userID : validPlayers) {
                accountRepository.findById(Long.parseLong(userID)).ifPresent(account -> {
                    account.setIsInQueue(false);
                    account.setIsPlaying(true);
                    accountRepository.save(account);
                });
            }
        }
    }
}
