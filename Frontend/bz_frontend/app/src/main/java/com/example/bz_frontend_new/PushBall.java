package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * A "PushBall" is the default projectile in this game.
 * For the course of this semester, it will be the only projectile
 * in the game.
 * When it hits an enemy player, it will perform 20 damage
 * and push the respective player backwards
 */
public class PushBall extends Projectile{
    // Constant fields
    private static final int LIFESPAN = 5;
    private static final int SPEED_MAGNITUDE = 20;

    // Flag for this PushBall's color
    private int team;

    // Boolean for if PushBall is active and should be rendered on screen
    private boolean isActive;

    // Paint for the color of the PushBall
    private Paint ballPaint;

    // Radius for ball itself
    private int radius;

    public PushBall(Context context, double posX, double posY, int radius, int team) {
        super(context, posX, posY, radius);
        super.setTypeOfProj("PushBall");
        this.radius = radius;

        // Init paint
        this.team = team;
        ballPaint = new Paint();
        ballPaint.setColor(Color.GRAY); // Gray by default/error

        // Init color of paint based on team, 0 = red, 1 = blue
        if (team == 0) {
            ballPaint.setColor(Color.RED);
        }
        else if (team == 1) {
            ballPaint.setColor(Color.BLUE);
        }

        // Always is inactive to start by default
        isActive = false;
    }

    // Updating method
    @Override
    public void update() {
        super.update();
    }

    // Rendering method, based on circle instead of sprite
    @Override
    public void render(Canvas canvas) {
        if (isActive) {
            canvas.drawCircle((int) super.getPosX(), (int) super.getPosY(), radius, ballPaint);
        }
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public int getTeam() {
        return team;
    }

    public static int getSpeedMagnitude() {
        return SPEED_MAGNITUDE;
    }
}
