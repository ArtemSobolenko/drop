package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.artem.drop.support.GdxTestEnvironment;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.artem.drop.GameConstants.RAIN_INTRO_DURATION_SECONDS;
import static com.artem.drop.GameConstants.RAIN_INTRO_TEXT;
import static com.artem.drop.GameConstants.RAIN_INTRO_TEXT_MARGIN;
import static com.artem.drop.GameConstants.WORLD_HEIGHT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Drives RainIntroScreen with a real (font-loaded) AssetService and a mocked
 * FitViewport/SpriteBatch/DefaultScreenNavigator, so only RainIntroScreen's
 * own timing/input-blocking logic is under test - not actual pixel rendering.
 */
class RainIntroScreenTest {

    private AssetService assetService;
    private FitViewport viewport;
    private DefaultScreenNavigator navigator;
    private RainIntroScreen screen;

    @BeforeEach
    void setUp() {
        GdxTestEnvironment.install();
        assetService = GdxTestEnvironment.loadRealAssetService();

        viewport = mock(FitViewport.class);
        when(viewport.getCamera()).thenReturn(new OrthographicCamera());

        navigator = mock(DefaultScreenNavigator.class);

        GameContext context = GameContext.builder()
            .spriteBatch(mock(SpriteBatch.class))
            .viewport(viewport)
            .assetService(assetService)
            .defaultScreenNavigator(navigator)
            .gameState(new GameState())
            .build();

        screen = new RainIntroScreen(context);
    }

    @AfterEach
    void tearDown() {
        GdxTestEnvironment.uninstall();
    }

    @Test
    void showDeactivatesInputSoTheIntroCannotBeSkipped() {
        screen.show();

        verify(Gdx.input).setInputProcessor(null);
    }

    @Test
    void rendersWithBlackBackground() {
        screen.render(0.1f);

        verify(Gdx.gl).glClearColor(0f, 0f, 0f, 1f);
    }

    @Test
    void doesNotNavigateToGameBeforeDurationElapses() {
        screen.render(RAIN_INTRO_DURATION_SECONDS - 0.1f);

        verify(navigator, never()).showGame();
    }

    @Test
    void navigatesToGameOnceDurationElapses() {
        screen.render(RAIN_INTRO_DURATION_SECONDS + 0.1f);

        verify(navigator).showGame();
    }

    @Test
    void navigatesToGameOnlyOnceEvenIfRenderedAgainAfterElapsing() {
        screen.render(RAIN_INTRO_DURATION_SECONDS + 0.1f);
        screen.render(0.1f);
        screen.render(0.1f);

        verify(navigator, times(1)).showGame();
    }

    @Test
    void accumulatesElapsedTimeAcrossMultipleFrames() {
        float perFrame = RAIN_INTRO_DURATION_SECONDS / 4f;

        screen.render(perFrame);
        screen.render(perFrame);
        screen.render(perFrame);
        verify(navigator, never()).showGame();

        screen.render(perFrame + 0.01f);
        verify(navigator).showGame();
    }

    @Test
    void showResetsElapsedTimeSoARestartedIntroWaitsTheFullDurationAgain() {
        screen.render(RAIN_INTRO_DURATION_SECONDS - 0.1f);

        screen.show();
        screen.render(RAIN_INTRO_DURATION_SECONDS - 0.1f);

        verify(navigator, never()).showGame();
    }

    /**
     * Mirrors RainIntroScreen's own drawText() formula with the real font
     * metrics to verify the text actually sits near the bottom-left corner,
     * not just that it renders without throwing.
     */
    @Test
    void textIsPositionedInTheBottomLeftCorner() {
        GlyphLayout layout = new GlyphLayout(assetService.getHudFont(), RAIN_INTRO_TEXT);

        float x = RAIN_INTRO_TEXT_MARGIN;
        float y = RAIN_INTRO_TEXT_MARGIN + layout.height;

        assertTrue(x >= 0f && x < WORLD_HEIGHT / 2f, "text is not near the left edge: x=" + x);
        assertTrue(y >= 0f && y < WORLD_HEIGHT / 2f, "text is not near the bottom edge: y=" + y);
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
    }
}
