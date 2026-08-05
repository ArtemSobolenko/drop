package com.artem.drop.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Bucket extends SpriteEntity {

    public Bucket(Sprite sprite) {
        super(sprite);
        setPosition(0f, 0f);
    }

    public void move(float dx) {
        sprite.translateX(dx);
        updateBounds();
    }

    public void setCenterX(float centerX) {
        sprite.setCenterX(centerX);
        updateBounds();
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
        updateBounds();
    }
}
