package com.artem.drop.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {

    private boolean pauseRequested;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            pauseRequested = true;
            return true;
        }

        return false;
    }

    public boolean consumePauseRequest() {
        if (!pauseRequested) {
            return false;
        }

        pauseRequested = false;
        return true;
    }
}
