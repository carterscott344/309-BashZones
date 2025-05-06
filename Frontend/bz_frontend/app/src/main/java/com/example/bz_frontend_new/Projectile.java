package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Basic projectile class, only use/make Subclasses from here.
 */
public class Projectile extends GameItem{
    // A projectile contains a circular hitbox object
    PlayerHitbox projHitbox;

    // If projectile's hitbox is hit, the projectile is hit
    private boolean projectileHit;

    // By default, hitbox is set by image positions
    public Projectile(Context context, double posX, double posY, int radius) {
        super(context, posX, posY);

        // Init projHitbox
        projHitbox = new PlayerHitbox((int) posX, (int) posY, radius);

        // projectileHit is false on construction
        projectileHit = false;
    }

    // Update now also changes hitbox positions according to position
    @Override
    public void update() {
        super.update();
        // Update hitbox
        projHitbox.update((int) super.getPosX(), (int) super.getPosY());

        // If hitbox is hit, projectile is also hit
        if (projHitbox.isHit()) {
            projectileHit = true;
        }
    }

    public PlayerHitbox getProjHitbox() {
        return projHitbox;
    }

    public boolean isProjectileHit() {
        return projectileHit;
    }
}
