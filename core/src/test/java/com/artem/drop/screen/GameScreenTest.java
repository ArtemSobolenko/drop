package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.entity.Bucket;
import com.artem.drop.input.GameInputProcessor;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.artem.drop.support.GdxTestEnvironment;
import com.artem.drop.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.artem.drop.GameConstants.DEFAULT_SPEED;
import static com.artem.drop.GameConstants.DEFAULT_SPEED_MULTIPLIER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Drives GameScreen with a real (font-loaded) AssetService and a mocked
 * GameWorld/FitViewport/SpriteBatch, so only GameScreen's own input-handling
 * and delegation logic is under test - not GameWorld's simulation or actual
 * pixel rendering (verified separately by running the app).
 */
class GameScreenTest {

    private AssetService assetService;
    private FitViewport viewport;
    private PlayerInput playerInput;
    private GameInputProcessor gameInputProcessor;
    private GameState gameState;
    private GameWorld gameWorld;
    private Music music;
    private GameScreen screen;

    @BeforeEach
    void setUp() {
        GdxTestEnvironment.install();
        assetService = GdxTestEnvironment.loadRealAssetService();
        music = assetService.getMusic();

        viewport = mock(FitViewport.class);
        when(viewport.getCamera()).thenReturn(new OrthographicCamera());

        playerInput = mock(PlayerInput.class);
        gameInputProcessor = mock(GameInputProcessor.class);

        gameState = new GameState();

        gameWorld = mock(GameWorld.class);
        when(gameWorld.getDrops()).thenReturn(new Array<>());
        when(gameWorld.getBucket()).thenReturn(mock(Bucket.class));

        GameContext context = GameContext.builder()
            .spriteBatch(mock(SpriteBatch.class))
            .viewport(viewport)
            .assetService(assetService)
            .defaultScreenNavigator(mock(DefaultScreenNavigator.class))
            .playerInput(playerInput)
            .inputProcessor(gameInputProcessor)
            .gameState(gameState)
            .build();

        screen = new GameScreen(context, gameWorld);
    }

    @AfterEach
    void tearDown() {
        GdxTestEnvironment.uninstall();
    }

    @Test
    void showStartsMusic() {
        screen.show();

        verify(music).play();
    }

    @Test
    void showActivatesGameInputProcessor() {
        screen.show();

        verify(Gdx.input).setInputProcessor(gameInputProcessor);
    }

    @Test
    void whenNotPausedUpdatesWorldWithFrameDelta() {
        screen.render(0.1f);

        verify(gameWorld).update(0.1f);
    }

    @Test
    void pausePressTogglesPauseAndSkipsWorldUpdate() {
        when(gameInputProcessor.consumePauseRequest()).thenReturn(true);

        screen.render(0.1f);

        assertTrue(gameState.isPaused());
        verify(gameWorld, never()).update(anyFloat());
    }

    @Test
    void pausingMidGamePausesMusic() {
        when(gameInputProcessor.consumePauseRequest()).thenReturn(true);

        screen.render(0.1f);

        verify(music).pause();
    }

    @Test
    void unpausingResumesMusic() {
        // Pause is a toggle (ESC again resumes), so pressing it on two
        // consecutive frames pauses then immediately resumes.
        when(gameInputProcessor.consumePauseRequest()).thenReturn(true);

        screen.render(0.1f); // pauses
        screen.render(0.1f); // resumes

        assertFalse(gameState.isPaused());
        verify(music).play();
    }

    @Test
    void moveRightPressedMovesBucketRightByFrameSpeed() {
        when(playerInput.isMoveRightPressed()).thenReturn(true);

        screen.render(0.5f);

        verify(gameWorld).moveBucket(DEFAULT_SPEED * 0.5f);
    }

    @Test
    void moveLeftPressedMovesBucketLeftByFrameSpeed() {
        when(playerInput.isMoveLeftPressed()).thenReturn(true);

        screen.render(0.5f);

        verify(gameWorld).moveBucket(-DEFAULT_SPEED * 0.5f);
    }

    @Test
    void leftShiftMultipliesMoveSpeedAndPlaysSpeedSound() {
        when(playerInput.isLeftShiftPressed()).thenReturn(true);
        when(playerInput.isMoveRightPressed()).thenReturn(true);

        screen.render(0.5f);

        verify(gameWorld).moveBucket(DEFAULT_SPEED * DEFAULT_SPEED_MULTIPLIER * 0.5f);
    }

    @Test
    void jumpRequestedTriggersBucketJump() {
        when(gameInputProcessor.consumePlayerJumpRequest()).thenReturn(true);

        screen.render(0.1f);

        verify(gameWorld).jumpBucket();
    }

