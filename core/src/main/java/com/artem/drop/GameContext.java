package com.artem.drop;

import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.Builder;

@Builder
public record GameContext(SpriteBatch spriteBatch,
                          BitmapFont bitmapFont,
                          FitViewport viewport,
                          AssetService assetService,
                          DefaultScreenNavigator defaultScreenNavigator,
                          DesktopPlayerInput desktopPlayerInput) {

    public void disposeAll() {
        spriteBatch.dispose();
        bitmapFont.dispose();
        assetService.disposeAllAssets();
        Gdx.app.log("Game", "Game Context disposed.");
    }
}
