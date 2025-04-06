package com.zonezone.backend.gameHandler;

import org.glassfish.tyrus.server.Server;

import java.util.Scanner;

public class GameServerLauncher {

    public static void main(String[] args) {
        Server server = new Server("0.0.0.0", 80, "/ws", null, GameSocketServer.class);

        try {
            server.start();
            System.out.println("✅ ZoneZone Game Server running at ws://coms-3090-046.class.las.iastate.edu:80/ws/connectToServer\n");

            // Keep it alive indefinitely
            Thread.currentThread().join();
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