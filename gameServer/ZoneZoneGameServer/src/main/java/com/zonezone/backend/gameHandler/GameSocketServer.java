package com.zonezone.backend.gameHandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zonezone.backend.gameHandler.mechanicsHandlers.MovementPayloadDTO;
import com.zonezone.backend.gameHandler.mechanicsHandlers.PlayerPositionTracker;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Set;

@ServerEndpoint("/connectToServer")
public class GameSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        MatchSessionManager.registerSession(session);
        System.out.println("🟢 Player Connected: SessionID: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        MatchSessionManager.unregisterSession(session);
        System.out.println("🔴 Player Disconnected: SessionID: " + session.getId());
    }

    @OnMessage
    public void onMessage(String messageJson, Session session) {
        Gson gson = new Gson();
       // System.out.println("📨 Incoming message: " + messageJson);

        try {
            JsonObject root = gson.fromJson(messageJson, JsonObject.class);

            String type = root.get("type").getAsString();
           //f System.out.println("🔍 Detected message type: " + type);

            switch (type) {
                case "chat" -> {
                    //System.out.println("1");
                    ChatPayload chat = gson.fromJson(messageJson, ChatPayload.class);
                    LiveMatchChatManager.postMessage(chat.matchID, chat.senderID, chat.message, chat.scope);
                    System.out.println("💬 Chat [" + chat.scope + "] from " + chat.senderID + ": " + chat.message);

                    String senderUsername = MatchSessionManager.getUsername(chat.senderID);

                    JsonObject response = new JsonObject();
                    response.addProperty("type", "chat");
                    response.addProperty("matchID", chat.matchID);
                    response.addProperty("senderID", chat.senderID);
                    response.addProperty("senderUsername", senderUsername); // ✅
                    response.addProperty("scope", chat.scope);
                    response.addProperty("message", chat.message);

                    switch (chat.scope.toLowerCase()) {
                        case "all" -> broadcastToAll(chat.matchID, response.toString());
                        case "teama" -> broadcastToTeam(chat.matchID, response.toString(), "A");
                        case "teamb" -> broadcastToTeam(chat.matchID, response.toString(), "B");
                    }
                    System.out.println("message from " + senderUsername + " : " + response.toString());


                }
                case "join" -> {
                    PlayerJoinPayloadDTO join = gson.fromJson(messageJson, PlayerJoinPayloadDTO.class);
                    MatchSessionManager.addPlayerSession(join.getMatchID(), join.getUserID(), session);
                    System.out.println("👋 Player " + join.getUserID() + " joined match " + join.getMatchID());

                    // 🎯 Assign default spawn positions
                    long userId = Long.parseLong(join.getUserID());
                    double defaultX = 0.0;
                    double defaultY = 0.0;
                    int defaultRotation = 0;

                    // Assign different positions depending on user slot
                    MatchAddPayload match = MatchSessionManager.getMatch(join.getMatchID());
                    if (match != null) {
                        if (match.teamA.contains(join.getUserID())) {
                            int index = match.teamA.indexOf(join.getUserID());
                            defaultX = 5.0 + index * 2;  // e.g. 5.0, 7.0
                            defaultY = 5.0;
                            defaultRotation = 90;
                        }
                        else if (match.teamB.contains(join.getUserID())) {
                            int index = match.teamB.indexOf(join.getUserID());
                            defaultX = 15.0 + index * 2; // e.g. 15.0, 17.0
                            defaultY = 15.0;
                            defaultRotation = 270;
                        }
                    }

                    PlayerPositionTracker.updatePosition(userId, defaultX, defaultY, defaultRotation);

                    System.out.println("🧭 Default position for " + userId + " → (" + defaultX + ", " + defaultY + ") ↻ " + defaultRotation + "°");
                }

                case "playerPosition" -> {
                    //System.out.println("📡 Received movement update");

                    MovementPayloadDTO movement = gson.fromJson(messageJson, MovementPayloadDTO.class);

                    PlayerPositionTracker.updatePosition(
                            movement.getUserID(),
                            movement.getPlayerXPosition(),
                            movement.getPlayerYPosition(),
                            movement.getRotationDegrees()
                    );

                    // 📦 Lookup the match ID for this session
                    String matchID = MatchSessionManager.getMatchIDFromSession(session);
                    if (matchID == null) {
                        session.getBasicRemote().sendText("{\"type\":\"error\",\"message\":\"Match not found for session.\"}");
                        return;
                    }

                    MatchAddPayload match = MatchSessionManager.getMatch(matchID);
                    if (match == null) {
                        session.getBasicRemote().sendText("{\"type\":\"error\",\"message\":\"Match data not found.\"}");
                        return;
                    }

                    JsonArray playerStates = new JsonArray();
                    for (String userID : match.teamA) {
                        addPlayerState(playerStates, Long.parseLong(userID));
                    }
                    for (String userID : match.teamB) {
                        addPlayerState(playerStates, Long.parseLong(userID));
                    }

                    JsonObject response = new JsonObject();
                    response.addProperty("type", "allPlayerPositions");
                    response.addProperty("matchID", matchID);
                    response.add("players", playerStates);

                    session.getBasicRemote().sendText(response.toString());

                    /*System.out.println("📍 User " + movement.getUserID() + " moved → (" +
                            movement.getPlayerXPosition() + ", " + movement.getPlayerYPosition() + ") ↻ " +
                            movement.getRotationDegrees() + "°");

                    System.out.println("📤 Sent updated positions for match: " + matchID);*/
                }

                case "matchAdd" -> {
                    MatchAddPayload match = gson.fromJson(messageJson, MatchAddPayload.class);
                    MatchSessionManager.addMatch(match);
                    System.out.println("📦 New Match Added: " + match.matchID);

                    // 📢 Broadcast match info to all connected clients
                    JsonObject broadcast = new JsonObject();
                    broadcast.addProperty("type", "matchBroadcast");
                    broadcast.addProperty("matchID", match.matchID);
                    broadcast.addProperty("gameMode", match.gameMode);

                    JsonArray teamA = new JsonArray();
                    JsonArray teamB = new JsonArray();
                    match.teamA.forEach(teamA::add);
                    match.teamB.forEach(teamB::add);
                    broadcast.add("teamA", teamA);
                    broadcast.add("teamB", teamB);

                    for (Session s : MatchSessionManager.getGlobalSessions()) {
                        try {
                            s.getBasicRemote().sendText(broadcast.toString());
                        } catch (Exception e) {
                            System.err.println("❌ Failed to broadcast match: " + e.getMessage());
                        }
                    }

                    for (String userId : match.teamA) {
                        String username = fetchUsernameFromBackend(userId); // implement this
                        MatchSessionManager.registerUsername(userId, username);
                    }

                    for (String userId : match.teamB) {
                        String username = fetchUsernameFromBackend(userId); // implement this
                        MatchSessionManager.registerUsername(userId, username);
                    }

                    System.out.println("📡 Match broadcast sent to all connected clients.");
                }

                case "playerLoaded" -> {
                    String matchID = MatchSessionManager.getMatchIDFromSession(session);
                    if (matchID == null) {
                        session.getBasicRemote().sendText("{\"type\":\"error\",\"message\":\"No match for this session.\"}");
                        return;
                    }

                    MatchSessionManager.markPlayerLoaded(matchID, session);
                    int count = MatchSessionManager.getLoadedCount(matchID);
                    int total = MatchSessionManager.getMatch(matchID).teamA.size() +
                            MatchSessionManager.getMatch(matchID).teamB.size();

                    System.out.println("✅ Player loaded in match " + matchID + " → " + count + "/" + total);

                    if (count == total) {
                        System.out.println("🚀 All players loaded in match " + matchID);

                        // Build snapshot
                        JsonArray playerStates = new JsonArray();
                        for (String userID : MatchSessionManager.getMatch(matchID).teamA) {
                            addPlayerState(playerStates, Long.parseLong(userID));
                        }
                        for (String userID : MatchSessionManager.getMatch(matchID).teamB) {
                            addPlayerState(playerStates, Long.parseLong(userID));
                        }

                        JsonObject startPacket = new JsonObject();
                        startPacket.addProperty("type", "loadedGame");
                        startPacket.addProperty("matchID", matchID);
                        startPacket.add("players", playerStates);

                        broadcastToAll(matchID, startPacket.toString());

                        // Start match timer
                        new Thread(() -> {
                            try {
                                for (int seconds = 300; seconds >= 0; seconds--) {
                                    JsonObject clockMessage = new JsonObject();
                                    clockMessage.addProperty("type", "clock");
                                    clockMessage.addProperty("matchID", matchID);
                                    clockMessage.addProperty("timeRemaining", seconds);

                                    broadcastToAll(matchID, clockMessage.toString());

                                    Thread.sleep(1000); // Wait 1 second
                                }

                                // End match after countdown
                                MatchAddPayload match = MatchSessionManager.getMatch(matchID);
                                if (match != null) {
                                    // 🎲 Randomly pick a winning team
                                    String randomWinningTeam = Math.random() < 0.5 ? "A" : "B";

                                    sendMatchEnd(match, randomWinningTeam);
                                    MatchSessionManager.removeMatch(matchID);
                                    LiveMatchChatManager.clearChat(matchID);
                                    System.out.println("🏁 Match ended by timeout — Random Winner: Team " + randomWinningTeam + " (matchID: " + matchID + ")");
                                }
                            } catch (Exception e) {
                                System.err.println("❌ Error during match countdown: " + e.getMessage());
                            }
                        }).start();
                    }
                }

                case "sound" -> {
                    String matchID = root.get("matchID").getAsString();
                    String soundType = root.get("soundType").getAsString();
                    String soundName = root.get("soundName").getAsString();

                    if (soundType.equals("global")) {
                        broadcastSoundToAll(matchID, soundName);
                    } else if (soundType.equals("localized")) {
                        double x = root.get("x").getAsDouble();
                        double y = root.get("y").getAsDouble();
                        double radius = root.has("radius") ? root.get("radius").getAsDouble() : 10.0; // default radius if not provided

                        broadcastSoundInRadius(matchID, soundName, x, y, radius);
                    }
                }

                case "leaderboardSnapshot" -> {
                    String matchID = root.get("matchID").getAsString();
                    String updateType = root.get("updateType").getAsString();

                    switch (updateType) {
                        case "kill" -> {
                            long killerID = root.get("killerID").getAsLong();
                            long victimID = root.get("victimID").getAsLong();
                            LeaderboardTracker.recordKill(matchID, killerID, victimID);
                        }
                        case "objective" -> {
                            String team = root.get("team").getAsString();
                            LeaderboardTracker.addTeamScore(matchID, team);

                            // 🏆 Check if this team reached winning score
                            int currentScore = LeaderboardTracker.getTeamScores(matchID).getOrDefault(team, 0);
                            if (currentScore >= 5) { // 🎯 Target score to win
                                MatchAddPayload match = MatchSessionManager.getMatch(matchID);
                                if (match != null) {
                                    sendMatchEnd(match, team);
                                    MatchSessionManager.removeMatch(matchID);
                                    LiveMatchChatManager.clearChat(matchID);
                                    System.out.println("🏁 Team " + team + " wins! Match ended: " + matchID);
                                } else {
                                    System.err.println("❌ Could not find match data for matchID: " + matchID);
                                }
                                return; // ⛔ Stop further broadcasting after match ends
                            }
                        }
                        default -> {
                            System.err.println("❌ Unknown leaderboard update type: " + updateType);
                            return;
                        }
                    }

                    // 📦 Build and broadcast updated leaderboard snapshot
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "updatedLeaderboard");
                    response.addProperty("matchID", matchID);

                    JsonArray players = new JsonArray();
                    LeaderboardTracker.getPlayerStats(matchID).forEach((userId, stats) -> {
                        JsonObject entry = new JsonObject();
                        entry.addProperty("userId", userId);
                        entry.addProperty("kills", stats.kills);
                        entry.addProperty("deaths", stats.deaths);
                        players.add(entry);
                    });

                    JsonObject scores = new JsonObject();
                    LeaderboardTracker.getTeamScores(matchID).forEach(scores::addProperty);

                    response.add("players", players);
                    response.add("teamScores", scores);

                    broadcastToAll(matchID, response.toString());
                    System.out.println("📊 Leaderboard updated for match: " + matchID);
                }


                default -> {
                    // fallback to match payload

                }
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to process message: " + messageJson);
            e.printStackTrace();
        }
    }

    private void addPlayerState(JsonArray array, Long userId) {
        PlayerPositionTracker.PlayerPosition pos = PlayerPositionTracker.getPosition(userId);
        if (pos != null) {
            JsonObject player = new JsonObject();
            player.addProperty("userID", userId);
            player.addProperty("x", pos.x);
            player.addProperty("y", pos.y);
            player.addProperty("rotation", pos.rotation);
            array.add(player);
        }
    }

    private void broadcastToAll(String matchID, String message) {
        for (Session s : MatchSessionManager.getAllSessions(matchID)) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (Exception e) {
                System.err.println("❌ Failed to send message to all chat session: " + e.getMessage());
            }
        }
    }

    private void broadcastToTeam(String matchID, String message, String team) {
        Set<Session> targets = team.equals("A")
                ? MatchSessionManager.getTeamASessions(matchID)
                : MatchSessionManager.getTeamBSessions(matchID);

        for (Session s : targets) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (Exception e) {
                System.err.println("❌ Failed to send message to team " + team + " session: " + e.getMessage());
            }
        }
    }

    private void broadcastSoundInRadius(String matchID, String soundName, double sourceX, double sourceY, double radius) {
        JsonObject soundPacket = new JsonObject();
        soundPacket.addProperty("type", "sound");
        soundPacket.addProperty("matchID", matchID);
        soundPacket.addProperty("soundType", "localized");
        soundPacket.addProperty("soundName", soundName);
        soundPacket.addProperty("x", sourceX);
        soundPacket.addProperty("y", sourceY);

        for (Session session : MatchSessionManager.getAllSessions(matchID)) {
            try {
                String userID = MatchSessionManager.getUserIDFromSession(session);
                String username = MatchSessionManager.getUsername(userID);
                if (userID == null) continue;

                PlayerPositionTracker.PlayerPosition pos = PlayerPositionTracker.getPosition(Long.parseLong(userID));
                if (pos == null) continue;

                double distance = Math.hypot(pos.x - sourceX, pos.y - sourceY);
                if (distance <= radius) {
                    session.getBasicRemote().sendText(soundPacket.toString());
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to send sound to session: " + e.getMessage());
            }
        }
    }

    private void broadcastSoundToAll(String matchID, String soundName) {
        JsonObject soundPacket = new JsonObject();
        soundPacket.addProperty("type", "sound");
        soundPacket.addProperty("matchID", matchID);
        soundPacket.addProperty("soundType", "global");
        soundPacket.addProperty("soundName", soundName);
        soundPacket.addProperty("x", 0); // optional
        soundPacket.addProperty("y", 0); // optional

        for (Session s : MatchSessionManager.getAllSessions(matchID)) {
            try {
                s.getBasicRemote().sendText(soundPacket.toString());
            } catch (Exception e) {
                System.err.println("❌ Failed to send global sound to session: " + e.getMessage());
            }
        }
    }

    private static class ChatPayload {
        public String type;
        public String matchID;
        public String senderID;
        public String scope;
        public String message;
    }

    private String fetchUsernameFromBackend(String userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://coms-3090-046.class.las.iastate.edu:8080/accountUsers/" + userId + "/getUsername"))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.err.println("❌ Failed to fetch username for user " + userId + ": " + e.getMessage());
            return "Unknown";
        }
    }

    private void sendMatchEnd(MatchAddPayload match, String winningTeam) {
        MatchEndPayloadDTO endPayload = new MatchEndPayloadDTO();
        endPayload.matchID = match.matchID;
        endPayload.players = new ArrayList<>();
        endPayload.players.addAll(match.teamA.stream().map(Long::parseLong).toList());
        endPayload.players.addAll(match.teamB.stream().map(Long::parseLong).toList());

        endPayload.winningTeam = winningTeam;
        endPayload.winningScore = LeaderboardTracker.getTeamScores(match.matchID).getOrDefault(winningTeam, 0);

        endPayload.losingTeam = winningTeam.equals("A") ? "B" : "A";
        endPayload.losingScore = LeaderboardTracker.getTeamScores(match.matchID).getOrDefault(endPayload.losingTeam, 0);

        endPayload.totalKills = LeaderboardTracker.getPlayerStats(match.matchID)
                .values().stream().mapToInt(stats -> stats.kills).sum();
        endPayload.totalDeaths = LeaderboardTracker.getPlayerStats(match.matchID)
                .values().stream().mapToInt(stats -> stats.deaths).sum();

        String json = new Gson().toJson(endPayload);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/match/end"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📤 Match end sent to backend for matchID: " + match.matchID);
        }
        catch (Exception e) {
            System.err.println("❌ Failed to notify backend of match end.");
            e.printStackTrace();
        }
    }

}
