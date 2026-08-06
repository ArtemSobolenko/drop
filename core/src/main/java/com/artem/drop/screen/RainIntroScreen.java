package com.artem.drop.screen;

import com.artem.drop.context.GameContext;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.extern.slf4j.Slf4j;

import static com.artem.drop.GameConstants.RAIN_INTRO_DURATION_SECONDS;
import static com.artem.drop.GameConstants.RAIN_INTRO_TEXT;
import static com.artem.drop.GameConstants.RAIN_INTRO_TEXT_MARGIN;

/**
 * Brief black transition screen shown between MainMenuScreen and GameScreen.
 * No input processor is active while it's showing, so it cannot be skipped -
 * it always advances to GameScreen on its own after RAIN_INTRO_DURATION_SECONDS.
 */
@Slf4j
public class RainIntroScreen implements Screen {

    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetService assetService;
    private final DefaultScreenNavigator screenNavigator;
    private final GlyphLayout glyphLayout;

    private float elapsedTime = 0f;
    private boolean navigatedToGame = false;

    public RainIntroScreen(GameContext gameContext) {
        this.viewport = gameContext.viewport();
        this.spriteBatch = gameContext.spriteBatch();
        this.assetService = gameContext.assetService();
        this.screenNavigator = gameContext.defaultScreenNavigator();
        this.glyphLayout = new GlyphLayout();
    }

    @Override
    public void show() {
        // No input processor is active, so nothing the player presses/touches
        // can skip this screen early.
        Gdx.input.setInputProcessor(null);
        elapsedTime = 0f;
        navigatedToGame = false;

        assetService.getThundeIntroSound().play();
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        viewport.apply();
        ScreenUtils.clear(Color.BLACK);

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        drawText();
        spriteBatch.end();

        if (!navigatedToGame && elapsedTime >= RAIN_INTRO_DURATION_SECONDS) {
            navigatedToGame = true;
            screenNavigator.showGame();
            log.info("Game Screen Loaded.");
            log.info("Game started.");
        }
    }

    private void drawText() {
        BitmapFont font = assetService.getHudFont();
        glyphLayout.setText(font, RAIN_INTRO_TEXT);

        font.draw(spriteBatch, glyphLayout, RAIN_INTRO_TEXT_MARGIN, RAIN_INTRO_TEXT_MARGIN + glyphLayout.height);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
    }
}
