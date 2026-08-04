package com.artem.drop.context;

import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GameContextTest {

    private GameContext newContext(SpriteBatch spriteBatch, AssetService assetService) {
        return GameContext.builder()
            .spriteBatch(spriteBatch)
            .viewport(mock(FitViewport.class))
            .assetService(assetService)
            .defaultScreenNavigator(mock(DefaultScreenNavigator.class))
            .playerInput(mock(PlayerInput.class))
            .gameState(new GameState())
            .build();
    }

    @Test
    void disposeAllDisposesSpriteBatchAndAssetService() {
        SpriteBatch spriteBatch = mock(SpriteBatch.class);
        AssetService assetService = mock(AssetService.class);
        GameContext context = newContext(spriteBatch, assetService);

        context.disposeAll();

        verify(spriteBatch).dispose();
        verify(assetService).disposeAll();
    }

    @Test
    void recordAccessorsAndGeneratedMethodsWork() {
        SpriteBatch spriteBatch = mock(SpriteBatch.class);
        AssetService assetService = mock(AssetService.class);
        GameContext context = newContext(spriteBatch, assetService);

        assertEquals(spriteBatch, context.spriteBatch());
        assertEquals(assetService, context.assetService());
        assertEquals(context, context);
        assertNotNull(context.toString());
        assertEquals(context.hashCode(), context.hashCode());
    }
}
