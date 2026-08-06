package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.input.MainMenuInputProcessor;
import com.artem.drop.input.PlayerInput;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.extern.slf4j.Slf4j;

import static com.artem.drop.GameConstants.MAIN_MENU_SUBTITLE_Y_FRACTION;
import static com.artem.drop.GameConstants.MAIN_MENU_SUB_TITLE_TEXT;
import static com.artem.drop.GameConstants.MAIN_MENU_TITLE_TEXT;
import static com.artem.drop.GameConstants.MAIN_MENU_TITLE_Y_FRACTION;

@Slf4j
public class MainMenuScreen implements Screen {

    private final GameContext gameContext;
    private final FitViewport viewport;
    private final AssetService assetService;
    private final PlayerInput playerInput;
    private final MainMenuInputProcessor inputProcessor;
    private final GlyphLayout glyphLayout;

    public MainMenuScreen(GameContext gameContext) {
        this.gameContext = gameContext;
        this.viewport = gameContext.viewport();
        this.glyphLayout = new GlyphLayout();
        this.assetService = gameContext.assetService();
        this.playerInput = gameContext.playerInput();
        this.inputProcessor = gameContext.mainMenuInputProcessor();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLUE);

        SpriteBatch spriteBatch = gameContext.spriteBatch();

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        drawMenu(spriteBatch);

        spriteBatch.end();

        if (handleStartGameRequest()) {
            gameContext.defaultScreenNavigator().showRainIntro();
            log.info("Rain intro screen loaded.");
        }
    }

    private void drawMenu(SpriteBatch spriteBatch) {

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(assetService.getMainBackgroundTexture(), 0, 0, worldWidth, worldHeight);

        BitmapFont font = assetService.getMenuFont();

        drawCenteredText(spriteBatch, font, MAIN_MENU_TITLE_TEXT, worldWidth, worldHeight * MAIN_MENU_TITLE_Y_FRACTION);
        drawCenteredText(spriteBatch, font, MAIN_MENU_SUB_TITLE_TEXT, worldWidth, worldHeight * MAIN_MENU_SUBTITLE_Y_FRACTION);
    }

    private void drawCenteredText(SpriteBatch spriteBatch, BitmapFont font, String text,
                                  float worldWidth, float y) {
        glyphLayout.setText(font, text);
        font.draw(spriteBatch, glyphLayout, (worldWidth - glyphLayout.width) / 2f, y);
    }

    private boolean handleStartGameRequest() {
        return playerInput.isTouched() || inputProcessor.consumeStartGameRequest();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputProcessor);
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
