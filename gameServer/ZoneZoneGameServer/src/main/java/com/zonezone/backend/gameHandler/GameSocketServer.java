package com.zonezone.backend.gameHandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zonezone.backend.gameHandler.mechanicsHandlers.MovementPayloadDTO;
import com.zonezone.backend.gameHandler.mechanicsHandlers.PlayerPositionTracker;

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
        System.out.println("🟢 Player Connected: SessionID: " + session.getId());
    }

    @OnMessage
    public void onMessage(String messageJson, Session session) {
        Gson gson = new Gson();
        System.out.println("📨 Incoming message: " + messageJson);

        try {
            JsonObject root = gson.fromJson(messageJson, JsonObject.class);

            String type = root.get("type").getAsString();
            System.out.println("🔍 Detected message type: " + type);

            switch (type) {
                case "chat" -> {
                    System.out.println("1");
                    ChatPayload chat = gson.fromJson(messageJson, ChatPayload.class);
                    LiveMatchChatManager.postMessage(chat.matchID, chat.senderID, chat.message, chat.scope);
                    System.out.println("💬 Chat [" + chat.scope + "] from " + chat.senderID + ": " + chat.message);

                    switch (chat.scope.toLowerCase()) {
                        case "all" -> broadcastToAll(chat.matchID, messageJson);
                        case "teama" -> broadcastToTeam(chat.matchID, messageJson, "A");
                        case "teamb" -> broadcastToTeam(chat.matchID, messageJson, "B");
                    }
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
                    System.out.println("📡 Received movement update");

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

                    System.out.println("📍 User " + movement.getUserID() + " moved → (" +
                            movement.getPlayerXPosition() + ", " + movement.getPlayerYPosition() + ") ↻ " +
                            movement.getRotationDegrees() + "°");

                    System.out.println("📤 Sent updated positions for match: " + matchID);
                }

                case "matchAdd" -> {
                    MatchAddPayload match = gson.fromJson(messageJson, MatchAddPayload.class);
                    MatchSessionManager.addMatch(match);
                    System.out.println("📦 New Match Added: " + match.matchID);
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
                        startPacket.addProperty("type", "allPlayerPositions");
                        startPacket.addProperty("matchID", matchID);
                        startPacket.add("players", playerStates);

                        broadcastToAll(matchID, startPacket.toString());

                        // Start match timer
                        new Thread(() -> {
                            try {
                                Thread.sleep(180000); // 60s
                                sendMatchEnd(MatchSessionManager.getMatch(matchID));
                                MatchSessionManager.removeMatch(matchID);
                                LiveMatchChatManager.clearChat(matchID);
                                System.out.println("🏁 Match ended: " + matchID);
                            } catch (Exception e) {
                                System.err.println("❌ Error during match timer: " + e.getMessage());
                            }
                        }).start();
                    }
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
            player.addProperty("userId", userId);
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


    private static class ChatPayload {
        public String type;
        public String matchID;
        public String senderID;
        public String scope;
        public String message;
    }

    private void sendMatchEnd(MatchAddPayload match) {
        MatchEndPayloadDTO endPayload = new MatchEndPayloadDTO();
        endPayload.matchID = match.matchID;
        endPayload.players = new ArrayList<>();
        endPayload.players.addAll(match.teamA.stream().map(Long::parseLong).toList());
        endPayload.players.addAll(match.teamB.stream().map(Long::parseLong).toList());

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
