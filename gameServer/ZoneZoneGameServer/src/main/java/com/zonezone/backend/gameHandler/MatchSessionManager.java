package com.zonezone.backend.gameHandler;

import javax.websocket.Session;
import java.util.*;

public class MatchSessionManager {

    private static final Map<String, MatchStartPayloadDTO> activeMatches = new HashMap<>();
    private static final Map<String, Set<Session>> teamASessions = new HashMap<>();
    private static final Map<String, Set<Session>> teamBSessions = new HashMap<>();
    private static final Map<String, Set<Session>> allSessions = new HashMap<>();
    private static final Map<Session, String> sessionToMatchID = new HashMap<>();

    public static void addMatch(MatchStartPayloadDTO match) {
        activeMatches.put(match.matchID, match);
        teamASessions.put(match.matchID, new HashSet<>());
        teamBSessions.put(match.matchID, new HashSet<>());
        allSessions.put(match.matchID, new HashSet<>());
        System.out.println("🆕 Match added to session: " + match.matchID);
    }

    public static MatchStartPayloadDTO getMatch(String matchID) {
        return activeMatches.get(matchID);
    }

    public static void addPlayerSession(String matchID, String userID, Session session) {
        MatchStartPayloadDTO match = getMatch(matchID);
        if (match == null) {
            System.err.println("❌ Tried to add session to unknown matchID: " + matchID);
            return;
        }

        allSessions.get(matchID).add(session);
        sessionToMatchID.put(session, matchID);

        if (match.teamA.contains(userID)) {
            teamASessions.get(matchID).add(session);
            System.out.println("👤 User " + userID + " added to Team A in match " + matchID);
        }
        else if (match.teamB.contains(userID)) {
            teamBSessions.get(matchID).add(session);
            System.out.println("👤 User " + userID + " added to Team B in match " + matchID);
        }
        else {
            System.err.println("⚠️ User " + userID + " not found in match " + matchID);
        }
    }

    public static void removeMatch(String matchID) {
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

    public static int getMatchCount() {
        return activeMatches.size();
    }
}