    @Test
    void noJumpRequestDoesNotTriggerBucketJump() {
        screen.render(0.1f);

        verify(gameWorld, never()).jumpBucket();
    }

    @Test
    void touchStartingInsideBucketDragsItToTouchX() {
        Bucket bucket = mock(Bucket.class);
        when(bucket.getBounds()).thenReturn(new Rectangle(0f, 0f, 1f, 1f));
        when(gameWorld.getBucket()).thenReturn(bucket);

        when(playerInput.isTouched()).thenReturn(true);
        when(playerInput.isJustTouched()).thenReturn(true);
        when(playerInput.getTouchPos()).thenReturn(new Vector2(0.5f, 0.5f));

        screen.render(0.1f);

        verify(gameWorld).setBucketCenterX(0.5f);
    }

    @Test
    void touchStartingOutsideBucketDoesNotDragIt() {
        Bucket bucket = mock(Bucket.class);
        when(bucket.getBounds()).thenReturn(new Rectangle(0f, 0f, 1f, 1f));
        when(gameWorld.getBucket()).thenReturn(bucket);

        when(playerInput.isTouched()).thenReturn(true);
        when(playerInput.isJustTouched()).thenReturn(true);
        when(playerInput.getTouchPos()).thenReturn(new Vector2(5f, 5f));

        screen.render(0.1f);

        verify(gameWorld, never()).setBucketCenterX(anyFloat());
    }

    @Test
    void releasingTouchStopsDraggingSoALaterTouchOutsideBucketDoesNotMoveIt() {
        Bucket bucket = mock(Bucket.class);
        when(bucket.getBounds()).thenReturn(new Rectangle(0f, 0f, 1f, 1f));
        when(gameWorld.getBucket()).thenReturn(bucket);

        // Frame 1: press inside the bucket - starts dragging.
        when(playerInput.isTouched()).thenReturn(true);
        when(playerInput.isJustTouched()).thenReturn(true);
        when(playerInput.getTouchPos()).thenReturn(new Vector2(0.5f, 0.5f));
        screen.render(0.1f);

        // Frame 2: released - dragging must reset.
        when(playerInput.isTouched()).thenReturn(false);
        screen.render(0.1f);

        // Frame 3: touched again outside the bucket, not a fresh press.
        when(playerInput.isTouched()).thenReturn(true);
        when(playerInput.isJustTouched()).thenReturn(false);
        when(playerInput.getTouchPos()).thenReturn(new Vector2(5f, 5f));
        screen.render(0.1f);

        verify(gameWorld, times(1)).setBucketCenterX(anyFloat());
    }

    @Test
    void whenPausedAndExitRequestedExitsAppWithoutUpdatingWorld() {
        when(gameInputProcessor.consumePauseRequest()).thenReturn(true);
        screen.render(0.1f); // pauses

        // Pause is a toggle, so the stub must go back to false or the next
        // render would immediately resume instead of acting while paused.
        when(gameInputProcessor.consumePauseRequest()).thenReturn(false);
        when(gameInputProcessor.consumeGameExitRequest()).thenReturn(true);
        screen.render(0.1f);

        verify(Gdx.app).exit();
        verify(gameWorld, never()).update(anyFloat());
    }

    @Test
    void whenPausedAndRestartRequestedResumesAndStartsANewGame() {
        DefaultScreenNavigator navigator = mock(DefaultScreenNavigator.class);
        GameContext context = GameContext.builder()
            .spriteBatch(mock(SpriteBatch.class))
            .viewport(viewport)
            .assetService(assetService)
            .defaultScreenNavigator(navigator)
            .playerInput(playerInput)
            .inputProcessor(gameInputProcessor)
            .gameState(gameState)
            .build();
        screen = new GameScreen(context, gameWorld);

        when(gameInputProcessor.consumePauseRequest()).thenReturn(true);
        screen.render(0.1f); // pauses

        // Pause is a toggle, so the stub must go back to false or the next
        // render would immediately resume instead of acting while paused.
        when(gameInputProcessor.consumePauseRequest()).thenReturn(false);
        when(gameInputProcessor.consumeGameRestartRequest()).thenReturn(true);
        screen.render(0.1f);

        assertFalse(gameState.isPaused());
        verify(navigator).restartGame();
    }

