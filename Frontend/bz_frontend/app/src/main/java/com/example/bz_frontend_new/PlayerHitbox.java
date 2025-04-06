package com.example.bz_frontend_new;

import android.graphics.Color;
import android.graphics.Paint;

// Players are circular sprites, so we'll make their hitboxes circles
public class PlayerHitbox {
    // Circle Values
    private int posX;
    private int posY;
    private int radius;
    private Paint hitboxPaint;

    public PlayerHitbox(int posX, int posY, int radius) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        hitboxPaint = new Paint();
        hitboxPaint.setColor(Color.WHITE);
    }

    // Updating logic method
    public void update() {

    }

    // Rendering method
    public void render() {

    }

    // Collision check

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
