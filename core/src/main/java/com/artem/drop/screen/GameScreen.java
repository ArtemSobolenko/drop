package com.artem.drop.screen;

import com.artem.drop.GameContext;
import com.artem.drop.entity.Bucket;
import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.extern.slf4j.Slf4j;

import static com.artem.drop.GameConstants.DEFAULT_DROPLET_CREATION_DELAY;
import static com.artem.drop.GameConstants.DEFAULT_SPEED;
import static com.artem.drop.GameConstants.DEFAULT_SPEED_MULTIPLIER;
import static com.artem.drop.GameConstants.DEFAULT_SPEED_VOLUME;

@Slf4j
public class GameScreen implements Screen {

    private final GameContext gameContext;

    private final AssetService assetService;

    private final DesktopPlayerInput desktopPlayerInput;

    private Bucket bucket;

    private Array<Sprite> dropSprites;

    private final FitViewport viewport;

    private final SpriteBatch spriteBatch;

    private Rectangle dropRectangle;

    private float dropTimer;
    private int dropsGathered;

    private boolean dragging = false;

    public GameScreen(final GameContext context) {

        this.gameContext = context;

        this.assetService = context.assetService();

        this.desktopPlayerInput = context.desktopPlayerInput();

        this.viewport = context.viewport();
        this.spriteBatch = context.spriteBatch();

        bucket = new Bucket(new Sprite(assetService.getBucketTexture()));

        dropSprites = new Array<>();

        dropRectangle = new Rectangle();

        dropTimer = 0f;
        dropsGathered = 0;
    }

    @Override
    public void show() {
        // start the playback of the background music when the screen is shown
        assetService.getConfiguredMusic().play();
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        input(delta);
        logic(delta);
        draw();
    }

    private void input(float delta) {

        float speed = DEFAULT_SPEED;
        float dx;

        if (desktopPlayerInput.isLeftShiftPressed()) {
            speed *= DEFAULT_SPEED_MULTIPLIER;
            assetService.getSpeedSound().play(DEFAULT_SPEED_VOLUME);
        }

        //move right
        if (desktopPlayerInput.isMoveRightPressed()) {
            dx = speed * delta;
            bucket.move(dx);
        }

        //move left
        if (desktopPlayerInput.isMoveLeftPressed()) {
            dx = speed * delta;
            bucket.move(-dx);
        }

        if (desktopPlayerInput.isTouched()) {
            Vector2 touchPos = desktopPlayerInput.getTouchPos();
            viewport.unproject(touchPos);

            if (desktopPlayerInput.isJustTouched()) {
                dragging = bucket.getBounds().contains(touchPos);
            }

            if (!desktopPlayerInput.isTouched()) {
                dragging = false;
            }

            if (dragging) {
                bucket.setCenterX(touchPos.x);
            }
        }
    }

    private void logic(float delta) {

        clampBucket();

        runDropLogicLoop(delta);

//        log.info("dropSprites loop finished");

        createDropletWithDelay(delta);
    }

    private void draw() {

        ScreenUtils.clear(Color.BLACK);

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        drawBackground();
        bucket.render(spriteBatch);
        drawHug();
        drawDrops();

        spriteBatch.end();
    }

    private void drawBackground() {
        spriteBatch.draw(assetService
            .getBackgroundTexture(), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    private void drawHug() {
        gameContext.bitmapFont()
            .draw(spriteBatch, "Drops collected: " + dropsGathered, 0, viewport.getWorldHeight());
    }

    private void drawDrops() {
        for (Sprite dropSprite : dropSprites) {
//            log.info("From drawDrops: dropSprites size = {}", dropSprites.size);
            dropSprite.draw(spriteBatch);
        }
    }

    private void runDropLogicLoop(float delta) {
        if (!dropSprites.isEmpty()) {
//            log.info("From logic: dropSprites size = {}", dropSprites.size);
            for (Sprite dropSprite : dropSprites) {

                dropSprite.translateY(-2f * delta);
                dropRectangle.set(dropSprite.getBoundingRectangle());

                if (dropSprite.getY() < -dropSprite.getHeight()) {
                    dropSprites.removeValue(dropSprite, true);
                }

                if (bucket.getBounds().overlaps(dropRectangle)) {
                    dropsGathered++;
                    dropSprites.removeValue(dropSprite, true);
                    assetService.getDropSound().play();
                }
            }
        }
    }

    private void runDropLogicLoopLegacy(float delta) {
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            log.info("From logic: dropSprites size = {}", dropSprites.size);
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            }

            if (bucket.getBounds().overlaps(dropRectangle)) {
                dropsGathered++;
                dropSprites.removeIndex(i);
                assetService.getDropSound().play();
            }
        }
    }

    private void createDropletWithDelay(float delta) {
        dropTimer += delta;
        if (dropTimer > DEFAULT_DROPLET_CREATION_DELAY) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void createDroplet() {

        float dropWidth = 1;
        float dropHeight = 1;

        Sprite dropSprite = new Sprite(assetService.getDropTexture());
        dropSprite.setSize(dropWidth, dropHeight);

        dropSprite.setX(MathUtils.random(0F, viewport.getWorldWidth() - dropWidth));
        dropSprite.setY(viewport.getWorldHeight());

        dropSprites.add(dropSprite);
    }

    private void clampBucket() {
        bucket.setX(MathUtils.clamp(bucket.getX(), 0,
            viewport.getWorldWidth() - bucket.getWidth()));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        //do not call dispose here
    }
}
