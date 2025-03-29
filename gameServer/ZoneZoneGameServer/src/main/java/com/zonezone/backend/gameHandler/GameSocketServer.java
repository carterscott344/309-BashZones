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

@ServerEndpoint("/startMatch")
public class GameSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("🟢 Connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Gson gson = new Gson();
        MatchStartPayloadDTO match = gson.fromJson(message, MatchStartPayloadDTO.class);

        MatchSessionManager.addMatch(match);

        System.out.println("🆕 Match added to session: " + match.matchID);
        System.out.println("📦 New Match Created:");
        System.out.println("Match ID: " + match.matchID);
        System.out.println("Team A: " + match.teamA);
        System.out.println("Team B: " + match.teamB);

        // 🔁 Optional: simulate match ending after 10 seconds
        new Thread(() -> {
            try {
                Thread.sleep(60000); // simulate 60 second game
                sendMatchEnd(match);
                MatchSessionManager.removeMatch(match.matchID);
                System.out.println("🏁 Match ended: " + match.matchID);
            }
            catch (InterruptedException ignored) {}
        }).start();
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
