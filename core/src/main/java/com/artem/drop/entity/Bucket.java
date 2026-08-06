package com.artem.drop.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Bucket extends SpriteEntity {

    private float velocityY = 0f;
    private boolean onGround = true;

    private static final float GRAVITY = -15f;
    private static final float JUMP_SPEED = 7f;

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

    public void jump() {
        if (onGround) {
            velocityY = JUMP_SPEED;
            onGround = false;
        }
    }

    public void update(float delta) {

        velocityY += GRAVITY * delta;

        sprite.translateY(velocityY * delta);

        if (sprite.getY() <= 0) {
            sprite.setY(0);
            velocityY = 0;
            onGround = true;
        }

        updateBounds();
    }
}
