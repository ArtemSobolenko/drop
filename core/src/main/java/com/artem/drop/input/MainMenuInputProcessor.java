package com.artem.drop.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class MainMenuInputProcessor extends InputAdapter {

    private boolean startGameRequested;

    @Override
    public boolean keyDown(int keycode) {
        return switch (keycode) {
            case Input.Keys.SPACE -> {
                startGameRequested = true;
                yield true;
            }
            default -> false;
        };
    }

    public boolean consumeStartGameRequest() {
        boolean result = startGameRequested;
        startGameRequested = false;
        return result;
    }

}
