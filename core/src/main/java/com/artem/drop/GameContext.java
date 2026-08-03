package com.artem.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public record GameContext(SpriteBatch spriteBatch,
                          BitmapFont bitmapFont,
                          FitViewport viewport,
                          AssetService assetService,
                          DefaultScreenNavigator defaultScreenNavigator) {

    public void disposeAllContext() {
        spriteBatch.dispose();
        bitmapFont.dispose();
        assetService.disposeAllAssets();
        Gdx.app.log("Game", "Game Context disposed.");
    }
}
