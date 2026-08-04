package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.entity.Bucket;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.artem.drop.support.GdxTestEnvironment;
import com.artem.drop.world.GameWorld;
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
    void whenNotPausedUpdatesWorldWithFrameDelta() {
        screen.render(0.1f);

        verify(gameWorld).update(0.1f);
    }

    @Test
    void pausePressTogglesPauseAndSkipsWorldUpdate() {
        when(playerInput.isPausePressed()).thenReturn(true);

        screen.render(0.1f);

        assertTrue(gameState.isPaused());
        verify(gameWorld, never()).update(anyFloat());
    }

    @Test
    void pausingMidGamePausesMusic() {
        when(playerInput.isPausePressed()).thenReturn(true);

        screen.render(0.1f);

        verify(music).pause();
    }

    @Test
    void unpausingResumesMusic() {
        // Pause is a toggle (ESC again resumes), so pressing it on two
        // consecutive frames pauses then immediately resumes.
        when(playerInput.isPausePressed()).thenReturn(true);

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
    void rendersPauseOverlayWhenPausedWithoutThrowing() {
        when(playerInput.isPausePressed()).thenReturn(true);

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
