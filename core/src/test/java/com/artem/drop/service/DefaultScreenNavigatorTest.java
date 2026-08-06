package com.artem.drop.service;

import com.artem.drop.DropGame;
import com.artem.drop.context.GameContext;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.screen.GameScreen;
import com.artem.drop.screen.MainMenuScreen;
import com.artem.drop.screen.RainIntroScreen;
import com.artem.drop.state.GameState;
import com.artem.drop.support.GdxTestEnvironment;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Wires a real GameContext/DefaultScreenNavigator/screen graph together (only
 * the leaf rendering/asset dependencies are mocked) to verify navigation
 * actually swaps in the right screen type.
 */
class DefaultScreenNavigatorTest {

    private DropGame game;

    @BeforeEach
    void setUp() {
        AssetService assetService = mock(AssetService.class);
        when(assetService.getBucketTexture()).thenReturn(mock(Texture.class));

        game = mock(DropGame.class);
        DefaultScreenNavigator navigator = new DefaultScreenNavigator(game);

        GameContext context = GameContext.builder()
            .spriteBatch(mock(SpriteBatch.class))
            .viewport(mock(FitViewport.class))
            .assetService(assetService)
            .defaultScreenNavigator(navigator)
            .playerInput(mock(PlayerInput.class))
            .gameState(new GameState())
            .build();

        when(game.getContext()).thenReturn(context);
    }

    @Test
    void showMainMenuSwitchesToMainMenuScreen() {
        game.getContext().defaultScreenNavigator().showMainMenu();

        ArgumentCaptor<Screen> captor = ArgumentCaptor.forClass(Screen.class);
        verify(game).setScreen(captor.capture());
        assertInstanceOf(MainMenuScreen.class, captor.getValue());
    }

    @Test
    void showRainIntroSwitchesToRainIntroScreen() {
        game.getContext().defaultScreenNavigator().showRainIntro();

        ArgumentCaptor<Screen> captor = ArgumentCaptor.forClass(Screen.class);
        verify(game).setScreen(captor.capture());
        assertInstanceOf(RainIntroScreen.class, captor.getValue());
    }

    @Test
    void showGameSwitchesToGameScreen() {
        game.getContext().defaultScreenNavigator().showGame();

        ArgumentCaptor<Screen> captor = ArgumentCaptor.forClass(Screen.class);
        verify(game).setScreen(captor.capture());
        assertInstanceOf(GameScreen.class, captor.getValue());
    }

    @Test
    void restartGameSwitchesToAFreshGameScreen() {
        game.getContext().defaultScreenNavigator().restartGame();

        ArgumentCaptor<Screen> captor = ArgumentCaptor.forClass(Screen.class);
        verify(game).setScreen(captor.capture());
        assertInstanceOf(GameScreen.class, captor.getValue());
    }

    /**
     * Regression test for a crash where returning to MainMenuScreen after
     * gameplay started (e.g. via the pause menu's "back to main menu" option)
     * threw GdxRuntimeException: Asset not loaded: music/main_menu.mp3.
     * MainMenuScreen.hide() unloads menu-only assets once gameplay begins, and
     * nothing reloaded them before MainMenuScreen.show() ran again. Unlike the
     * other tests in this class, this one uses a real AssetService and a real
     * (non-mocked) DropGame subclass so that game.setScreen() actually invokes
     * the new screen's show()/resize() - exactly the path that crashed.
     */
    @Test
    void showMainMenuAfterGameplayReloadsPreviouslyUnloadedMenuAssets() {
        GdxTestEnvironment.install();
        try {
            AssetService realAssetService = GdxTestEnvironment.loadRealAssetService();

            // Mirrors what MainMenuScreen.hide() does once gameplay starts.
            realAssetService.unloadMainBackground();
            realAssetService.unloadMainMenuMusic();

            RealScreenSwitchingDropGame realGame = new RealScreenSwitchingDropGame();
            DefaultScreenNavigator navigator = new DefaultScreenNavigator(realGame);

            GameContext context = GameContext.builder()
                .spriteBatch(mock(SpriteBatch.class))
                .viewport(mock(FitViewport.class))
                .assetService(realAssetService)
                .defaultScreenNavigator(navigator)
                .playerInput(mock(PlayerInput.class))
                .gameState(new GameState())
                .build();
            realGame.useContext(context);

            assertDoesNotThrow(navigator::showMainMenu);
        } finally {
            GdxTestEnvironment.uninstall();
        }
    }

    /**
     * A real (non-mocked) DropGame so Game.setScreen() really calls the new
     * screen's show()/resize(), with getContext() swapped out for a
     * test-controlled GameContext (DropGame only populates it via create()).
     */
    private static final class RealScreenSwitchingDropGame extends DropGame {
        private GameContext testContext;

        void useContext(GameContext context) {
            this.testContext = context;
        }

        @Override
        public GameContext getContext() {
            return testContext;
        }
    }
}
