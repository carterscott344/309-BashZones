package com.zonezone.backend.gameHandler;

import java.util.List;

public class MatchEndPayloadDTO {
    public String matchID;
    public List<Long> players; // All userIDs involved in the match

    public String winningTeam;
    public int winningScore;
    public String losingTeam;
    public int losingScore;

    public int totalKills;
    public int totalDeaths;

    public MatchEndPayloadDTO() {
        // No-args constructor
    }

    public MatchEndPayloadDTO(
            String matchID,
            List<Long> players,
            String winningTeam,
            int winningScore,
            String losingTeam,
            int losingScore,
            int totalKills,
            int totalDeaths
    ) {
        this.matchID = matchID;
        this.players = players;
        this.winningTeam = winningTeam;
        this.winningScore = winningScore;
        this.losingTeam = losingTeam;
        this.losingScore = losingScore;
        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
    }
}
