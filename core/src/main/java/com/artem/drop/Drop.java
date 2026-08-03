package com.artem.drop;

import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.Getter;

//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

@Getter
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

        DefaultScreenNavigator defaultScreenNavigator
            = new DefaultScreenNavigator(this);

        DesktopPlayerInput desktopPlayerInput = new DesktopPlayerInput();

        context = GameContext.builder()
            .spriteBatch(new SpriteBatch())
            .bitmapFont(font)
            .viewport(viewport)
            .assetService(assets)
            .defaultScreenNavigator(defaultScreenNavigator)
            .desktopPlayerInput(desktopPlayerInput)
            .build();

        defaultScreenNavigator.showMainMenu();

        Gdx.app.log("Game", "Game started.");
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        context.disposeAll();
    }

}
