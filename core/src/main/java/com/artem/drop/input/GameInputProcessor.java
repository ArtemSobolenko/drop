package com.artem.drop.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {

    private boolean pauseRequested;
    private boolean startGameRequested;
    private boolean gameExitRequested;
    private boolean gameRestartRequested;

    @Override
    public boolean keyDown(int keycode) {
        return switch (keycode) {
            case Input.Keys.ESCAPE -> {
                pauseRequested = true;
                yield true;
            }
            case Input.Keys.SPACE -> {
                startGameRequested = true;
                yield true;
            }
            case Input.Keys.Q -> {
                gameExitRequested = true;
                yield true;
            }
            case Input.Keys.R -> {
                gameRestartRequested = true;
                yield true;
            }
            default -> false;
        };
    }

    public boolean consumePauseRequest() {
        if (!pauseRequested) {
            return false;
        }
        pauseRequested = false;
        return true;
    }

    public boolean consumeStartGameRequest() {
        boolean result = startGameRequested;
        startGameRequested = false;
        return result;
    }

    public boolean consumeGameExitRequest() {
        boolean result = gameExitRequested;
        gameExitRequested = false;
        return result;
    }

    public boolean consumeGameRestartRequest() {
        boolean result = gameRestartRequested;
        gameRestartRequested = false;
        return result;
    }
}
