package com.artem.drop.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;

import static com.artem.drop.GameConstants.DEFAULT_DROPLET_FALLING_SPEED;

public class Drop {

    private final Sprite sprite;

    @Getter
    private Rectangle bounds;

    public Drop(Sprite sprite) {
        this.sprite = sprite;
        this.sprite.setSize(1, 1);
        this.bounds = new Rectangle();
        updateBounds();
    }

    public void move(float delta) {
        sprite.translateY(DEFAULT_DROPLET_FALLING_SPEED * delta);
        updateBounds();
    }

    public void setX(float x) {
        sprite.setX(x);
        updateBounds();
    }

    public void setY(float y) {
        sprite.setY(y);
        updateBounds();
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
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

    public void updateBounds() {
        bounds.set(sprite.getBoundingRectangle());
    }
}
