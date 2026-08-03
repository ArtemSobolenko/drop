package com.artem.drop.service;

import com.artem.drop.Drop;
import com.artem.drop.screen.GameScreen;
import com.artem.drop.screen.MainMenuScreen;

public class DefaultScreenNavigator implements ScreenNavigator {

    private final Drop game;

    public DefaultScreenNavigator(Drop game) {
        this.game = game;
    }

    @Override
    public void showMainMenu() {
        game.setScreen(new MainMenuScreen(game.getContext()));
    }

    @Override
    public void showGame() {
        game.setScreen(new GameScreen(game.getContext()));
    }
}
