package com.artem.drop.world;

import com.artem.drop.entity.Drop;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.artem.drop.GameConstants.DEFAULT_DROPLET_CREATION_DELAY;
import static com.artem.drop.GameConstants.WORLD_WIDTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Exercises GameWorld together with the real Bucket/Drop/Sprite/Rectangle
 * objects it manages; only the GPU/audio leaf resources (Texture, Sound) are
 * mocked, since those need a real graphics/audio backend to construct.
 */
class GameWorldIntegrationTest {

    private Sound dropSound;
    private Sound dropMissSound;
    private GameWorld world;

    @BeforeEach
    void setUp() {
        AssetService assetService = mock(AssetService.class);
        when(assetService.getBucketTexture()).thenReturn(mock(Texture.class));
        when(assetService.getDropTexture()).thenReturn(mock(Texture.class));

        dropSound = mock(Sound.class);
        dropMissSound = mock(Sound.class);
        when(assetService.getDropSound()).thenReturn(dropSound);
        when(assetService.getDropMissSound()).thenReturn(dropMissSound);

        world = new GameWorld(assetService);
    }

    @Test
    void moveBucketClampsToLeftWorldEdge() {
        world.moveBucket(-100f);

        assertEquals(0f, world.getBucket().getX());
    }

    @Test
    void moveBucketClampsToRightWorldEdge() {
        world.moveBucket(100f);

        assertEquals(WORLD_WIDTH - world.getBucket().getWidth(), world.getBucket().getX(), 1e-6f);
    }

    @Test
    void setBucketCenterXClampsWithinWorldBounds() {
        world.setBucketCenterX(1000f);

        assertEquals(WORLD_WIDTH - world.getBucket().getWidth(), world.getBucket().getX(), 1e-6f);
    }

    @Test
    void spawnsExactlyOneDropAfterCreationDelayElapses() {
        world.update(DEFAULT_DROPLET_CREATION_DELAY + 0.01f);

        assertEquals(1, world.getDrops().size);
    }

    @Test
    void doesNotSpawnBeforeCreationDelayElapses() {
        world.update(DEFAULT_DROPLET_CREATION_DELAY - 0.5f);

        assertEquals(0, world.getDrops().size);
    }

    @Test
    void catchingADropIncrementsGatheredCountAndPlaysSound() {
        world.update(DEFAULT_DROPLET_CREATION_DELAY + 0.01f);

        Drop drop = world.getDrops().first();
        drop.setX(world.getBucket().getX());
        drop.setY(world.getBucket().getY());

        world.update(0.0001f);

        assertEquals(1, world.getDropsGathered());
        assertEquals(0, world.getDrops().size);
        verify(dropSound, times(1)).play();
    }

    @Test
    void missingADropIncrementsMissedCountAndPlaysSound() {
        world.update(DEFAULT_DROPLET_CREATION_DELAY + 0.01f);
        Drop drop = world.getDrops().first();
        drop.setY(-drop.getHeight() - 1f);

        world.update(0.0001f);

        assertEquals(1, world.getDropsMissed());
        assertEquals(0, world.getDrops().size);
        verify(dropMissSound, times(1)).play();
    }

    @Test
    void jumpBucketMovesTheBucketUpwardOnTheNextUpdate() {
        world.jumpBucket();
        world.update(0.05f);

        assertTrue(world.getBucket().getY() > 0f);
    }
}
