package com.example.bz_frontend_new.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.bz_frontend_new.GamePanelLauncher;
import com.example.bz_frontend_new.R;

// Enum for entities and their spritesheets
public enum GameCharacters {

    // Enums
    PLAYER(R.drawable.player_head);

    // Spritesheet (Just individual images for this enum)
    private Bitmap spriteSheet;

    // Sprites (Use ONLY if not an individual sprite, but a spritesheet, assume 4x4 size, 16x16)
    private Bitmap[][] sprites = new Bitmap[4][4];

    // Options for resizing images
    private BitmapFactory.Options options = new BitmapFactory.Options();

    GameCharacters(int resID) {
        // Removes default scaling of phone for accurate image sizes
        options.inScaled = false;
        spriteSheet = BitmapFactory.decodeResource(GamePanelLauncher.getGameContext().getResources(), resID, options);
    }

    // Used if wanting to render entire sheet or if the spritesheet is just one sprite
    public Bitmap getSpriteSheet(boolean scale) {
        if (scale) return getScaledBitmap(spriteSheet);
        return spriteSheet;
    }

    // Creates sprites array if wanted from a spritesheet
    public void createSprites() {
        // For loop for sprite sheet images
        for(int i = 0; i < sprites.length; i++) {
            for(int j = 0; j < sprites.length; j++) {
                sprites[i][j] = getScaledBitmap(Bitmap.createBitmap(spriteSheet, 16 * i, 16 * j, 16, 16));
            }
        }
    }

    // Used if rendering sprites from a spritesheet, do NOT use if individual image
    public Bitmap getSprite(int xPos, int yPos) {
        return sprites[xPos][yPos];
    }

    // Used for rendering scaled images
    private Bitmap getScaledBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 6, bitmap.getHeight() * 6, false);
    }
}