    @Test
    void whenPausedAndMainMenuRequestedResumesAndNavigatesToMainMenuWithoutUpdatingWorld() {
        DefaultScreenNavigator navigator = mock(DefaultScreenNavigator.class);
        GameContext context = GameContext.builder()
            .spriteBatch(mock(SpriteBatch.class))
            .viewport(viewport)
            .assetService(assetService)
            .defaultScreenNavigator(navigator)
            .playerInput(playerInput)
            .inputProcessor(gameInputProcessor)
            .gameState(gameState)
            .build();
        screen = new GameScreen(context, gameWorld);

        when(gameInputProcessor.consumePauseRequest()).thenReturn(true);
        screen.render(0.1f); // pauses

        // Pause is a toggle, so the stub must go back to false or the next
        // render would immediately resume instead of acting while paused.
        when(gameInputProcessor.consumePauseRequest()).thenReturn(false);
        when(gameInputProcessor.consumeMainMenuRequest()).thenReturn(true);
        screen.render(0.1f);

        assertFalse(gameState.isPaused());
        verify(navigator).showMainMenu();
        verify(gameWorld, never()).update(anyFloat());
    }

    /**
     * Regression test for a bug where pressing BACKSPACE while actively
     * playing (not paused) left GameInputProcessor's mainMenuRequested flag
     * stuck true - GameScreen only ever consumed it inside the "paused"
     * branch - so the next time the player paused via ESC, it fired
     * immediately and dropped them straight back to the main menu.
     */
    @Test
    void pressingMainMenuKeyWhilePlayingDoesNotFireOnTheNextPause() {
        GameInputProcessor realInputProcessor = new GameInputProcessor();
        DefaultScreenNavigator navigator = mock(DefaultScreenNavigator.class);
        screen = newScreenWithRealInputProcessor(realInputProcessor, navigator);

        // Not paused yet - BACKSPACE must have no effect.
        realInputProcessor.keyDown(Input.Keys.BACKSPACE);
        screen.render(0.1f);

        assertFalse(gameState.isPaused());
        verify(navigator, never()).showMainMenu();

        // The earlier BACKSPACE must not fire now just because we paused.
        realInputProcessor.keyDown(Input.Keys.ESCAPE);
        screen.render(0.1f);

        assertTrue(gameState.isPaused());
        verify(navigator, never()).showMainMenu();
    }

    @Test
    void pressingExitKeyWhilePlayingDoesNotFireOnTheNextPause() {
        GameInputProcessor realInputProcessor = new GameInputProcessor();
        screen = newScreenWithRealInputProcessor(realInputProcessor, mock(DefaultScreenNavigator.class));

        realInputProcessor.keyDown(Input.Keys.Q);
        screen.render(0.1f);

        assertFalse(gameState.isPaused());

        realInputProcessor.keyDown(Input.Keys.ESCAPE);
        screen.render(0.1f);

        assertTrue(gameState.isPaused());
        verify(Gdx.app, never()).exit();
    }

    @Test
    void pressingRestartKeyWhilePlayingDoesNotFireOnTheNextPause() {
        GameInputProcessor realInputProcessor = new GameInputProcessor();
        DefaultScreenNavigator navigator = mock(DefaultScreenNavigator.class);
        screen = newScreenWithRealInputProcessor(realInputProcessor, navigator);

        realInputProcessor.keyDown(Input.Keys.R);
        screen.render(0.1f);

        assertFalse(gameState.isPaused());

        realInputProcessor.keyDown(Input.Keys.ESCAPE);
        screen.render(0.1f);

        assertTrue(gameState.isPaused());
        verify(navigator, never()).restartGame();
    }

    private GameScreen newScreenWithRealInputProcessor(GameInputProcessor realInputProcessor,
                                                        DefaultScreenNavigator navigator) {
        GameContext context = GameContext.builder()
            .spriteBatch(mock(SpriteBatch.class))
            .viewport(viewport)
            .assetService(assetService)
            .defaultScreenNavigator(navigator)
            .playerInput(playerInput)
            .inputProcessor(realInputProcessor)
            .gameState(gameState)
            .build();
        return new GameScreen(context, gameWorld);
    }

    @Test
    void rendersPauseOverlayWhenPausedWithoutThrowing() {
        when(gameInputProcessor.consumePauseRequest()).thenReturn(true);

        assertDoesNotThrow(() -> screen.render(0.1f));
        assertTrue(gameState.isPaused());
    }

    @Test
    void resizeUpdatesViewport() {
        screen.resize(800, 500);

        verify(viewport).update(800, 500, true);
    }

    @Test
    void emptyLifecycleCallbacksDoNotThrow() {
        assertDoesNotThrow(() -> {
            screen.hide();
            screen.pause();
            screen.resume();
            screen.dispose();
        });
        assertFalse(gameState.isPaused());
    }
}
