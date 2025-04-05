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
    private int rotDegrees;

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
        rotDegrees = rotDegrees % 360;
    }

    // Rendering method
    public void render(Canvas canvas) {
        // Save canvas before rotating for player rotation
        canvas.save();

        // Rotate canvas according to player rotation
        canvas.rotate(rotDegrees,(float) posX + image.getWidth() / 2,(float) posY + image.getHeight() / 2);

        // Render player
        canvas.drawBitmap(image,(float) posX + image.getWidth() / 2,(float) posY + image.getHeight() / 2, null);

        // Restore canvas to position before render
        canvas.restore();
    }
}
