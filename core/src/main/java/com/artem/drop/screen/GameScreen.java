package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.entity.Drop;
import com.artem.drop.input.GameInputProcessor;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.artem.drop.world.GameWorld;
import com.badlogic.gdx.Gdx;
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
import static com.artem.drop.GameConstants.GAME_EXIT_TEXT;
import static com.artem.drop.GameConstants.GAME_MAIN_MENU_TEXT;
import static com.artem.drop.GameConstants.GAME_PAUSED_TEXT;
import static com.artem.drop.GameConstants.GAME_RESTART_TEXT;
import static com.artem.drop.GameConstants.HUD_LINE_SPACING;
import static com.artem.drop.GameConstants.PAUSE_OVERLAY_LINE_SPACING;

@Slf4j
public class GameScreen implements Screen {

    private final GlyphLayout pausedLayout;
    private final GlyphLayout exitLayout;
    private final GlyphLayout restartLayout;
    private final GlyphLayout mainMenuLayout;

    private final FitViewport viewport;

    private final SpriteBatch spriteBatch;

    private final GameWorld gameWorld;

    private final GameState gameState;

    private final AssetService assetService;

    private final PlayerInput playerInput;

    private final GameInputProcessor inputProcessor;

    private final DefaultScreenNavigator screenNavigator;

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
        this.screenNavigator = context.defaultScreenNavigator();
        this.pausedLayout = new GlyphLayout();
        this.exitLayout = new GlyphLayout();
        this.restartLayout = new GlyphLayout();
        this.mainMenuLayout = new GlyphLayout();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputProcessor);
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

        if (gameState.isPaused()) {

            if (inputProcessor.consumeGameExitRequest()) {
                Gdx.app.exit();
                return;
            }

            if (inputProcessor.consumeGameRestartRequest()) {
                gameState.setPlaying();
                screenNavigator.restartGame();
                return;
            }

            if (inputProcessor.consumeMainMenuRequest()) {
                gameState.setPlaying();
                screenNavigator.showMainMenu();
                return;
            }

            draw();
            return;
        }

        // Exit/restart/main-menu are pause-menu-only actions; discard any
        // pressed while actively playing so they can't fire unexpectedly the
        // next time the player pauses.
        inputProcessor.discardPauseMenuRequests();

        handleInput(delta);
        gameWorld.update(delta);
        draw();
    }

    private void handleInput(float delta) {

        float speed = DEFAULT_SPEED;

        if (playerInput.isLeftShiftPressed()) {
            speed *= DEFAULT_SPEED_MULTIPLIER;
            assetService.getSpeedSound().play(DEFAULT_SPEED_VOLUME);
        }

        //move bucket right
        if (playerInput.isMoveRightPressed()) {
            gameWorld.moveBucket(speed * delta);
        }

        //move bucket left
        if (playerInput.isMoveLeftPressed()) {
            gameWorld.moveBucket(-speed * delta);
        }

        //jump the bucket
        if (inputProcessor.consumePlayerJumpRequest()) {
            log.info("jump pressed");
            gameWorld.jumpBucket();
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

        font.draw(spriteBatch, "Drops missed: " + gameWorld.getDropsMissed(), 0, top - HUD_LINE_SPACING);
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

        BitmapFont pauseFont = assetService.getPauseFont();
        pausedLayout.setText(pauseFont, GAME_PAUSED_TEXT);

        float x = (viewport.getWorldWidth() - pausedLayout.width) / 2f;
        float y = (viewport.getWorldHeight() + pausedLayout.height) / 2f;

        pauseFont.draw(spriteBatch, pausedLayout, x, y);

        drawPauseOverlayOption(assetService.getExitFont(), exitLayout, GAME_EXIT_TEXT, y, 1);
        drawPauseOverlayOption(assetService.getRestartFont(), restartLayout, GAME_RESTART_TEXT, y, 2);
        drawPauseOverlayOption(assetService.getBackToMenuFont(), mainMenuLayout, GAME_MAIN_MENU_TEXT, y, 3);
    }

    private void drawPauseOverlayOption(BitmapFont font, GlyphLayout layout, String text,
                                        float pausedTextY, int lineIndex) {
        layout.setText(font, text);

        float x = (viewport.getWorldWidth() - layout.width) / 2f;
        float y = pausedTextY - lineIndex * PAUSE_OVERLAY_LINE_SPACING;

        font.draw(spriteBatch, layout, x, y);
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
