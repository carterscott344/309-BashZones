package com.example.bz_frontend_new;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.java_websocket.handshake.ServerHandshake;

import androidx.annotation.NonNull;

import okhttp3.WebSocket;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback, WebSocketListener {

    // Canvas holder
    private SurfaceHolder holder;

    // Shared preferences for PlayerID
    SharedPreferences sp;
    private long localPlayerID;

    // Joystics
    private Joystick leftJoystick;
    private Joystick rightJoystick;

    // Player (For this client)
    private Player player;

    // Game Loop Class
    private GameLoop gameLoop;

    public GamePanel(Context context) {
        super(context);
        // Add holder for canvas
        holder = getHolder();
        holder.addCallback(this);

        // Initialize game objects
        leftJoystick = new Joystick(275, 350, 100, 50);
        rightJoystick = new Joystick(275, 800, 100, 50);
        player = new Player(context, 500, 200);

        // Initialize Game Loop
        gameLoop = new GameLoop(this);

        // Get player ID
        localPlayerID = sp.getLong("userID", -1);

        // Connect to websocket
        WebSocketManager.getInstance().setWebSocketListener(this);
    }

    // Handles game logic
    public void update(double delta) {
        // Updating joysticks
        leftJoystick.update();
        rightJoystick.update();

        // Updating player
        player.update(leftJoystick, rightJoystick);
    }

    // Handles game rendering
    public void render() {
        // Refresh canvas to begin render
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        // Drawing player
        player.render(c);

        // Drawing joysticks
        leftJoystick.draw(c);
        rightJoystick.draw(c);

        // Draw canvas
        holder.unlockCanvasAndPost(c);
    }

    // Updates currently held local information with information received from server
    public void getServerInformation(String message) {

    }

    // Handles sending player information to server
    public void sendPlayerData() {
        // String of information to send to server
        String localInfo =
                "ClientPlayerInformation" + "," +
                String.valueOf(localPlayerID) + "," +
                String.valueOf(player.getPosX()) + "," +
                String.valueOf(player.getPosY());
        WebSocketManager.getInstance().sendMessage(localInfo);
    }

    // Handles screen touches
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touch event actions
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Left joystick handling
                if(leftJoystick.isPressed(event.getX(), event.getY())) {
                    leftJoystick.setIsPressed(true);
                }
                // Right joystick handling
                if(rightJoystick.isPressed(event.getX(), event.getY())) {
                    rightJoystick.setIsPressed(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                // Left joystick handling
                if(leftJoystick.getIsPressed()) {
                    // Only set actuator if player is MOVING the joystick
                    leftJoystick.setActuator(event.getX(), event.getY());
                }
                // Right joystick handling
                if(rightJoystick.getIsPressed()) {
                    // Only set actuator if player is MOVING the joystick
                    rightJoystick.setActuator(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                // Left joystick handling
                leftJoystick.setIsPressed(false);
                leftJoystick.resetActuator();
                // Right joystick handling
                rightJoystick.setIsPressed(false);
                rightJoystick.resetActuator();
                return true;
        }

        // Event has been handled
        return true;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        gameLoop.startGameLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onWebSocketMessage(String message) {
        // Send information received to update local information
        getServerInformation(message);
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}
