package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Basic Superclass for any types of items to extend from.
 *
 * Make sure in any Subclass to change the image in its constructor,
 * defaults to player head sprite.
 */
public class GameItem {
    // Positional Fields
    private double posX;
    private double posY;
    private double veloX;
    private double veloY;
    private int rotDegrees;

    // Sprite
    private Bitmap image;

    // Options for resizing image
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public GameItem(Context context, double posX, double posY) {
        // Default position and rotation
        this.posX = posX;
        this.posY = posY;

        // Image will be set later, defaults to Player Head image for now
        options.inScaled = false;
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_head, options);
        image = Bitmap.createScaledBitmap(image, image.getWidth() * 6, image.getHeight() * 6, false);
    }

    // Method for logic updating
    public void update() {
        posX += veloX;
        posY += veloY;
    }

    // Method for rendering
    public void render(Canvas canvas) {
        Matrix transform = new Matrix();
        transform.setTranslate((float) (posX + image.getWidth() / 2), (float) (posY + image.getHeight() / 2));
        transform.preRotate(rotDegrees, image.getWidth()/2, image.getHeight()/2);
        canvas.drawBitmap(image, transform, null);
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getVeloX() {
        return veloX;
    }

    public double getVeloY() {
        return veloY;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public void setVeloX(double veloX) {
        this.veloX = veloX;
    }

    public void setVeloY(double veloY) {
        this.veloY = veloY;
    }

    public void setRotDegrees(int rotDegrees) {
        this.rotDegrees = rotDegrees;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
