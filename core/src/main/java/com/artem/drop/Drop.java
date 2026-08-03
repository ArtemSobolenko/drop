package com.artem.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Drop extends Game {

    private GameContext context;

    public void create() {

        // use libGDX's default font
        BitmapFont font = new BitmapFont();

        FitViewport viewport = new FitViewport(8, 5);

        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        AssetService assets = new AssetService();
        assets.loadAllAssets();

        //or with FreeTypeFontGenerator

        context = new GameContext(new SpriteBatch(), font, viewport, assets);

        this.setScreen(new MainMenuScreen(this, context));

        Gdx.app.log("Game", "Game started.");
    }

    public GameContext getContext() {
        return context;
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        context.spriteBatch().dispose();
        context.bitmapFont().dispose();
        context.assetService().disposeAllAssets();
    }

}
