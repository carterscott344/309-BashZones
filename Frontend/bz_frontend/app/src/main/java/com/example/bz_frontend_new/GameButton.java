package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GameButton {
    // Orientation fields
    private int left;
    private int top;
    private int width;
    private int height;

    // Paint to describe rectangle color, can change depending on if the button is pressed
    private Paint rectPaint;

    // Bitmap for image that will be drawn over the button
    private Bitmap image;

    // Options for resizing images
    private BitmapFactory.Options options = new BitmapFactory.Options();

    // Boolean for if the button is pressed
    private boolean isPressed;

    public GameButton(int left, int top, int width, int height, Context context) {
        // Initialize orientation
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;

        // Initialize isPressed
        isPressed = false;

        // Initialize paint
        rectPaint = new Paint();
        rectPaint.setColor(Color.WHITE);
    }

    // Logic updating method
    public void update() {
        if (isPressed) {
            // Set color to gray if button has been pressed for visual feedback
            rectPaint.setColor(Color.GRAY);
        }
        // Otherwise, if the button isn't pressed and the color of the button is gray, make the button white again
        else if (!isPressed && rectPaint.getColor() == Color.GRAY) {
            rectPaint.setColor(Color.WHITE);
        }
    }

    // Rendering method
    public void render(Canvas canvas) {
        canvas.drawRect(left, top, left + width, top + height, rectPaint);
    }

    // Returns if the button is actually pressed or not depending on where the user touches the screen
    public boolean isPressed(double x, double y) {
        // if touch position is within rectangle's borders, return true
        if (x >= left && x <= left + width && y >= top && y <= top + height) {
            return true;
        }
        return false;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getLeft() {
        return left;
    }

    public int getWidth() {
        return width;
    }

    public int getTop() {
        return top;
    }

    public int getHeight() {
        return height;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
