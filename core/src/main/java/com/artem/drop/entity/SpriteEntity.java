package com.artem.drop.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;

import static com.artem.drop.GameConstants.ENTITY_SIZE;

/**
 * Common sprite + AABB bounds wrapper shared by every game entity. Bounds are
 * recomputed from the sprite on every mutation so getBounds() is always
 * current for collision checks.
 */
public abstract class SpriteEntity {

    protected final Sprite sprite;

    @Getter
    private final Rectangle bounds = new Rectangle();

    protected SpriteEntity(Sprite sprite) {
        this.sprite = sprite;
        this.sprite.setSize(ENTITY_SIZE, ENTITY_SIZE);
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
