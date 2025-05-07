package com.zonezone.backend.gameHandler;

import javax.websocket.Session;
import java.util.*;

public class MatchSessionManager {

    private static final Map<String, MatchAddPayload> activeMatches = new HashMap<>();
    private static final Map<String, Set<Session>> teamASessions = new HashMap<>();
    private static final Map<String, Set<Session>> teamBSessions = new HashMap<>();
    private static final Map<String, Set<Session>> allSessions = new HashMap<>();
    private static final Map<Session, String> sessionToMatchID = new HashMap<>();
    private static final Map<String, Set<Session>> loadedPlayers = new HashMap<>();

    private static final Map<String, String> userIdToUsername = new HashMap<>();
    private static final Map<String, String> userIdToTeam = new HashMap<>(); // ✅ NEW: Track team per user

    private static final Set<Session> globalSessions = new HashSet<>();

    private static final Map<Session, String> sessionToUserID = new HashMap<>();

    public static void addMatch(MatchAddPayload match) {
        activeMatches.put(match.matchID, match);
        teamASessions.put(match.matchID, new HashSet<>());
        teamBSessions.put(match.matchID, new HashSet<>());
        allSessions.put(match.matchID, new HashSet<>());
        System.out.println("🆕 Match added to session: " + match.matchID);
    }

    public static MatchAddPayload getMatch(String matchID) {
        return activeMatches.get(matchID);
    }

    public static void addPlayerSession(String matchID, String userID, Session session) {
        MatchAddPayload match = getMatch(matchID);
        if (match == null) {
            System.err.println("❌ Tried to add session to unknown matchID: " + matchID);
            return;
        }

        allSessions.get(matchID).add(session);
        sessionToMatchID.put(session, matchID);
        sessionToUserID.put(session, userID);

        if (match.teamA.contains(userID)) {
            teamASessions.get(matchID).add(session);
            userIdToTeam.put(userID, "A"); // ✅ Track team assignment
            System.out.println("👤 User " + userID + " added to Team A in match " + matchID);
        } else if (match.teamB.contains(userID)) {
            teamBSessions.get(matchID).add(session);
            userIdToTeam.put(userID, "B"); // ✅ Track team assignment
            System.out.println("👤 User " + userID + " added to Team B in match " + matchID);
        } else {
            System.err.println("⚠️ User " + userID + " not found in match " + matchID);
        }
    }

    public static void removeMatch(String matchID) {
        MatchAddPayload match = activeMatches.get(matchID);

        // ✅ Clean up team assignment mapping
        if (match != null) {
            match.teamA.forEach(userIdToTeam::remove);
            match.teamB.forEach(userIdToTeam::remove);
        }

        activeMatches.remove(matchID);
        teamASessions.remove(matchID);
        teamBSessions.remove(matchID);
        Set<Session> sessions = allSessions.remove(matchID);
        if (sessions != null) {
            sessions.forEach(sessionToMatchID::remove);
        }
        System.out.println("🗑️ Match removed from session: " + matchID);
    }

    public static Set<Session> getAllSessions(String matchID) {
        return allSessions.getOrDefault(matchID, Set.of());
    }

    public static Set<Session> getTeamASessions(String matchID) {
        return teamASessions.getOrDefault(matchID, Set.of());
    }

    public static Set<Session> getTeamBSessions(String matchID) {
        return teamBSessions.getOrDefault(matchID, Set.of());
    }

    public static String getMatchIDFromSession(Session session) {
        return sessionToMatchID.get(session);
    }

    public static void markPlayerLoaded(String matchID, Session session) {
        loadedPlayers.computeIfAbsent(matchID, k -> new HashSet<>()).add(session);
    }

    public static int getLoadedCount(String matchID) {
        return loadedPlayers.getOrDefault(matchID, Set.of()).size();
    }

    public static int getMatchCount() {
        return activeMatches.size();
    }

    public static void registerUsername(String userId, String username) {
        userIdToUsername.put(userId, username);
    }

    public static String getUsername(String userId) {
        return userIdToUsername.getOrDefault(userId, "Unknown");
    }

    public static void registerSession(Session session) {
        globalSessions.add(session);
    }

    public static void unregisterSession(Session session) {
        globalSessions.remove(session);
        sessionToUserID.remove(session); // ✅ clean
        sessionToMatchID.remove(session); // ✅ clean
    }

    public static Set<Session> getGlobalSessions() {
        return globalSessions;
    }

    public static String getUserIDFromSession(Session session) {
        return sessionToUserID.get(session);
    }

    public static String getUsernameFromSession(Session session) {
        String userID = sessionToUserID.get(session);
        if (userID == null) {
            return "Unknown";
        }
        return userIdToUsername.getOrDefault(userID, "Unknown");
    }

    // ✅ NEW: Get a player's team ("A" or "B")
    public static String getTeamForUser(String userID) {
        return userIdToTeam.get(userID);
    }

    // ✅ NEW: Get a player's team color (0 for red, 1 for blue)
    public static Integer getTeamColorForUser(String userID) {
        String team = getTeamForUser(userID);
        if (team == null) return null;
        return team.equals("A") ? 0 : 1;
    }
}
