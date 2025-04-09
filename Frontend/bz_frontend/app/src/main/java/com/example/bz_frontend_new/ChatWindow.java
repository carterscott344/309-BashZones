package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ChatWindow {

    // Orientation fields
    int left;
    int top;
    int width;
    int height;

    // Paint for chat window
    private Paint chatWindowPaint;

    // Chat close button
    private ChatCloseButton chatCloseButton;

    public ChatWindow(int left, int top, int width, int height, Context context) {
        // Initialize orientation
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;

        // Initialize paint
        chatWindowPaint = new Paint();
        chatWindowPaint.setColor(Color.DKGRAY);
    }

    // Logic updating method
    public void update() {

    }

    // Rendering method
    public void render(Canvas canvas) {
        // Render window
        canvas.drawRect(left, top, left + width, top + height, chatWindowPaint);

        // Render exit button

        // Render edit text

        // Render send button
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }
}
