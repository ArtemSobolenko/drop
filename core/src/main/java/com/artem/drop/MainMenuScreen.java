package com.artem.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    private final Drop game;

    private final AssetService assetService;

    public MainMenuScreen(final Drop game, AssetService assetService) {
        this.game = game;
        this.assetService = assetService;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.YELLOW);

        assetService.loadAllAssets();

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        //draw text. Remember that x and y are in meters
        game.batch.draw(assetService.getMainBackgroundTexture(), 0, 0, 8, 5);
        game.font.draw(game.batch, "Welcome to Drop Game!!! ", 1, 1.5f);
        game.font.draw(game.batch, "Tap anywhere to begin!", 1, 1);

        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game, assetService));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        assetService.unloadMainBackground();
        Gdx.app.log("Game", "Main background disposed.");
    }
}
