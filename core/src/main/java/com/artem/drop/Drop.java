package com.artem.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Drop extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;
    private final AssetService assetService;

    public Drop() {
        this.assetService = new AssetService();
    }

    public void create() {

        batch = new SpriteBatch();

        // use libGDX's default font
        font = new BitmapFont();

        viewport = new FitViewport(8, 5);

        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        //or with FreeTypeFontGenerator

        assetService.loadAllAssets();

        this.setScreen(new MainMenuScreen(this, assetService));

        Gdx.app.log("Game", "Game started.");
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

}
