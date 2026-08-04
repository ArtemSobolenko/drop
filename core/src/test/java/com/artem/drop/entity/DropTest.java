package com.artem.drop.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import static com.artem.drop.GameConstants.DEFAULT_DROPLET_FALLING_SPEED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DropTest {

    private Drop newDrop() {
        return new Drop(new Sprite(mock(Texture.class)));
    }

    @Test
    void startsWithUnitSize() {
        Drop drop = newDrop();

        assertEquals(1f, drop.getWidth());
        assertEquals(1f, drop.getHeight());
    }

    @Test
    void moveAppliesFallingSpeedScaledByDelta() {
        Drop drop = newDrop();
        drop.setY(10f);

        drop.move(0.5f);

        assertEquals(10f + DEFAULT_DROPLET_FALLING_SPEED * 0.5f, drop.getY(), 1e-6f);
    }

    @Test
    void setXAndSetYUpdateBounds() {
        Drop drop = newDrop();

        drop.setX(2f);
        drop.setY(3f);

        assertEquals(2f, drop.getBounds().x);
        assertEquals(3f, drop.getBounds().y);
    }

    @Test
    void renderDrawsItsSpriteOnTheGivenBatch() {
        Sprite sprite = mock(Sprite.class);
        when(sprite.getBoundingRectangle()).thenReturn(new Rectangle());
        Drop drop = new Drop(sprite);
        SpriteBatch batch = mock(SpriteBatch.class);

        drop.render(batch);

        verify(sprite).draw(batch);
    }
}
