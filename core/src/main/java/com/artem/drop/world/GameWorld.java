package com.artem.drop.world;

import com.artem.drop.context.GameContext;
import com.artem.drop.entity.Bucket;
import com.artem.drop.entity.Drop;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameWorld {

    private final Bucket bucket;
    private final Array<Drop> drops;

    public GameWorld(GameContext context) {
        AssetService assets = context.assetService();

        this.bucket = new Bucket(new Sprite(assets.getBucketTexture()));
        this.drops = new Array<>();
    }

    public void update(float delta) {
        log.info("We are in game world, delta={}", delta);
    }
}
