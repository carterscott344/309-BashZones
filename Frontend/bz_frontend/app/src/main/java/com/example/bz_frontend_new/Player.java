package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Canvas;

public class Player {
    private static final double MAX_SPEED = 200;
    private double posX;
    private double posY;
    private double veloX;
    private double veloY;

    public Player(Context context, double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
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

    }
}
