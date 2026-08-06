package com.artem.drop;

import com.artem.drop.context.GameContext;
import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.input.GameInputProcessor;
import com.artem.drop.input.MainMenuInputProcessor;
import com.artem.drop.service.AssetService;
import com.artem.drop.service.DefaultScreenNavigator;
import com.artem.drop.state.GameState;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.artem.drop.GameConstants.WORLD_HEIGHT;
import static com.artem.drop.GameConstants.WORLD_WIDTH;

@Slf4j
@Getter
public class DropGame extends Game {

    private GameContext context;

    @Override
    public void create() {

        log.info("Game created.");

        FitViewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        AssetService assets = new AssetService();
        assets.loadAllAssets();
        log.info("All assets loaded.");

        DefaultScreenNavigator defaultScreenNavigator
            = new DefaultScreenNavigator(this);

        GameInputProcessor gameInputProcessor = new GameInputProcessor();
        MainMenuInputProcessor mainMenuInputProcessor = new MainMenuInputProcessor();

        // Each screen activates its own input processor in show() rather than
        // sharing one app-wide processor/multiplexer - GameInputProcessor and
        // MainMenuInputProcessor both bind SPACE to different meanings (jump
        // vs. start game), so both being permanently active at once meant
        // whichever was listed first in a multiplexer silently swallowed the
        // other's SPACE presses.
        context = GameContext.builder()
            .spriteBatch(new SpriteBatch())
            .viewport(viewport)
            .assetService(assets)
            .defaultScreenNavigator(defaultScreenNavigator)
            .playerInput(new DesktopPlayerInput())
            .inputProcessor(gameInputProcessor)
            .mainMenuInputProcessor(mainMenuInputProcessor)
            .gameState(new GameState())
            .build();

        defaultScreenNavigator.showMainMenu();
        log.info("Main Menu Loaded.");
    }

    @Override
    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        context.disposeAll();
        log.info("Game Context disposed.");
    }

}
