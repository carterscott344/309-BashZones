package com.example.bz_frontend_new;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

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
        sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
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

    // Updates currently held local information with information received from server for users
    public void useServerPlayerInformation(JSONObject playerObj) {

    }

    // Updates currently held local information with information received from server for items
    public void useServerItemInformation(JSONObject itemObj) {

    }

    // Handles sending player information to server
    public void sendPlayerData() {
        JSONObject localInfoObj = new JSONObject();
        try {
            // Put player information into object
            localInfoObj.put("type", "ClientUserInfo");
            localInfoObj.put("playerID", localPlayerID);
            localInfoObj.put("posX", player.getPosX());
            localInfoObj.put("posY", player.getPosY());
            localInfoObj.put("rotDegrees", player.getRotDegrees());

            // Convert object to string for websocket
            String localInfo = localInfoObj.toString();

            // String of information to send to server
            WebSocketManager.getInstance().sendMessage(localInfo);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    // Returns user to general page
    public void returnToGeneral() {
        Intent i = new Intent(getContext(), GeneralPage.class);
        getContext().startActivity(i);
        ((Activity)getContext()).finish();
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
        try {
            JSONObject messageObj = new JSONObject(message);
            // If information is about a user
            if (messageObj.getString("type").equals("ServerUserInformation")) {
                useServerPlayerInformation(messageObj);
            }
            // If the message is to update an item
            else if (messageObj.getString("type").equals("ServerItemInformation")) {

            }
            // If the message is to end the match
            else if (messageObj.getString("type").equals("EndMatch")) {

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // For now, when connection is closed, return to general page
    // TODO: Create end of game functionality depending on reason for websocket close
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        returnToGeneral();
    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}
