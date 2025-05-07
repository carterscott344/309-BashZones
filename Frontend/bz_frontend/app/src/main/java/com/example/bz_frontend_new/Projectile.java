package com.example.bz_frontend_new;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Basic projectile class, only use/make Subclasses from here.
 */
public class Projectile extends GameItem{
    // A projectile contains a circular hitbox object
    PlayerHitbox projHitbox;

    // A projectile should also know its local player's hitbox
    private PlayerHitbox localPlayer;

    // If projectile's hitbox is hit, the projectile is hit
    private boolean projectileHit;

    // Type of projectile
    private String typeOfProj;

    // By default, hitbox is set by image positions
    public Projectile(Context context, double posX, double posY, int radius, PlayerHitbox localPlayer) {
        super(context, posX, posY);

        // Init projHitbox
        projHitbox = new PlayerHitbox((int) posX, (int) posY, radius, false);

        // projectileHit is false on construction
        projectileHit = false;

        // Init local player hitbox
        this.localPlayer = localPlayer;

        // Init type to ERROR, as this class should NOT be used
        typeOfProj = "ERROR";
    }

    // Update now also changes hitbox positions according to position
    @Override
    public void update() {
        super.update();
        // Update hitbox
        projHitbox.update((int) super.getPosX(), (int) super.getPosY());

        // If hitbox is hit, projectile is also hit
        if (projHitbox.isHit(localPlayer)) {
            projHitbox.setIsHit(true);
            projectileHit = true;
        }
        // Only used in rendering purposes, in reality our projectiles only care about first contact
        else {
            projHitbox.setIsHit(false);
        }
    }

    public PlayerHitbox getProjHitbox() {
        return projHitbox;
    }

    public boolean isProjectileHit() {
        return projectileHit;
    }

    public void setTypeOfProj(String typeOfProj) {
        this.typeOfProj = typeOfProj;
    }

    public String getTypeOfProj() {
        return typeOfProj;
    }
}
