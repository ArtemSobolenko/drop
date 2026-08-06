package com.artem.drop.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;

import static com.artem.drop.GameConstants.BUCKET_GRAVITY;
import static com.artem.drop.GameConstants.BUCKET_JUMP_SPEED;
import static com.artem.drop.GameConstants.GROUND_Y;
import static com.artem.drop.GameConstants.WORLD_MIN_X;

public class Bucket extends SpriteEntity {

    private float velocityY = 0f;
    private boolean onGround = true;

    public Bucket(Sprite sprite) {
        super(sprite);
        setPosition(WORLD_MIN_X, GROUND_Y);
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
            velocityY = BUCKET_JUMP_SPEED;
            onGround = false;
        }
    }

    public void update(float delta) {

        velocityY += BUCKET_GRAVITY * delta;

        sprite.translateY(velocityY * delta);

        if (sprite.getY() <= GROUND_Y) {
            sprite.setY(GROUND_Y);
            velocityY = 0f;
            onGround = true;
        }

        updateBounds();
    }
}
