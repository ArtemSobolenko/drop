package com.artem.drop.service;

import com.artem.drop.DropGame;
import com.artem.drop.context.GameContext;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.screen.GameScreen;
import com.artem.drop.screen.MainMenuScreen;
import com.artem.drop.state.GameState;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
}
