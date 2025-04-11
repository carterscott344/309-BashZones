package com.example.bz_frontend_new;

import android.content.Context;

public class ChatCloseButton extends GameButton{

    // Boolean for determining if this button is active or not
    private boolean isActive;

    public ChatCloseButton(int left, int top, int width, int height, Context context) {
        super(left, top, width, height, context);

        // Initialize button being active to true, because technically the chat is closed
        isActive = true;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
