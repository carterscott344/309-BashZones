package ZoneZone.com.matchRoomHandler;

import java.util.List;
import java.util.UUID;

public class MatchRoom {

    private final String matchID;
    private final List<String> teamA;
    private final List<String> teamB;
    private final String gameMode;

    public MatchRoom(List<String> teamA, List<String> teamB) {
        this.matchID = UUID.randomUUID().toString();
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameMode = "2v2"; // Static for now
    }

    public String getMatchID() {
        return matchID;
    }

    public List<String> getTeamA() {
        return teamA;
    }

    public List<String> getTeamB() {
        return teamB;
    }

    public String getGameMode() {
        return gameMode;
    }
}
