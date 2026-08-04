package com.artem.drop.context;

import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public record GameContext(SpriteBatch spriteBatch,
                          BitmapFont bitmapFont,
                          FitViewport viewport,
                          AssetService assetService,
                          DefaultScreenNavigator defaultScreenNavigator,
                          DesktopPlayerInput desktopPlayerInput,
                          GameState gameState) {

    public void disposeAll() {
        spriteBatch.dispose();
        bitmapFont.dispose();
        assetService.disposeAllAssets();
    }
}
