package com.example.bz_frontend_new;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Joystick {

    private Paint outerCirclePaint;
    private Paint innerCirclePaint;
    private int outerCircleRadius;
    private int innerCircleRadius;
    private int outerCircleCenterPositionX;
    private int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;
    private double centerToTouchDistance;
    private double actuatorX;
    private double actuatorY;
    private boolean isPressed;

    public Joystick(int centerPositionX, int centerPositionY, int outerCircleRadius, int innerCircleRadius) {
        // Centering of joystick
        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPositionX;
        innerCircleCenterPositionY = centerPositionY;

        // Radii of circles
        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;

        // Paint of circles
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(Color.WHITE);
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    // Rendering method
    public void draw(Canvas canvas) {
        // Outer circle drawn first
        canvas.drawCircle(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                outerCircleRadius,
                outerCirclePaint
                );

        // Inner circle drawn second
        canvas.drawCircle(
                innerCircleCenterPositionX,
                innerCircleCenterPositionY,
                innerCircleRadius,
                innerCirclePaint
        );
    }

    // Logic updating method
    public void update() {
        updateInnerCirclePosition();
    }

    private void updateInnerCirclePosition() {
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX*outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
    }

    // Method for when joystick is pressed
    public boolean isPressed(double x, double y) {
        // Calculate distance from center of joystick to position of touch
        centerToTouchDistance = Math.sqrt(
                Math.pow(outerCircleCenterPositionX - x, 2) +
                Math.pow(outerCircleCenterPositionY - y, 2)
        );

        // Returns true if user has pressed inside the radius of the outer circle
        return centerToTouchDistance < outerCircleRadius;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    // Determines how much (range of 0-1) the user is pulling the joystick
    public void setActuator(double x, double y) {
        double deltaX = x - outerCircleCenterPositionX;
        double deltaY = y - outerCircleCenterPositionY;
        double deltaDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

        if(deltaDistance < outerCircleRadius) {
            actuatorX = deltaX/outerCircleRadius;
            actuatorY = deltaY/outerCircleRadius;
        }
        else { // Vector normalizing
            actuatorX = deltaX/deltaDistance;
            actuatorY = deltaY/deltaDistance;
        }
    }

    public void resetActuator() {
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    public double getActuatorX() {
        return actuatorX;
    }

    public double getActuatorY() {
        return actuatorY;
    }
}
