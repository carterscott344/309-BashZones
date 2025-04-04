package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Player {
    // Constant fields
    private static final double MAX_SPEED = 10;

    // Position, Velocity, Rotation
    private double posX;
    private double posY;
    private double veloX;
    private double veloY;

    // Current image the player is displaying
    private Bitmap image;

    // Options for resizing images
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public Player(Context context, double posX, double posY) {
        // Default position
        this.posX = posX;
        this.posY = posY;

        // Default image for player
        options.inScaled = false;
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_head, options);
        image = Bitmap.createScaledBitmap(image, image.getWidth() * 6, image.getHeight() * 6, false);
    }

    // Updating game logic
    public void update(Joystick leftJoystick) {
        // Player velocity handling
        veloX = leftJoystick.getActuatorX()*MAX_SPEED;
        veloY = leftJoystick.getActuatorY()*MAX_SPEED;

        // Player position handling
        posX += veloX;
        posY += veloY;

        // Player rotation handling
    }

    // Rendering method
    public void render(Canvas canvas) {
        canvas.drawBitmap(image,(float) posX,(float) posY, null);
    }
}
