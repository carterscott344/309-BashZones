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
    private static final int LIFESPAN = 1;
    private static final int SPEED_MAGNITUDE = 20;

    // Flag for this PushBall's color
    private int team;

    // Boolean for if PushBall is active and should be rendered on screen
    private boolean isActive;

    // Paint for the color of the PushBall
    private Paint ballPaint;

    // Radius for ball itself
    private int radius;

    // Start time of PushBall's current life cycle
    private long startTime;

    // Boolean for if PushBall was shot by local player
    private boolean local;

    private int playerTeam;

    private Player localPlayerObj;

    public PushBall(Context context, double posX, double posY, int radius, Player localPlayerObj, PlayerHitbox localPlayer, int team, int playerTeam, boolean local) {
        super(context, posX, posY, radius, localPlayer);
        super.setTypeOfProj("PushBall");
        this.radius = radius;

        this.local = local;

        this.playerTeam = playerTeam;

        this.localPlayerObj = localPlayerObj;

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

        // Set active time to -1
        startTime = -1;

        this.local = local;
    }

    // Updating method
    @Override
    public void update() {
        super.update();
        long now = System.currentTimeMillis();

        // Check if PushBall has been hit by local player and not fired by local player
        if (super.isProjectileHit() && isActive && !local) {
            localPlayerObj.addToPushBallStack(this);
            isActive = false;
            super.setProjectileHitFalse();
        }

        // Check if PushBall has existed past its current lifespan and is fired by player
        if (startTime > 0 && ((now - startTime) / 1000) > LIFESPAN && local) {
            isActive = false;
        }
    }

    // Rendering method, based on circle instead of sprite
    @Override
    public void render(Canvas canvas, double [] scroll) {
        if (isActive) {
            canvas.drawCircle((int) (super.getPosX() - scroll[0]), (int) (super.getPosY() - scroll[1]), radius, ballPaint);
//            projHitbox.render(canvas);
        }
    }

    public void setIsActive(boolean active) {
        isActive = active;
        if (isActive) {
            startTime = System.currentTimeMillis();
        }
        else {
            startTime = -1;
        }
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

    public void setTeam(int team) {
        this.team = team;
    }

    public int getPlayerTeam() {
        return playerTeam;
    }
}
