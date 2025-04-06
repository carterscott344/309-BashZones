package ZoneZone.com.webSocketHandler;

import java.util.List;

public class WebSocketMessageDTO {
    // Getters & Setters
    private String type;

    private String matchID;
    private List<String> teamA;
    private List<String> teamB;
    private String gameMode; // e.g., "2v2"

    public WebSocketMessageDTO() {}

    public WebSocketMessageDTO(String type, String matchID, List<String> teamA, List<String> teamB, String gameMode) {
        this.type = type;
        this.matchID = matchID;
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameMode = gameMode;
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

    public String getType() {
        return type;
    }

}
