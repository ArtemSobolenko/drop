package com.artem.drop;

import com.badlogic.gdx.Gdx;

public class DefaultScreenNavigator implements ScreenNavigator {

    private final Drop game;

    public DefaultScreenNavigator(Drop game) {
        this.game = game;
    }

    @Override
    public void showMainMenu() {
        game.setScreen(new MainMenuScreen(game.getContext()));
        Gdx.app.log("Game", "Main Menu Loaded.");
    }

    @Override
    public void showGame() {
        game.setScreen(new GameScreen(game.getContext()));
        Gdx.app.log("Game", "Game Screen Loaded.");
    }
}
