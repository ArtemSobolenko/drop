package com.artem.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

    private final Drop game;
    private final GameContext gameContext;
    private final FitViewport viewport;
    private final AssetService assetService;

    public MainMenuScreen(Drop game) {
        this.game = game;
        this.gameContext = game.getContext();
        this.viewport = gameContext.viewport();
        this.assetService = gameContext.assetService();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.YELLOW);

        SpriteBatch spriteBatch = gameContext.spriteBatch();
        BitmapFont bitmapFont = gameContext.bitmapFont();

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        //draw text. Remember that x and y are in meters
        spriteBatch.draw(assetService.getMainBackgroundTexture(), 0, 0, 8, 5);
        bitmapFont.draw(spriteBatch, "Welcome to Drop Game!!! ", 1, 1.5f);
        bitmapFont.draw(spriteBatch, "Tap anywhere to begin!", 1, 1);

        spriteBatch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
        assetService.unloadMainBackground();
        Gdx.app.log("Game", "Main background disposed.");
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
