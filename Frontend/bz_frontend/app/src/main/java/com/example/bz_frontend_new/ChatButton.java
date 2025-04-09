package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ChatButton extends GameButton {

    // Boolean for if the button is no longer pressed and is now active (chat window open)
    private boolean isActive;

    public ChatButton(int left, int top, int width, int height, Context context) {
        super(left, top, width, height, context);

        // Initialize isActive
        isActive = false;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
