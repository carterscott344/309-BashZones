package ZoneZone.com.queueHandler;

import ZoneZone.com.matchRoomHandler.MatchRoom;
import ZoneZone.com.webSocketHandler.GameServerWebSocketClient;
import ZoneZone.com.webSocketHandler.WebSocketMessageDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchMakingService {

    private final lobbyQueue queue;

    public MatchMakingService(lobbyQueue queue) {
        this.queue = queue;
    }

    @Scheduled(fixedRate = 2000) // every 2 seconds
    public void checkQueue() {
        if (queue.size() >= 4) {
            List<String> players = queue.popFirstFour();
            List<String> teamA = players.subList(0, 2);
            List<String> teamB = players.subList(2, 4);

            MatchRoom matchRoom = new MatchRoom(teamA, teamB);

            WebSocketMessageDTO payload = new WebSocketMessageDTO(
                    matchRoom.getMatchID(),
                    matchRoom.getTeamA(),
                    matchRoom.getTeamB(),
                    matchRoom.getGameMode()
            );

            GameServerWebSocketClient.sendMatchPayload(payload);
            System.out.println("✅ Match sent to game server: " + matchRoom.getMatchID());
        }
    }
}
