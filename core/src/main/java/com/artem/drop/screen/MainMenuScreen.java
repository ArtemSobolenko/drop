package com.artem.drop.screen;

import com.artem.drop.GameContext;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainMenuScreen implements Screen {

    private final GameContext gameContext;
    private final FitViewport viewport;
    private final AssetService assetService;
    private final PlayerInput playerInput;

    public MainMenuScreen(GameContext gameContext) {
        this.gameContext = gameContext;
        this.viewport = gameContext.viewport();
        this.assetService = gameContext.assetService();
        this.playerInput = gameContext.desktopPlayerInput();
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

        if (playerInput.isTouched()) {
            gameContext.defaultScreenNavigator().showGame();
            log.info("Game Screen Loaded.");
            log.info("Game started.");
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        assetService.getConfiguredMainMenuMusic().play();
    }

    @Override
    public void hide() {
        assetService.unloadMainBackground();
        //  assetService.getConfiguredMainMenuMusic().stop();
        assetService.unloadMainMenuMusic();
        log.info("Main background disposed.");
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
