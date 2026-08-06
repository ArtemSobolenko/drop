package com.artem.drop.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {

    private boolean pauseRequested;
    private boolean gameExitRequested;
    private boolean gameRestartRequested;
    private boolean mainMenuRequested;
    private boolean playerJumpRequested;

    @Override
    public boolean keyDown(int keycode) {
        return switch (keycode) {
            case Input.Keys.ESCAPE -> {
                pauseRequested = true;
                yield true;
            }
            case Input.Keys.SPACE -> {
                playerJumpRequested = true;
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
            case Input.Keys.BACKSPACE -> {
                mainMenuRequested = true;
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

    public boolean consumeMainMenuRequest() {
        boolean result = mainMenuRequested;
        mainMenuRequested = false;
        return result;
    }

    public boolean consumePlayerJumpRequest() {
        boolean result = playerJumpRequested;
        playerJumpRequested = false;
        return result;
    }

    /**
     * Clears exit/restart/main-menu requests without acting on them. These
     * keys only make sense from the pause menu, but keyDown() sets their flag
     * the instant the key is pressed regardless of pause state. Without this,
     * pressing e.g. BACKSPACE while actively playing left mainMenuRequested
     * stuck true, and the next time the player paused it fired immediately -
     * as if they'd pressed BACKSPACE again from the menu they hadn't reached yet.
     */
    public void discardPauseMenuRequests() {
        gameExitRequested = false;
        gameRestartRequested = false;
        mainMenuRequested = false;
    }
}
