package com.artem.drop.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BucketTest {

    // Bucket only ever calls math methods on Sprite (position/size/bounds), never
    // anything GPU-backed, so a mocked Texture is enough to build a real Sprite
    // without needing any libGDX graphics context.
    private Bucket newBucket() {
        return new Bucket(new Sprite(mock(Texture.class)));
    }

    @Test
    void startsAtOriginWithUnitSize() {
        Bucket bucket = newBucket();

        assertEquals(0f, bucket.getX());
        assertEquals(0f, bucket.getY());
        assertEquals(1f, bucket.getWidth());
        assertEquals(1f, bucket.getHeight());
    }

    @Test
    void moveTranslatesXAndUpdatesBounds() {
        Bucket bucket = newBucket();

        bucket.move(2f);

        assertEquals(2f, bucket.getX());
        assertEquals(2f, bucket.getBounds().x);
    }

    @Test
    void setXMovesToExactPosition() {
        Bucket bucket = newBucket();

        bucket.setX(3f);

        assertEquals(3f, bucket.getX());
        assertEquals(3f, bucket.getBounds().x);
    }

    @Test
    void setCenterXCentersBucketOnGivenX() {
        Bucket bucket = newBucket();

        bucket.setCenterX(5f);

        assertEquals(4.5f, bucket.getX(), 1e-6f);
    }

    @Test
    void setPositionMovesBothAxes() {
        Bucket bucket = newBucket();

        bucket.setPosition(1f, 2f);

        assertEquals(1f, bucket.getX());
        assertEquals(2f, bucket.getY());
    }

    @Test
    void renderDrawsItsSpriteOnTheGivenBatch() {
        Sprite sprite = mock(Sprite.class);
        when(sprite.getBoundingRectangle()).thenReturn(new Rectangle());
        Bucket bucket = new Bucket(sprite);
        SpriteBatch batch = mock(SpriteBatch.class);

        bucket.render(batch);

        verify(sprite).draw(batch);
    }
}
