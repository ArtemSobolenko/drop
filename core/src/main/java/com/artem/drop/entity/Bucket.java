package com.artem.drop.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;

public class Bucket {

    private final Sprite sprite;

    @Getter
    private Rectangle bounds;

    public Bucket(Sprite sprite) {
        this.sprite = sprite;
        this.sprite.setSize(1, 1);
        this.sprite.setPosition(0f, 0f);
        this.bounds = new Rectangle();
        updateBounds();
    }

    public void move(float dx) {
        sprite.translateX(dx);
        updateBounds();
    }

    public void setX(float x) {
        sprite.setX(x);
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

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void updateBounds() {
        bounds.set(sprite.getBoundingRectangle());
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

}
