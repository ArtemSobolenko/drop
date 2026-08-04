package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.extern.slf4j.Slf4j;

import static com.artem.drop.GameConstants.MAIN_MENU_SUB_TITLE_TEXT;
import static com.artem.drop.GameConstants.MAIN_MENU_TITLE_TEXT;

@Slf4j
public class MainMenuScreen implements Screen {

    private final GameContext gameContext;
    private final FitViewport viewport;
    private final AssetService assetService;
    private final PlayerInput playerInput;
    private final GlyphLayout glyphLayout;

    public MainMenuScreen(GameContext gameContext) {
        this.gameContext = gameContext;
        this.viewport = gameContext.viewport();
        this.glyphLayout = new GlyphLayout();
        this.assetService = gameContext.assetService();
        this.playerInput = gameContext.playerInput();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.YELLOW);

        SpriteBatch spriteBatch = gameContext.spriteBatch();

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        drawMenu(spriteBatch);

        spriteBatch.end();

        if (playerInput.isTouched()) {
            gameContext.defaultScreenNavigator().showGame();
            log.info("Game Screen Loaded.");
            log.info("Game started.");
        }
    }

    private void drawMenu(SpriteBatch spriteBatch) {

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(assetService.getMainBackgroundTexture(), 0, 0, worldWidth, worldHeight);

        BitmapFont font = assetService.getMenuFont();

        drawCenteredText(spriteBatch, font, MAIN_MENU_TITLE_TEXT, worldWidth, worldHeight * 0.35f);
        drawCenteredText(spriteBatch, font, MAIN_MENU_SUB_TITLE_TEXT, worldWidth, worldHeight * 0.20f);
    }

    private void drawCenteredText(SpriteBatch spriteBatch, BitmapFont font, String text,
                                  float worldWidth, float y) {
        glyphLayout.setText(font, text);
        font.draw(spriteBatch, glyphLayout, (worldWidth - glyphLayout.width) / 2f, y);
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
