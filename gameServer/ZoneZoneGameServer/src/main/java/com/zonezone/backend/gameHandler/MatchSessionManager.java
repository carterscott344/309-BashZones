package com.zonezone.backend.gameHandler;

import java.util.HashMap;
import java.util.Map;

public class MatchSessionManager {

    private static final Map<String, MatchStartPayloadDTO> activeMatches = new HashMap<>();

    public static void addMatch(MatchStartPayloadDTO match) {
        activeMatches.put(match.matchID, match);
        System.out.println("🆕 Match added to session: " + match.matchID);
    }

    public static MatchStartPayloadDTO getMatch(String matchID) {
        return activeMatches.get(matchID);
    }

    public static void removeMatch(String matchID) {
        activeMatches.remove(matchID);
        System.out.println("🗑️ Match removed from session: " + matchID);
    }

    public static int getMatchCount() {
        return activeMatches.size();
    }
}
