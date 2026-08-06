package com.artem.drop.world;

import com.artem.drop.entity.Bucket;
import com.artem.drop.entity.Drop;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;

import static com.artem.drop.GameConstants.DEFAULT_DROPLET_CREATION_DELAY;
import static com.artem.drop.GameConstants.WORLD_HEIGHT;
import static com.artem.drop.GameConstants.WORLD_WIDTH;

/**
 * Owns the gameplay simulation: the bucket, the falling drops, spawning and
 * collision resolution. Screens read this state to render it and forward
 * player intent to it; they hold no simulation state themselves.
 */
public class GameWorld {

    private final AssetService assetService;

    @Getter
    private final Bucket bucket;

    @Getter
    private final Array<Drop> drops = new Array<>();

    @Getter
    private int dropsGathered = 0;

    @Getter
    private int dropsMissed = 0;

    private float dropTimer = 0f;

    public GameWorld(AssetService assetService) {
        this.assetService = assetService;
        this.bucket = new Bucket(new Sprite(assetService.getBucketTexture()));
    }

    public void update(float delta) {
        updateBucket(delta);
        updateDrops(delta);
        spawnDrops(delta);
    }

    public void moveBucket(float dx) {
        bucket.move(dx);
        clampBucket();
    }

    public void jumpBucket() {
        bucket.jump();
    }

    private void updateBucket(float delta) {
        bucket.update(delta);
    }

    public void setBucketCenterX(float centerX) {
        bucket.setCenterX(centerX);
        clampBucket();
    }

    private void updateDrops(float delta) {
        for (int i = drops.size - 1; i >= 0; i--) {
            Drop drop = drops.get(i);
            drop.move(delta);

            if (drop.getY() < -drop.getHeight()) {
                dropsMissed++;
                assetService.getDropMissSound().play();
                drops.removeIndex(i);
                continue;
            }

            if (bucket.getBounds().overlaps(drop.getBounds())) {
                dropsGathered++;
                assetService.getDropSound().play();
                drops.removeIndex(i);
            }
        }
    }

    private void spawnDrops(float delta) {
        dropTimer += delta;
        if (dropTimer > DEFAULT_DROPLET_CREATION_DELAY) {
            dropTimer = 0f;
            spawnDrop();
        }
    }

    private void spawnDrop() {
        Drop drop = new Drop(new Sprite(assetService.getDropTexture()));
        drop.setX(MathUtils.random(0f, WORLD_WIDTH - drop.getWidth()));
        drop.setY(WORLD_HEIGHT);

        drops.add(drop);
    }

    private void clampBucket() {
        bucket.setX(MathUtils.clamp(bucket.getX(), 0f, WORLD_WIDTH - bucket.getWidth()));
    }
}
