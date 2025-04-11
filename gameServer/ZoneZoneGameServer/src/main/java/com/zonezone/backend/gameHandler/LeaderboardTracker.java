package com.zonezone.backend.gameHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class LeaderboardTracker {

    public static class PlayerStats {
        public int kills = 0;
        public int deaths = 0;
    }

    private static final Map<String, Map<Long, PlayerStats>> matchStats = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Integer>> teamScores = new ConcurrentHashMap<>();

    public static void recordKill(String matchID, Long killerID, Long victimID) {
        matchStats.computeIfAbsent(matchID, m -> new ConcurrentHashMap<>());

        matchStats.get(matchID)
                .computeIfAbsent(killerID, k -> new PlayerStats()).kills++;

        matchStats.get(matchID)
                .computeIfAbsent(victimID, v -> new PlayerStats()).deaths++;
    }

    public static void addTeamScore(String matchID, String team) {
        teamScores.computeIfAbsent(matchID, m -> new ConcurrentHashMap<>());
        teamScores.get(matchID).merge(team, 1, Integer::sum);
    }

    public static Map<Long, PlayerStats> getPlayerStats(String matchID) {
        return matchStats.getOrDefault(matchID, Map.of());
    }

    public static Map<String, Integer> getTeamScores(String matchID) {
        return teamScores.getOrDefault(matchID, Map.of());
    }

    public static void clear(String matchID) {
        matchStats.remove(matchID);
        teamScores.remove(matchID);
    }
}
