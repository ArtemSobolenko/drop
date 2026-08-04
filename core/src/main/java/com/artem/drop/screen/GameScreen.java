package com.artem.drop.screen;

import com.artem.drop.GameContext;
import com.artem.drop.entity.Bucket;
import com.artem.drop.entity.Drop;
import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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

    private Array<Drop> drops;

    private final FitViewport viewport;

    private final SpriteBatch spriteBatch;

    private float dropTimer;
    private int dropsGathered;
    private int dropMissed;

    private boolean dragging = false;

    public GameScreen(final GameContext context) {

        this.gameContext = context;

        this.assetService = context.assetService();

        this.desktopPlayerInput = context.desktopPlayerInput();

        this.viewport = context.viewport();
        this.spriteBatch = context.spriteBatch();

        bucket = new Bucket(new Sprite(assetService.getBucketTexture()));

        drops = new Array<>();

        dropTimer = 0f;
        dropsGathered = 0;
        dropMissed = 0;
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
        drawHud();
        drawDrops();

        spriteBatch.end();
    }

    private void drawBackground() {
        spriteBatch.draw(assetService
            .getBackgroundTexture(), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    private void drawHud() {
        BitmapFont font = gameContext.bitmapFont();

        float top = viewport.getWorldHeight();

        font.draw(spriteBatch, "Drops collected: " + dropsGathered, 0, top);

        font.draw(spriteBatch, "Drops missed: " + dropMissed, 0, top - 0.3f);
    }

    private void drawDrops() {
        for (Drop drop : drops) {
            drop.render(spriteBatch);
        }
    }

    private void runDropLogicLoop(float delta) {
        for (int i = drops.size - 1; i >= 0; i--) {
//            log.info("From logic: dropSprites size = {}", dropSprites.size);

            Drop drop = drops.get(i);
            drop.move(delta);

            if (drop.getY() < -drop.getHeight()) {
                dropMissed++;
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

    private void createDropletWithDelay(float delta) {
        dropTimer += delta;
        if (dropTimer > DEFAULT_DROPLET_CREATION_DELAY) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void createDroplet() {

        Drop drop = new Drop(new Sprite(assetService.getDropTexture()));
        drop.setX(MathUtils.random(0F, viewport.getWorldWidth() - drop.getWidth()));
        drop.setY(viewport.getWorldHeight());

        drops.add(drop);
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
