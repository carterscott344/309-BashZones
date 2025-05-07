package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import org.json.JSONException;
import org.json.JSONObject;

public class OtherPlayer extends Player {

    private double posX, posY;
    private int rotDegrees;
    private long localID;
    private Bitmap image;

    // Options for resizing images
    private BitmapFactory.Options options = new BitmapFactory.Options();

    private GamePanel ourPanel;

    public OtherPlayer(Context context, double posX, double posY, GamePanel panel, long localID) {
        super(context, posX, posY);
        this.posX = posX;
        this.posY = posY;
        this.rotDegrees = 0;
        this.localID = localID;

        // Default image for player
        options.inScaled = false;
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_head, options);
        image = Bitmap.createScaledBitmap(image, image.getWidth() * 6, image.getHeight() * 6, false);

        // Initialize Game Panel
        ourPanel = panel;
    }

    // Update pulls from server's stored information
    @Override
    public void update(Joystick leftJoystick, Joystick rightJoystick) {
        // If we have player information stored then update coordinates and rotation
        if (ourPanel.localPlayerStats.get(String.valueOf(localID)) != null) {
            // Obtain the object related to this player
            JSONObject playerAsset = ourPanel.localPlayerStats.get(String.valueOf(localID));
            try {
                posX = playerAsset.getDouble("x");
                posY = playerAsset.getDouble("y");
                rotDegrees = playerAsset.getInt("rotation");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void render(Canvas canvas, double[] scroll) {
        Matrix transform = new Matrix();
        transform.preTranslate((float) ((posX - image.getWidth() / 2f) - scroll[0]), (float) ((posY - image.getWidth() / 2f) - scroll[1]));
        transform.preScale(6, 6, (float) (image.getWidth() / 2f), (float) (image.getHeight() / 2f));
        transform.preRotate(rotDegrees, image.getWidth()/2, image.getHeight()/2);
        canvas.drawBitmap(image, transform, null);
    }
}
