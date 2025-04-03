package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.bz_frontend_new.entities.GameCharacters;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    // Canvas holder
    private SurfaceHolder holder;

    // Joystics
    private Joystick leftJoystick;

    // Game Loop Class
    private GameLoop gameLoop;

    public GamePanel(Context context) {
        super(context);
        // Add holder for canvas
        holder = getHolder();
        holder.addCallback(this);

        // Initialize game objects
        leftJoystick = new Joystick(275, 350, 100, 50);

        // Initialize Game Loop
        gameLoop = new GameLoop(this);
    }

    // Handles game logic
    public void update(double delta) {
        // Updating joysticks
        leftJoystick.update();
    }

    // Handles game rendering
    public void render() {
        // Refresh canvas to begin render
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        // Drawing player
        c.drawBitmap(GameCharacters.PLAYER.getSpriteSheet(true), 500, 500, null);

        // Drawing joysticks
        leftJoystick.draw(c);

        // Draw canvas
        holder.unlockCanvasAndPost(c);
    }

    // Handles screen touches
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touch event actions
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Left joystick handling
                if(leftJoystick.isPressed(event.getX(), event.getY())) {
                    leftJoystick.setIsPressed(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                // Left joystick handling
                if(leftJoystick.getIsPressed()) {
                    // Only set actuator if player is MOVING the joystick
                    leftJoystick.setActuator(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                // Left joystick handling
                leftJoystick.setIsPressed(false);
                leftJoystick.resetActuator();
                return true;
        }

        // Event has been handled
        return true;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        gameLoop.startGameLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}
