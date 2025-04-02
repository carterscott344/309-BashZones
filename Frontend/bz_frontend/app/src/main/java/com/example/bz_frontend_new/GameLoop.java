package com.example.bz_frontend_new;

public class GameLoop implements Runnable{

    // Separate thread that runs the game
    private Thread gameThread;

    // Game Panel
    private GamePanel gamePanel;

    public GameLoop(GamePanel gamePanel) {
        // Initialize and start the game loop with its thread
        gameThread = new Thread(this);

        // Initialize Game Panel
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {

        // Game Loop
        while(true) {
            gamePanel.update();
            gamePanel.render();
        }

    }

    public void startGameLoop() {
        gameThread.start();
    }
}
