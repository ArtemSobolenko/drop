package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.input.MainMenuInputProcessor;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.artem.drop.support.GdxTestEnvironment;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Drives MainMenuScreen with a real (font/asset-loaded) AssetService and a
 * mocked FitViewport/SpriteBatch/DefaultScreenNavigator/PlayerInput, so only
 * MainMenuScreen's own logic is under test - not actual pixel rendering
 * (verified separately by running the app).
 */
class MainMenuScreenTest {

    private AssetService assetService;
    private FitViewport viewport;
    private PlayerInput playerInput;
    private MainMenuInputProcessor inputProcessor;
    private DefaultScreenNavigator navigator;
    private Music mainMenuMusic;
    private MainMenuScreen screen;

    @BeforeEach
    void setUp() {
        GdxTestEnvironment.install();
        assetService = GdxTestEnvironment.loadRealAssetService();
        mainMenuMusic = assetService.getMainMenuMusic();

        viewport = mock(FitViewport.class);
        when(viewport.getCamera()).thenReturn(new OrthographicCamera());

        playerInput = mock(PlayerInput.class);
        inputProcessor = mock(MainMenuInputProcessor.class);

        navigator = mock(DefaultScreenNavigator.class);

        GameContext context = GameContext.builder()
            .spriteBatch(mock(SpriteBatch.class))
            .viewport(viewport)
            .assetService(assetService)
            .defaultScreenNavigator(navigator)
            .playerInput(playerInput)
            .mainMenuInputProcessor(inputProcessor)
            .gameState(new GameState())
            .build();

        screen = new MainMenuScreen(context);
    }

    @AfterEach
    void tearDown() {
        GdxTestEnvironment.uninstall();
    }

    @Test
    void showStartsMainMenuMusic() {
        screen.show();

        verify(mainMenuMusic).play();
    }

    @Test
    void showActivatesMainMenuInputProcessor() {
        screen.show();

        verify(Gdx.input).setInputProcessor(inputProcessor);
    }

    @Test
    void renderWithoutTouchDoesNotNavigate() {
        when(playerInput.isTouched()).thenReturn(false);

        assertDoesNotThrow(() -> screen.render(0.1f));

        verify(navigator, never()).showGame();
    }

    @Test
    void renderWithoutConsumeStartGameRequestDoesNotNavigate() {
        when(inputProcessor.consumeStartGameRequest()).thenReturn(false);

        assertDoesNotThrow(() -> screen.render(0.1f));

        verify(navigator, never()).showGame();
    }

    @Test
    void renderWhenTouchedNavigatesToGameScreen() {
        when(playerInput.isTouched()).thenReturn(true);

        screen.render(0.1f);

        verify(navigator).showGame();
    }

    @Test
    void renderWhenConsumeStartGameRequestNavigatesToGameScreen() {
        when(inputProcessor.consumeStartGameRequest()).thenReturn(true);

        screen.render(0.1f);

        verify(navigator).showGame();
    }

    @Test
    void hideUnloadsMenuOnlyAssetsSoTheyBecomeUnavailable() {
        screen.hide();

        assertThrows(RuntimeException.class, assetService::getMainBackgroundTexture);
    }

    @Test
    void resizeUpdatesViewport() {
        screen.resize(800, 500);

        verify(viewport).update(800, 500, true);
    }

    @Test
    void emptyLifecycleCallbacksDoNotThrow() {
        assertDoesNotThrow(() -> {
            screen.pause();
            screen.resume();
            screen.dispose();
        });
    }
}
