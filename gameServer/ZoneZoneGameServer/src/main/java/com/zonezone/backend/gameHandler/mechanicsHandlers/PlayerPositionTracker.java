package com.zonezone.backend.gameHandler.mechanicsHandlers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class PlayerPositionTracker {

    private static final Map<Long, PlayerPosition> playerPositions = new ConcurrentHashMap<>();

    public static void updatePosition(Long userId, double x, double y, int rotation) {
        playerPositions.put(userId, new PlayerPosition(x, y, rotation));
    }

    public static PlayerPosition getPosition(Long userId) {
        return playerPositions.get(userId);
    }

    public static Map<Long, PlayerPosition> getAllPositions() {
        return playerPositions;
    }

    public static class PlayerPosition {
        public final double x;
        public final double y;
        public final int rotation;

        public PlayerPosition(double x, double y, int rotation) {
            this.x = x;
            this.y = y;
            this.rotation = rotation;
        }
    }
}

