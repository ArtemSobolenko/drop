package com.artem.drop.service;

import com.artem.drop.DropGame;
import com.artem.drop.context.GameContext;
import com.artem.drop.screen.GameScreen;
import com.artem.drop.screen.MainMenuScreen;
import com.artem.drop.world.GameWorld;

public class DefaultScreenNavigator implements ScreenNavigator {

    private final DropGame game;

    public DefaultScreenNavigator(DropGame game) {
        this.game = game;
    }

    @Override
    public void showMainMenu() {
        GameContext context = game.getContext();
        context.assetService().ensureMainMenuAssetsLoaded();
        game.setScreen(new MainMenuScreen(context));
    }

    @Override
    public void showGame() {
        showFreshGameScreen();
    }

    @Override
    public void restartGame() {
        showFreshGameScreen();
    }

    private void showFreshGameScreen() {
        GameContext context = game.getContext();
        GameWorld gameWorld = new GameWorld(context.assetService());
        game.setScreen(new GameScreen(context, gameWorld));
    }
}
