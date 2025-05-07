package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Stack;

public class Player {
    // Constant fields
    private static final double MAX_SPEED = 10;

    // Position, Velocity, Rotation
    private double posX;
    private double posY;
    private double veloX;
    private double veloY;
    private int rotDegrees;

    // Hitbox
    private PlayerHitbox playerHitbox;

    // Stack for what projectiles the player is colliding with on a given frame
    private Stack<PushBall> hitByProj;

    // Health
    private int health;

    // Current image the player is displaying
    private Bitmap image;

    // Collision for maps
    private boolean collided;

    // Image for the current hat the player is displaying
    private Bitmap hatImg;

    // Text for current tag player is displaying
    private String tagText;

    // Rect to represent player's map hitbox
    private Rect mapHitbox;

    // Options for resizing images
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public Player(Context context, double posX, double posY) {
        // Default position and rotation
        this.posX = posX;
        this.posY = posY;
        rotDegrees = 0;

        // Default image for player
        options.inScaled = false;
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_head, options);
//        image = Bitmap.createScaledBitmap(image, image.getWidth() * 6, image.getHeight() * 6, false);

        // Init hitbox
        playerHitbox = new PlayerHitbox((int) posX, (int) posY, 72, true);

        // Default empty stack
        hitByProj = new Stack<>();

        collided = false;

        // Default health for player
        health = 100;

        // Init map hitbox
        mapHitbox = new Rect((int)(posX - 76), (int)(posY - 76), (int)(posX + 76), (int)(posY + 76));
    }

    // Updating game logic
    public void update(Joystick leftJoystick, Joystick rightJoystick, ArrayList<Rect> map) {
        // Player velocity handling
        veloX = leftJoystick.getActuatorX()*MAX_SPEED;
        veloY = leftJoystick.getActuatorY()*MAX_SPEED;

        // Player position handling
        posX += veloX;

        collided = false;

        mapHitbox = new Rect((int)(posX - 76), (int)(posY - 76), (int)(posX + 76), (int)(posY + 76));
        for (Rect rect : map) {
            if (rectangle_collision(
                    mapHitbox.left,
                    mapHitbox.top,
                    mapHitbox.width(),
                    mapHitbox.height(),
                    rect.left,
                    rect.top,
                    rect.width(),
                    rect.height()
            )) {
                collided = true;
                if (veloX < 0) { // Move player rect right
                    setPosX(rect.right + 78);
                }
                if (veloX > 0) { // Move player rect left
                    setPosX(rect.left - 78);
                }
            }
        }

        posY += veloY;

        // Now Y
        mapHitbox = new Rect((int)(posX - 76), (int)(posY - 76), (int)(posX + 76), (int)(posY + 76));
        for (Rect rect : map) {
            if (rectangle_collision(
                    mapHitbox.left,
                    mapHitbox.top,
                    mapHitbox.width(),
                    mapHitbox.height(),
                    rect.left,
                    rect.top,
                    rect.width(),
                    rect.height())) {
                if (veloY > 0) { // Move player rect up
                    setPosY(rect.top - 78);
                }
                if (veloY < 0) { // Move player rect down
                    setPosY(rect.bottom + 78);
                }
            }
        }

        // Projectile collision stack handling
        // This way, hits can be handled without checking hitboxes again, because we already
        // know the player has been hit
        while (!hitByProj.isEmpty()) {
            PushBall current = hitByProj.pop();
            // We only want to do something if the team isn't the player's team
            if (current.getPlayerTeam() != current.getTeam()) {
                setHealthAddition(-20);
            }
        }

        // Hitbox position handling
        playerHitbox.update((int) posX, (int) posY);

        // Player rotation handling, only changes rotation if joystick is being pressed
        if (rightJoystick.getIsPressed()) {
            double rotRadians = Math.atan2(rightJoystick.getActuatorY(), rightJoystick.getActuatorX());
            rotDegrees = (int) (rotRadians * (180 / Math.PI));
            rotDegrees -= 90;
        }
    }

    // Rendering method
    public void render(Canvas canvas, double[] scroll) {
        Matrix transform = new Matrix();
        transform.preTranslate((float) ((posX - image.getWidth() / 2f) - scroll[0]), (float) ((posY - image.getWidth() / 2f) - scroll[1]));
        transform.preScale(6, 6, (float) (image.getWidth() / 2f), (float) (image.getHeight() / 2f));
        transform.preRotate(rotDegrees, image.getWidth()/2, image.getHeight()/2);
        canvas.drawBitmap(image, transform, null);

        // Test, ignore
//        Paint test = new Paint();
//        if (collided) {test.setColor(Color.GREEN);}
//        else {test.setColor(Color.WHITE);}
//        canvas.drawRect((int) (mapHitbox.left - scroll[0]), (int) (mapHitbox.top - scroll[1]), (int) (mapHitbox.right - scroll[0]), (int) (mapHitbox.bottom - scroll[1]), test);
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public int getRotDegrees() {
        return rotDegrees;
    }

    public Bitmap getImage() {
        return image;
    }

    public PlayerHitbox getPlayerHitbox() {
        return playerHitbox;
    }

    // Sets health by adding argument health to player's health
    public void setHealthAddition(int addedHealth) {
        this.health += addedHealth;
    }

    public void addToPushBallStack(PushBall toAdd) {
        hitByProj.add(toAdd);
    }

    public int getHealth() {
        return health;
    }

    public Rect getMapHitbox() {
        return mapHitbox;
    }

    public void setPosX(double posX) {
        this.posX = posX;
        mapHitbox.set((int)(posX - 76), (int)(posY - 76), (int)(posX + 76), (int)(posY + 76));
    }

    public void setPosY(double posY) {
        this.posY = posY;
        mapHitbox.set((int)(posX - 76), (int)(posY - 76), (int)(posX + 76), (int)(posY + 76));
    }

    public double getVeloY() {
        return veloY;
    }

    public double getVeloX() {
        return veloX;
    }

    boolean rectangle_collision(float x_1, float y_1, float width_1, float height_1, float x_2, float y_2, float width_2, float height_2)
    {
        return !(x_1 > x_2+width_2 || x_1+width_1 < x_2 || y_1 > y_2+height_2 || y_1+height_1 < y_2);
    }

    // Resets player's health and teleports them to given spawn position
    public void respawn(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        health = 100;
    }
}
