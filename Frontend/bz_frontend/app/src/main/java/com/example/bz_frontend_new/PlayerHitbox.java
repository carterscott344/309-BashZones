package com.example.bz_frontend_new;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

// Players are circular sprites, so we'll make their hitboxes circles
public class PlayerHitbox {
    // Circle Values
    private int posX;
    private int posY;
    private int radius;
    private Paint hitboxPaint;

    // Boolean determining if it is in contact with something
    private boolean isHit;

    public PlayerHitbox(int posX, int posY, int radius, boolean needsScaling) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        hitboxPaint = new Paint();
        hitboxPaint.setColor(Color.WHITE);
        isHit = false;
    }

    // Updating logic method
    public void update(int newPosX, int newPosY) {
        posX = newPosX;
        posY = newPosY;

        if (isHit) {
            hitboxPaint.setColor(Color.GREEN);
        }
        else {
            hitboxPaint.setColor(Color.WHITE);
        }
    }

    // Rendering method (OPTIONAL)
    public void render(Canvas canvas, double[] scroll) {
        canvas.drawCircle((int) (posX - scroll[0]), (int) (posY - scroll[1]), radius, hitboxPaint);
    }

    // Method for hit detection
    public boolean isHit(PlayerHitbox other) {

        int x = other.getPosX();
        int y = other.getPosY();

        // Calculate distance from center of hitbox to center of other hitbox
        int centerToHitboxCenterDistance = (int) Math.sqrt(
                Math.pow(posX - x, 2) + Math.pow(posY - y, 2)
        );

        Boolean hit = centerToHitboxCenterDistance <= radius + other.getRadius();

        // Set other hitbox to hit if it has been hit
        if (hit) {other.setIsHit(true);}

        // Returns true if hitbox circles overlap
        return hit;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setIsHit(boolean hit) {
        isHit = hit;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getRadius() {
        return radius;
    }
}
