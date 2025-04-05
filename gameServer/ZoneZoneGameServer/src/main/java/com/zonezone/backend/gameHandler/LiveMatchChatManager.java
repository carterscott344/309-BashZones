package com.zonezone.backend.gameHandler;

import java.util.*;

public class LiveMatchChatManager {

    private static final Map<String, List<ChatMessage>> allChatMap = new HashMap<>();
    private static final Map<String, List<ChatMessage>> teamAChatMap = new HashMap<>();
    private static final Map<String, List<ChatMessage>> teamBChatMap = new HashMap<>();

    public static void postMessage(String matchID, String senderID, String message, String scope) {
        ChatMessage chat = new ChatMessage(senderID, message, new Date());

        switch (scope.toLowerCase()) {
            case "all" -> allChatMap.computeIfAbsent(matchID, k -> new ArrayList<>()).add(chat);
            case "teama" -> teamAChatMap.computeIfAbsent(matchID, k -> new ArrayList<>()).add(chat);
            case "teamb" -> teamBChatMap.computeIfAbsent(matchID, k -> new ArrayList<>()).add(chat);
        }
    }

    public static List<ChatMessage> getMessages(String matchID, String scope) {
        return switch (scope.toLowerCase()) {
            case "all" -> allChatMap.getOrDefault(matchID, new ArrayList<>());
            case "teama" -> teamAChatMap.getOrDefault(matchID, new ArrayList<>());
            case "teamb" -> teamBChatMap.getOrDefault(matchID, new ArrayList<>());
            default -> new ArrayList<>();
        };
    }

    public static void clearChat(String matchID) {
        allChatMap.remove(matchID);
        teamAChatMap.remove(matchID);
        teamBChatMap.remove(matchID);
    }

    public record ChatMessage(String senderID, String message, Date timestamp) {}
}
