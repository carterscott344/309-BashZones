package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

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

    // Health
    private int health;

    // Current image the player is displaying
    private Bitmap image;

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
        image = Bitmap.createScaledBitmap(image, image.getWidth() * 6, image.getHeight() * 6, false);

        // Init hitbox
        playerHitbox = new PlayerHitbox((int) posX, (int) posY, 74);

        // Default health for player
        health = 100;
    }

    // Updating game logic
    public void update(Joystick leftJoystick, Joystick rightJoystick) {
        // Player velocity handling
        veloX = leftJoystick.getActuatorX()*MAX_SPEED;
        veloY = leftJoystick.getActuatorY()*MAX_SPEED;

        // Player position handling
        posX += veloX;
        posY += veloY;

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
    public void render(Canvas canvas) {
        Matrix transform = new Matrix();
        transform.setTranslate((float) (posX + image.getWidth() / 2), (float) (posY + image.getHeight() / 2));
        transform.preRotate(rotDegrees, image.getWidth()/2, image.getHeight()/2);
        canvas.drawBitmap(image, transform, null);
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
}
