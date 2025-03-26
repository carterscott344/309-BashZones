package com.zonezone.backend;

import com.google.gson.Gson;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/startMatch")
public class GameSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("🟢 Connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        MatchStartPayloadDTO match = new Gson().fromJson(message, MatchStartPayloadDTO.class);
        MatchSessionManager.addMatch(match);

        System.out.println("📦 New Match Created:");
        System.out.println("Match ID: " + match.matchID);
        System.out.println("Team A: " + match.teamA);
        System.out.println("Team B: " + match.teamB);
    }

}