package com.zonezone.backend.gameHandler;

import com.google.gson.Gson;

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

@ServerEndpoint("/startMatch")
public class GameSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("🟢 Connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String messageJson, Session session) {
        Gson gson = new Gson();

        if (messageJson.contains("\"type\":\"chat\"")) {
            ChatPayload chat = gson.fromJson(messageJson, ChatPayload.class);
            LiveMatchChatManager.postMessage(chat.matchID, chat.senderID, chat.message, chat.scope);
            System.out.println("💬 Chat [" + chat.scope + "] from " + chat.senderID + ": " + chat.message);

            switch (chat.scope.toLowerCase()) {
                case "all" -> broadcastToAll(chat.matchID, messageJson);
                case "teama" -> broadcastToTeam(chat.matchID, messageJson, "A");
                case "teamb" -> broadcastToTeam(chat.matchID, messageJson, "B");
            }
        }
        else if (messageJson.contains("\"type\":\"join\"")) {
            JoinPayload join = gson.fromJson(messageJson, JoinPayload.class);
            MatchSessionManager.addPlayerSession(join.matchID, join.userID, session);
        }
        else {
            MatchStartPayloadDTO match = gson.fromJson(messageJson, MatchStartPayloadDTO.class);
            MatchSessionManager.addMatch(match);
            System.out.println("📦 New Match Created: " + match.matchID);

            new Thread(() -> {
                try {
                    Thread.sleep(60000);
                    sendMatchEnd(match);
                    MatchSessionManager.removeMatch(match.matchID);
                    LiveMatchChatManager.clearChat(match.matchID);
                    System.out.println("🏁 Match ended: " + match.matchID);
                } catch (InterruptedException ignored) {}
            }).start();
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
    private static class JoinPayload {
        public String type;
        public String matchID;
        public String userID;
    }

    private void sendMatchEnd(MatchStartPayloadDTO match) {
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
