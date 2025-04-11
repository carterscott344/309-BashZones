package com.example.bz_frontend_new;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ChatTextButton extends GameButton{

    // Text that this button will display and send
    private String myText;

    // Paint for text
    private Paint textPaint;

    public ChatTextButton(int left, int top, int width, int height, Context context) {
        super(left, top, width, height, context);

        // Initialize button's text to standard greeting
        myText = "Hello!";

        // Initialize paint
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(70);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        // Draw this button's text on top of button's rect
        canvas.drawText(myText, getLeft() + (getWidth() / 2), getTop() + getHeight() - 15, textPaint);
    }

    // So the chat window knows what text to send to websocket
    public String getMyText() {
        return myText;
    }

    // Change this text button's text
    public void setMyText(String myText) {
        this.myText = myText;
    }
}
