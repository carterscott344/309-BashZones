package com.zonezone.backend.gameHandler.mechanicsHandlers;

import com.google.gson.JsonObject;
import com.zonezone.backend.gameHandler.GameSocketServer;
import com.zonezone.backend.gameHandler.MatchAddPayload;
import com.zonezone.backend.gameHandler.MatchSessionManager;
import com.zonezone.backend.gameHandler.LeaderboardTracker;
import com.zonezone.backend.gameHandler.LiveMatchChatManager;

import javax.websocket.Session;
import java.util.*;

public class ObjectiveCalculatorManager {

    private static final Map<String, Set<String>> playersOnObjective = new HashMap<>();
    private static final Map<String, Integer> controlPoints = new HashMap<>();
    private static final Map<String, Integer> objectiveIndex = new HashMap<>();
    private static final Map<String, String> controllingTeam = new HashMap<>();

    public static void updatePlayerObjectiveStatus(String matchID, String userID, boolean isOnObjective) {
        playersOnObjective.computeIfAbsent(matchID, k -> new HashSet<>());

        if (isOnObjective) {
            playersOnObjective.get(matchID).add(userID);
        } else {
            playersOnObjective.get(matchID).remove(userID);
        }
    }

    public static void startObjectiveLoop(String matchID) {
        objectiveIndex.put(matchID, 1);
        controlPoints.put(matchID, 0);
        controllingTeam.put(matchID, "None");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MatchAddPayload match = MatchSessionManager.getMatch(matchID);
                if (match == null) {
                    cancel();
                    return;
                }

                Set<String> players = playersOnObjective.getOrDefault(matchID, Set.of());
                Set<String> teamA = new HashSet<>(match.teamA);
                Set<String> teamB = new HashSet<>(match.teamB);

                boolean teamAOnPoint = players.stream().anyMatch(teamA::contains);
                boolean teamBOnPoint = players.stream().anyMatch(teamB::contains);

                String activeTeam = "None";
                if (teamAOnPoint && !teamBOnPoint) activeTeam = "Red";
                else if (teamBOnPoint && !teamAOnPoint) activeTeam = "Blue";

                String currentTeam = controllingTeam.getOrDefault(matchID, "None");
                int percent = controlPoints.getOrDefault(matchID, 0);

                if (!activeTeam.equals("None")) {
                    if (currentTeam.equals("None")) {
                        controllingTeam.put(matchID, activeTeam);
                        controlPoints.put(matchID, 1);
                    } else if (currentTeam.equals(activeTeam)) {
                        controlPoints.put(matchID, Math.min(15, percent + 1));
                    } else {
                        if (percent > 0) {
                            controlPoints.put(matchID, percent - 1);
                        } else {
                            controllingTeam.put(matchID, activeTeam);
                            controlPoints.put(matchID, 1);
                        }
                    }
                }

                if (controlPoints.get(matchID) >= 15) {
                    String scoringTeam = controllingTeam.get(matchID);
                    int index = objectiveIndex.get(matchID);

                    if (scoringTeam.equals("Red")) {
                        index = Math.min(2, index + 1);
                    } else if (scoringTeam.equals("Blue")) {
                        index = Math.max(0, index - 1);
                    }

                    MatchSessionManager.getAllSessions(matchID).forEach(session -> {
                        try {
                            session.getBasicRemote().sendText("{\"type\":\"objectivePoint\",\"scoringTeam\":\"" + scoringTeam + "\"}");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    String teamCode = scoringTeam.equals("Red") ? "A" : "B";
                    LeaderboardTracker.addTeamScore(matchID, teamCode);
                    int newScore = LeaderboardTracker.getTeamScores(matchID).getOrDefault(teamCode, 0);

                    JsonObject leaderboardUpdate = new JsonObject();
                    leaderboardUpdate.addProperty("type", "leaderboardSnapshot");
                    leaderboardUpdate.addProperty("matchID", matchID);
                    leaderboardUpdate.addProperty("updateType", "objective");
                    leaderboardUpdate.addProperty("team", teamCode);

                    for (Session s : MatchSessionManager.getAllSessions(matchID)) {
                        try {
                            s.getBasicRemote().sendText(leaderboardUpdate.toString());
                        } catch (Exception e) {
                            System.err.println("❌ Failed to send leaderboard update: " + e.getMessage());
                        }
                    }

                    if (newScore >= 5) {
                        for (Session s : MatchSessionManager.getAllSessions(matchID)) {
                            try {
                                String userId = MatchSessionManager.getUserIDFromSession(s);
                                String userTeam = match.teamA.contains(userId) ? "A" : "B";
                                JsonObject endMessage = new JsonObject();
                                endMessage.addProperty("type", "endMatch");
                                endMessage.addProperty("result", userTeam.equals(teamCode) ? "Win" : "Lose");
                                s.getBasicRemote().sendText(endMessage.toString());
                            } catch (Exception e) {
                                System.err.println("❌ Failed to send matchEnded packet: " + e.getMessage());
                            }
                        }

                        GameSocketServer.sendMatchEnd(match, teamCode);
                        MatchSessionManager.removeMatch(matchID);
                        LiveMatchChatManager.clearChat(matchID);
                        System.out.println("🏁 Team " + teamCode + " wins! Match ended: " + matchID);
                        cancel();
                        return;
                    }

                    objectiveIndex.put(matchID, index);
                    controlPoints.put(matchID, 0);
                    controllingTeam.put(matchID, "None");
                }

                JsonObject update = new JsonObject();
                update.addProperty("type", "updateObjective");
                update.addProperty("activeObjective", objectiveIndex.get(matchID));
                update.addProperty("controlledBy", controllingTeam.get(matchID));
                update.addProperty("percentControl", controlPoints.get(matchID));

                for (Session s : MatchSessionManager.getAllSessions(matchID)) {
                    try {
                        s.getBasicRemote().sendText(update.toString());
                    } catch (Exception e) {
                        System.err.println("❌ Failed to send objective update: " + e.getMessage());
                    }
                }
            }
        }, 1000, 1000);
    }

    public static String getCurrentControl(String matchID) {
        return controllingTeam.getOrDefault(matchID, "None");
    }
}
