package com.example.bz_frontend_new;

public class GameLoop implements Runnable{

    // Separate thread that runs the game
    private Thread gameThread;

    private boolean breakIt;

    // Game Panel
    private GamePanel gamePanel;

    public GameLoop(GamePanel gamePanel) {
        // Initialize and start the game loop with its thread
        gameThread = new Thread(this);

        breakIt = false;

        // Initialize Game Panel
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        // Get framerate
        long lastFPScheck = System.currentTimeMillis();
        int fps = 0;

        // DeltaTime
        long lastDelta = System.nanoTime();
        long nanoSec = 1_000_000_000; // One second
        // Game Loop
        while(true && !breakIt) {

            // Delta time within loop
            long nowDelta = System.nanoTime();
            long timeSincelastDelta = nowDelta - lastDelta;
            double delta = timeSincelastDelta / nanoSec;

            // Updating local game state
            gamePanel.update(delta);
            gamePanel.render();

            // Send client objective information to server
            gamePanel.sendObjectiveData();

            // Send client proj information to server
            gamePanel.sendProjData();

            // Send local client data to server
            gamePanel.sendPlayerData();

            lastDelta = nowDelta;
            fps++;

            // Checking if a second has passed, can print these values to check fps!
            long now = System.currentTimeMillis();
            if (now - lastFPScheck >= 1000) {
                fps = 0;
                lastFPScheck += 1000;
            }
        }

    }

    public void startGameLoop() {
        gameThread.start();
    }

    public void flagBreak() {
        this.breakIt = true;
    }
}
