package com.artem.drop;

import com.artem.drop.context.GameContext;
import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.artem.drop.GameConstants.WORLD_HEIGHT;
import static com.artem.drop.GameConstants.WORLD_WIDTH;

//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

@Slf4j
@Getter
public class DropGame extends Game {

    private GameContext context;

    public void create() {

        log.info("Game created.");

        // use libGDX's default font
        BitmapFont font = new BitmapFont();

        FitViewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        AssetService assets = new AssetService();
        assets.loadAllAssets();
        log.info("All assets loaded.");

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
            .gameState(new GameState())
            .build();

        defaultScreenNavigator.showMainMenu();
        log.info("Main Menu Loaded.");
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        context.disposeAll();
        log.info("Game Context disposed.");
    }

}
