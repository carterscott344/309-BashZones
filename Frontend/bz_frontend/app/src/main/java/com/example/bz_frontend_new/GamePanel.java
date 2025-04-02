package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    // Canvas holder
    private SurfaceHolder holder;

    // Game Loop Class
    private GameLoop gameLoop;

    public GamePanel(Context context) {
        super(context);
        // Add holder for canvas
        holder = getHolder();
        holder.addCallback(this);

        // Initialize Game Loop
        gameLoop = new GameLoop(this);
    }

    // Handles game logic
    public void update() {

    }

    // Handles game rendering
    public void render() {
        // Refresh canvas to begin render
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        // Draw canvas
        holder.unlockCanvasAndPost(c);
    }

    // Handles screen touches
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Screen has been tapped
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

        }

        // Get coordinates of touch
        float touchX = event.getX();
        float touchY = event.getY();

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
