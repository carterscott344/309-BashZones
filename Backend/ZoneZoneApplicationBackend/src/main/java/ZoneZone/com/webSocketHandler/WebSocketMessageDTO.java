package ZoneZone.com.webSocketHandler;

import java.util.List;

public class WebSocketMessageDTO {
    // Getters & Setters
    private String matchID;
    private List<String> teamA;
    private List<String> teamB;
    private String gameMode; // e.g., "2v2"

    public WebSocketMessageDTO() {}

    public WebSocketMessageDTO(String matchID, List<String> teamA, List<String> teamB, String gameMode) {
        this.matchID = matchID;
        this.teamA = teamA;
        this.teamB = teamB;
        this.gameMode = gameMode;
    }

}
