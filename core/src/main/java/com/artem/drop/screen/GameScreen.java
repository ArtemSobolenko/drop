package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.state.GameState;
import com.artem.drop.entity.Bucket;
import com.artem.drop.entity.Drop;
import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.world.GameWorld;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import static com.artem.drop.GameConstants.GAME_PAUSED_TEXT;

@Slf4j
public class GameScreen implements Screen {

    private final GlyphLayout glyphLayout;

    private final FitViewport viewport;

    private final SpriteBatch spriteBatch;

    private final GameContext gameContext;

    private final GameWorld gameWorld;

    private final GameState gameState;

    private final AssetService assetService;

    private final DesktopPlayerInput desktopPlayerInput;

    private final Bucket bucket;

    private final Array<Drop> drops;

    private float dropTimer = 0f;
    private int dropsGathered = 0;
    private int dropMissed = 0;

    private boolean dragging = false;
    private boolean previousPausedState = false;

    public GameScreen(final GameContext context, final GameWorld gameWorld) {

        this.gameContext = context;
        this.gameWorld = gameWorld;

        this.assetService = context.assetService();

        this.desktopPlayerInput = context.desktopPlayerInput();

        this.gameState = context.gameState();

        this.viewport = context.viewport();
        this.spriteBatch = context.spriteBatch();
        this.glyphLayout = new GlyphLayout();

        bucket = new Bucket(new Sprite(assetService.getBucketTexture()));

        drops = new Array<>();
    }

    @Override
    public void show() {
        // start the playback of the background music when the screen is shown
        assetService.getConfiguredMusic().play();
    }

    @Override
    public void render(float delta) {
        viewport.apply();

        if (desktopPlayerInput.isPausePressed()) {
            gameState.togglePause();
        }

        updatePauseState();

        if (!gameState.isPaused()) {
            input(delta);
            gameWorld.update(delta);
            logic(delta);
        }
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
        drawDrops();
        drawBucket();

        drawHud();
        drawPauseOverlay();

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

        font.draw(spriteBatch, "Drops missed: " + dropMissed, 0, top - 0.4f);
    }

    private void drawDrops() {
        for (Drop drop : drops) {
            drop.render(spriteBatch);
        }
    }

    private void drawBucket() {
        bucket.render(spriteBatch);
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

    private void updatePauseState() {

        if (gameState.isPaused() != previousPausedState) {

            if (gameState.isPaused()) {
                assetService.getConfiguredMusic().pause();
            } else {
                assetService.getConfiguredMusic().play();
            }

            previousPausedState = gameState.isPaused();
        }
    }

    private void drawPauseOverlay() {

        if (!gameState.isPaused()) {
            return;
        }

        BitmapFont font = gameContext.bitmapFont();

        glyphLayout.setText(font, GAME_PAUSED_TEXT);

        float x = (viewport.getWorldWidth() - glyphLayout.width) / 2f;
        float y = (viewport.getWorldHeight() + glyphLayout.height) / 2f;

        font.draw(spriteBatch, glyphLayout, x, y);
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
