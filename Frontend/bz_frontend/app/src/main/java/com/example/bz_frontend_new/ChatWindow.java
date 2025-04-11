package com.example.bz_frontend_new;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatWindow {

    // Shared preferences
    SharedPreferences sp;

    // Orientation fields
    int left;
    int top;
    int width;
    int height;

    // Four chat text buttons set according to user's game settings
    ChatTextButton topLeft;
    ChatTextButton topRight;
    ChatTextButton bottomLeft;
    ChatTextButton bottomRight;

    // Paint for chat window
    private Paint chatWindowPaint;

    // Boolean to determine if we can send another chat or not
    private boolean canSendChat;

    // Array list for displaying chats
    ArrayList<String> chats;
    private Paint chatPaint;

    public ChatWindow(int left, int top, int width, int height, Context context) {
        // Shared preferences
        sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        // Initialize orientation
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;

        // Initialize paint
        chatWindowPaint = new Paint();
        chatWindowPaint.setColor(Color.DKGRAY);

        // Initialize chat buttons
        topLeft = new ChatTextButton(0, 0, 400, 100, context);
        topRight = new ChatTextButton(0, 0, 400, 100, context);
        bottomLeft = new ChatTextButton(0, 0, 400, 100, context);
        bottomRight = new ChatTextButton(0, 0, 400, 100, context);

        // TODO: REMOVE HARDCODED TEXT, SET THESE ACCORDING TO USER SETTINGS
        // Initialize chat button text
        topLeft.setMyText("Great Game!");
        bottomLeft.setMyText("Wow!");
        topRight.setMyText("Go Ahead!");

        // Can send chat by default
        canSendChat = true;

        // Initialize Chats
        chats = new ArrayList<>();
        chatPaint = new Paint();
        chatPaint.setColor(Color.WHITE);
        chatPaint.setTextAlign(Paint.Align.LEFT);
        chatPaint.setTextSize(60);

        // Testing messages for chats
        chats.add("This is message 1");
        chats.add("This is message 2");
        chats.add("This is message 3");
        chats.add("This is message 4");
        chats.add("This is message 5");
        chats.add("This is message 6");
        chats.add("This is message 7");
        chats.add("This is message 8");
    }

    // Logic updating method
    public void update() {
        // Set chat button positions
        topLeft.setLeft(left + 20);
        topLeft.setTop(top + height - 240);
        bottomLeft.setLeft(left + 20);
        bottomLeft.setTop(top + height - 120);
        topRight.setLeft(left + width - topRight.getWidth() - 20);
        topRight.setTop(top + height - 240);
        bottomRight.setLeft(left + width - bottomRight.getWidth() - 20);
        bottomRight.setTop(top + height - 120);
    }

    // Rendering method
    public void render(Canvas canvas) {
        // Render window
        canvas.drawRect(left, top, left + width, top + height, chatWindowPaint);

        // Render text options
        topLeft.render(canvas);
        bottomLeft.render(canvas);
        topRight.render(canvas);
        bottomRight.render(canvas);

        // Render chat messages
        int currentY = topLeft.getTop() - 80;
        for (int i = 0; i < chats.size(); i++) {
            // Newest message has an anchored position
            if (i == 0) {
                canvas.drawText(chats.get(i), topLeft.getLeft(), currentY, chatPaint);
            }
            else {
                currentY -= 80;
                canvas.drawText(chats.get(i), topLeft.getLeft(), currentY, chatPaint);
            }
        }
    }

    // Checks if message can be sent and sends respective message to websocket
    public void sendMessage(double x, double y) {
        JSONObject couldSend = new JSONObject();
        try {
            couldSend.put("type", "chat");
            couldSend.put("matchID", sp.getString("currentMatchID", null));
            couldSend.put("senderID", sp.getLong("userID", -1));
            couldSend.put("scope", "all");

            if (topLeft.isPressed(x, y)) {
                couldSend.put("message", topLeft.getMyText());
                String payload = couldSend.toString();
                WebSocketManager.getInstance().sendMessage(payload);
            }
            else if (topRight.isPressed(x, y)) {
                couldSend.put("message", topRight.getMyText());
                String payload = couldSend.toString();
                WebSocketManager.getInstance().sendMessage(payload);
            }
            else if (bottomLeft.isPressed(x, y)) {
                couldSend.put("message", bottomLeft.getMyText());
                String payload = couldSend.toString();
                WebSocketManager.getInstance().sendMessage(payload);
            }
            else if (bottomRight.isPressed(x, y)) {
                couldSend.put("message", bottomRight.getMyText());
                String payload = couldSend.toString();
                WebSocketManager.getInstance().sendMessage(payload);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Updates chats that will be displayed
    public void updateChats(String newMessage) {
        // If chats size is too large, remove the last element
        if (chats.size() > 20) {
            chats.remove(chats.size() - 1);
        }

        // Add new message to chat
        chats.add(0, newMessage);
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }

    public void setCanSendChat(boolean canSendChat) {
        this.canSendChat = canSendChat;
    }

    public boolean getCanSendChat() {
        return canSendChat;
    }
}
