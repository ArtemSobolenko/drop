package com.artem.drop.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;

import static com.artem.drop.GameConstants.DEFAULT_DROPLET_FALLING_SPEED;

public class Drop extends SpriteEntity {

    public Drop(Sprite sprite) {
        super(sprite);
    }

    public void move(float delta) {
        sprite.translateY(DEFAULT_DROPLET_FALLING_SPEED * delta);
        updateBounds();
    }
}
