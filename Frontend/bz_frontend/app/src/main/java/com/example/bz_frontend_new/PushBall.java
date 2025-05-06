package com.example.bz_frontend_new;

import android.content.Context;
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

    // Flag for this PushBall's color
    private int team;

    // Flag for if this PushBall is shot by friendly team/enemy team
    private boolean isSafe;

    // Paint for the color of the PushBall
    private Paint ballPaint;

    // Radius for ball itself
    private int radius;

    public PushBall(Context context, double posX, double posY, int radius, boolean isSafe, int team) {
        super(context, posX, posY, radius);
        this.isSafe = isSafe;
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
    }

    // Updating method
    @Override
    public void update() {
        super.update();
    }

    // Rendering method, based on circle instead of sprite
    @Override
    public void render(Canvas canvas) {
        canvas.drawCircle((int) super.getPosX(), (int) super.getPosY(), radius, ballPaint);
    }
}
