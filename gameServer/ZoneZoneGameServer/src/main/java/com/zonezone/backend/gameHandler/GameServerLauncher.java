package com.zonezone.backend.gameHandler;

import org.glassfish.tyrus.server.Server;

import java.util.Scanner;

public class GameServerLauncher {

    public static void main(String[] args) {
        Server server = new Server("localhost", 8025, "/ws", null, GameSocketServer.class);

        try {
            server.start();
            System.out.println("✅ ZoneZone Game Server running at ws://localhost:8025/ws/startMatch");
            System.out.println("Press Enter to shut down...");
            new Scanner(System.in).nextLine();
        }
        catch (Exception e) {
            System.err.println("❌ Failed to start game server:");
            e.printStackTrace();
        }
        finally {
            server.stop();
        }
    }
}