package com.example.bz_frontend_new;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback, WebSocketListener {
    // Constant fields
    private static final int[] songs = {
            R.raw.arcadia,
            R.raw.callmekatlababy,
            R.raw.pablominibar
    };

    // Scrolling
    private double[] scroll = new double[2];

    // Damage information
    private HashMap<String, Integer> damages = new HashMap<>();

    // Canvas holder
    private SurfaceHolder holder;

    // Player team
    private int playerTeam;

    // Media player for music
    MediaPlayer mediaPlayer;

    // String for weapon that is equipped
    private String equippedType;

    // Projectile information for PushBalls
    PushBall[] pushBalls;

    // Storage for canvas width and height
    private int canvasWidth;
    private int canvasHeight;

    // Shared preferences for PlayerID
    SharedPreferences sp;
    private long localPlayerID;

    // Joystics
    private Joystick leftJoystick;
    private Joystick rightJoystick;

    // FireButton;
    private GameButton fireButton;

    // Chat Button
    private ChatButton chatButton;

    // Chat Window
    private ChatWindow chatWindow;

    // Chat close button
    private ChatCloseButton chatCloseButton;

    // Player (For this client)
    private Player player;

    // Game Loop Class
    private GameLoop gameLoop;

    // Player information hash map
    public HashMap<String, JSONObject> localPlayerStats;

    // Player object hash map
    public HashMap<String, OtherPlayer> localPlayerObjects;

    // boolean if the match is loaded or not
    private boolean matchLoaded;

    // Testing pushball, ignore
    private PushBall tester;

    // Paint for displaying player Health
    private Paint healthpaint;

    // Local clock time
    private int localClockTime;
    private Paint clockPaint;

    // Updating player touch positions
    private double touchX;
    private double touchY;

    public GamePanel(Context context) {
        super(context);
        // Add holder for canvas
        holder = getHolder();
        holder.addCallback(this);

        // Match is not loaded to start
        matchLoaded = false;

        // Init game information
        damages.put("PushBall", 20);

        // Initialize storage of size of canvas
        canvasWidth = 0;
        canvasHeight = 0;

        // Equipped is defaulted to PushBall
        equippedType = "PushBall";

        // Init player's team, this will be switched from match start data
        playerTeam = 0;

        // Init scroll
        scroll[0] = 0;
        scroll[1] = 0;

        // Initialize game objects
        leftJoystick = new Joystick(275, 350, 120, 60);
        rightJoystick = new Joystick(275, 800, 120, 60);
        player = new Player(context, 0, 0);
        chatButton = new ChatButton((canvasWidth - 128) / 2, 50, 128, 128, context);
        fireButton = new GameButton(0, 0, 128, 128, context);
        chatWindow = new ChatWindow((canvasWidth - 1200) / 2, 0, 1200, 720, context);
        chatCloseButton = new ChatCloseButton((canvasWidth + 460) / 2, 50, 128, 128, context);
        chatCloseButton.setPaintColor(Color.RED);

        // Initialize PushBalls array
        pushBalls = new PushBall[10];
        for (int i = 0; i < pushBalls.length; i++) {
            pushBalls[i] = new PushBall(context, 0, 0, 25, player, player.getPlayerHitbox(), playerTeam, playerTeam, true);
        }

        // Initialize Game Loop
        gameLoop = new GameLoop(this);

        // Get player ID
        sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        localPlayerID = sp.getLong("userID", -1);

        // Initialize localPlayerAssets
        localPlayerStats = new HashMap<>();
        localPlayerObjects = new HashMap<>();

        // Initialize localClockTime
        // Clock time
        localClockTime = 0;
        clockPaint = new Paint();
        clockPaint.setColor(Color.WHITE);
        clockPaint.setTextSize(100);
        clockPaint.setTextAlign(Paint.Align.LEFT);

        // Initialize healthPaint
        healthpaint = new Paint();
        healthpaint.setColor(Color.GREEN);
        healthpaint.setTextSize(100);
        healthpaint.setTextAlign(Paint.Align.LEFT);

        // Player touch positions initially set to unreachable val, change every time the player touches the screen
        touchX = Double.POSITIVE_INFINITY;
        touchY = Double.POSITIVE_INFINITY;

        // Connect to websocket
        WebSocketManager.getInstance().setWebSocketListener(this);

        // Tell server that client is ready to play
        JSONObject readyObj = new JSONObject();
        try {
            readyObj.put("type", "playerLoaded");

            // Convert object to string for websocket
            String readyString = readyObj.toString();

            // String to send to server
            WebSocketManager.getInstance().sendMessage(readyString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Testing projectile, ignore
//        tester = new PushBall(context,500,
//                player.getPosY() + 30,
//                35,
//                player,
//                player.getPlayerHitbox(),
//                1,
//                0,
//                false
//                );
//        tester.setIsActive(true);

        // Begin music
        Random random = new Random();
        int randomInd = random.nextInt(songs.length);

        mediaPlayer = MediaPlayer.create(context, songs[randomInd]);
        mediaPlayer.start();
    }

    // Handles game logic
    public void update(double delta) {
        // Updating joysticks
        leftJoystick.update();
        rightJoystick.update();

//        tester.update();

        // Update fire button
        fireButton.update();

        // Update chat button if active, else update chat window, preserves server bandwidth
        if (!chatButton.getIsActive()) {
            chatButton.update();
        }
        else {
            chatWindow.update();
            chatCloseButton.update();
        }

        // Update other players
        if (!localPlayerObjects.isEmpty()) {
            for (OtherPlayer players : localPlayerObjects.values()) {
                players.update(leftJoystick, rightJoystick);
            }
        }
        // Updating local PushBalls
        for (int i = 0; i < pushBalls.length; i++) {
            // Update only active pushballs to preserve memory
            if (pushBalls[i].getIsActive()) {
                pushBalls[i].update();
            }
        }

        // Updating player
        player.update(leftJoystick, rightJoystick);
    }

    // Handles game rendering
    public void render() {
        // Refresh canvas to begin render
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        // Get canvas width and height each frame
        canvasWidth = c.getWidth();
        canvasHeight = c.getHeight();

        scroll[0] = player.getPosX() - canvasWidth / 2f;
        scroll[1] = player.getPosY() - canvasHeight / 2f;

        // Rendering test pushball
//        tester.render(c);

        // Set relative UI element positions if needed, hacky solution
        if (chatButton.getLeft() < 1) {
            leftJoystick.setCenterPos(300, canvasHeight - 300);
            rightJoystick.setCenterPos(canvasWidth - 300, canvasHeight - 300);
            chatButton.setLeft((canvasWidth - chatButton.getWidth()) / 2);
            fireButton.setLeft((int) ((canvasWidth) * .85));
            fireButton.setTop(canvasHeight / 2 - 100);
            chatWindow.setLeft((canvasWidth - chatWindow.getWidth()) / 2);
        }

        // Render other players
        if (!localPlayerObjects.isEmpty()) {
            for (OtherPlayer players : localPlayerObjects.values()) {
                players.render(c, scroll);
            }
        }
        // Drawing PushBalls (PushBalls when rendering check if they are active)
        for (int i = 0; i < pushBalls.length; i++) {
            pushBalls[i].render(c, scroll);
        }

        // Drawing player
        player.render(c, scroll);
//        player.getPlayerHitbox().render(c, scroll);

        // Drawing joysticks
        leftJoystick.draw(c);
        rightJoystick.draw(c);

        // Drawing fire button
        fireButton.render(c);

        // Draw chat button if it isn't active
        if (!chatButton.getIsActive()) {
            chatButton.render(c);
        }
        else {
            chatWindow.render(c);
            chatCloseButton.render(c);
        }

        // Render clock
        c.drawText(String.valueOf(localClockTime), 10, 130, clockPaint);

        // Render health
        c.drawText(String.valueOf(player.getHealth()), 10, 220, healthpaint);

        // Draw canvas
        holder.unlockCanvasAndPost(c);
    }

    // Updates currently held local information with information received from server for users
    public void useServerPlayerInformation(JSONObject playerInfoObj) {
        try {
            // Get array of players from server
            JSONArray players = playerInfoObj.getJSONArray("players");
            for (int i = 0; i < players.length(); i++) {
                JSONObject currentPlayer = players.getJSONObject(i);
                localPlayerStats.put(String.valueOf(currentPlayer.getLong("userID")), currentPlayer);
                // If our information is about a new player, make a new player object to render
                if (localPlayerObjects.get(String.valueOf(currentPlayer.getLong("userID"))) == null) {
                    localPlayerObjects.put(String.valueOf(currentPlayer.getLong("userID")),
                            new OtherPlayer(getContext(),
                            currentPlayer.getDouble("x"),
                                    currentPlayer.getDouble("y"),
                                    this,
                                    currentPlayer.getLong("userID")));
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Updates currently held local information with information received from server for items
    public void useServerItemInformation(JSONObject itemObj) {

    }

    // Handles sending player information to server
    public void sendPlayerData() {
        JSONObject localInfoObj = new JSONObject();
        try {
            // Put player information into object
            localInfoObj.put("type", "playerPosition");
            localInfoObj.put("userID", localPlayerID);
            localInfoObj.put("playerXPosition", player.getPosX());
            localInfoObj.put("playerYPosition", player.getPosY());
            localInfoObj.put("rotationDegrees", player.getRotDegrees());

            // Convert object to string for websocket
            String localInfo = localInfoObj.toString();

            // String of information to send to server
            WebSocketManager.getInstance().sendMessage(localInfo);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void fireWeapon() {
        if (equippedType.equals("PushBall")) {
            for (int i = 0; i < pushBalls.length; i++) {
                // We activate and fire the first available pushBall
                if (!pushBalls[i].getIsActive()) {
                    PushBall pushBall = pushBalls[i];
                    int veloMagnitude = PushBall.getSpeedMagnitude();

                    // Set position and velocity based on where the player is facing
                    pushBall.setPosX(player.getPosX());
                    pushBall.setPosY(player.getPosY());
                    pushBall.setVeloX(veloMagnitude * Math.cos(Math.toRadians(player.getRotDegrees() + 90)));
                    pushBall.setVeloY(veloMagnitude * Math.sin(Math.toRadians(player.getRotDegrees() + 90)));

                    // Set PushBall to active state so it begins moving and rendering
                    pushBall.setIsActive(true);

                    // Break loop because we found our PushBall
                    break;
                }
            }
        }
    }

    // Handles screen touches
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touch event actions
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Set touchX and touchY
                touchX = event.getX();
                touchY = event.getY();

                // Chat button handling
                chatWindow.sendMessage(touchX, touchY);

                // Left joystick handling
                if(leftJoystick.isPressed(event.getX(), event.getY())) {
                    leftJoystick.setIsPressed(true);
                }
                // Right joystick handling
                if(rightJoystick.isPressed(event.getX(), event.getY())) {
                    rightJoystick.setIsPressed(true);
                }
                // Chat button handling
                if(chatButton.isPressed(event.getX(), event.getY())) {
                    chatButton.setIsPressed(true);
                }
                // Chat close button handling
                if(chatCloseButton.isPressed(event.getX(), event.getY())) {
                    chatCloseButton.setIsPressed(true);
                }
                // Fire button handling
                if(fireButton.isPressed(event.getX(), event.getY())) {
                    fireButton.setIsPressed(true);
                    fireWeapon();
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
                // Chat button handling
                if (chatButton.getIsPressed()) {
                    chatButton.setIsActive(true);
                }
                chatButton.setIsPressed(false);
                // Chat close button handling
                if (chatCloseButton.getIsPressed() && chatButton.getIsActive()) {
                    chatButton.setIsActive(false);
                }
                chatCloseButton.setIsPressed(false);
                // Can send chat now that user is no longer pressing screen
                chatWindow.setCanSendChat(true);
                // Fire button handling
                if (fireButton.getIsPressed()) {
                    fireButton.setIsPressed(false);
                }
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
            System.out.println(messageObj.getString("type"));
            // If information is about a user
            if (messageObj.getString("type").equals("allPlayerPositions")) {
                useServerPlayerInformation(messageObj);
            }
            // If the message is to update an item
            else if (messageObj.getString("type").equals("serverItemInformation")) {

            }
            // If the message is a chat
            else if (messageObj.getString("type").equals("chat")) {
                // Get the new message and post it to the chat
                String toPost = messageObj.getString("senderUsername") + ": " + messageObj.getString("message");
                chatWindow.updateChats(toPost);
            }
            // If the message is a timer update
            else if (messageObj.getString("type").equals("clock")) {
                localClockTime = messageObj.getInt("timeRemaining");
            }
            // If the message is to remove a player
            else if (messageObj.getString("type").equals("removePlayer")) {

            }
            // If the message is to start the match
            else if (messageObj.getString("type").equals("loadedGame")) {
                System.out.println("Loaded Game!");
                // First, create default stored information
                useServerPlayerInformation(messageObj);

                int teamInt = messageObj.getInt("team");

                // Set player projectiles based on team
                for (int i = 0; i < pushBalls.length; i++) {
                    pushBalls[i].setTeam(teamInt);
                }

                // Allow game to start updating and rendering objects
                matchLoaded = true;
            }
            // If the message is to end the match
            else if (messageObj.getString("type").equals("endMatch")) {
                // Terminates Web Socket connection and returns player to general
                WebSocketManager.getInstance().disconnectWebSocket();
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

    public long getLocalPlayerID() {
        return localPlayerID;
    }
}
