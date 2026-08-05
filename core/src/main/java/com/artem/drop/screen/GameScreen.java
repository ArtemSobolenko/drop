package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.entity.Drop;
import com.artem.drop.input.GameInputProcessor;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.state.GameState;
import com.artem.drop.world.GameWorld;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.extern.slf4j.Slf4j;

import static com.artem.drop.GameConstants.DEFAULT_SPEED;
import static com.artem.drop.GameConstants.DEFAULT_SPEED_MULTIPLIER;
import static com.artem.drop.GameConstants.DEFAULT_SPEED_VOLUME;
import static com.artem.drop.GameConstants.GAME_PAUSED_TEXT;

@Slf4j
public class GameScreen implements Screen {

    private final GlyphLayout glyphLayout;

    private final FitViewport viewport;

    private final SpriteBatch spriteBatch;

    private final GameWorld gameWorld;

    private final GameState gameState;

    private final AssetService assetService;

    private final PlayerInput playerInput;

    private final GameInputProcessor inputProcessor;

    private boolean dragging = false;
    private boolean previousPausedState = false;

    public GameScreen(final GameContext context, final GameWorld gameWorld) {

        this.gameWorld = gameWorld;

        this.assetService = context.assetService();
        this.playerInput = context.playerInput();
        this.inputProcessor = context.inputProcessor();
        this.gameState = context.gameState();
        this.viewport = context.viewport();
        this.spriteBatch = context.spriteBatch();
        this.glyphLayout = new GlyphLayout();
    }

    @Override
    public void show() {
        // start the playback of the background music when the screen is shown
        assetService.getConfiguredMusic().play();
    }

    @Override
    public void render(float delta) {
        viewport.apply();

        if (inputProcessor.consumePauseRequest()) {
            gameState.togglePause();
        }

        updatePauseState();

        if (!gameState.isPaused()) {
            handleInput(delta);
            gameWorld.update(delta);
        }
        draw();
    }

    private void handleInput(float delta) {

        float speed = DEFAULT_SPEED;

        if (playerInput.isLeftShiftPressed()) {
            speed *= DEFAULT_SPEED_MULTIPLIER;
            assetService.getSpeedSound().play(DEFAULT_SPEED_VOLUME);
        }

        //move right
        if (playerInput.isMoveRightPressed()) {
            gameWorld.moveBucket(speed * delta);
        }

        //move left
        if (playerInput.isMoveLeftPressed()) {
            gameWorld.moveBucket(-speed * delta);
        }

        handleTouchInput();
    }

    private void handleTouchInput() {

        if (!playerInput.isTouched()) {
            dragging = false;
            return;
        }

        Vector2 touchPos = playerInput.getTouchPos();
        viewport.unproject(touchPos);

        if (playerInput.isJustTouched()) {
            dragging = gameWorld.getBucket().getBounds().contains(touchPos);
        }

        if (dragging) {
            gameWorld.setBucketCenterX(touchPos.x);
        }
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
        BitmapFont font = assetService.getHudFont();

        float top = viewport.getWorldHeight();

        font.draw(spriteBatch, "Drops collected: " + gameWorld.getDropsGathered(), 0, top);

        font.draw(spriteBatch, "Drops missed: " + gameWorld.getDropsMissed(), 0, top - 0.4f);
    }

    private void drawDrops() {
        for (Drop drop : gameWorld.getDrops()) {
            drop.render(spriteBatch);
        }
    }

    private void drawBucket() {
        gameWorld.getBucket().render(spriteBatch);
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

        BitmapFont font = assetService.getPauseFont();

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
