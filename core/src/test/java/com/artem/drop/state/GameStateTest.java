package com.artem.drop.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateTest {

    @Test
    void startsUnpaused() {
        assertFalse(new GameState().isPaused());
    }

    @Test
    void pauseSetsPausedTrue() {
        GameState state = new GameState();
        state.pause();
        assertTrue(state.isPaused());
    }

    @Test
    void resumeSetsPausedFalse() {
        GameState state = new GameState();
        state.pause();
        state.resume();
        assertFalse(state.isPaused());
    }

    @Test
    void togglePauseFlipsCurrentState() {
        GameState state = new GameState();

        state.togglePause();
        assertTrue(state.isPaused());

        state.togglePause();
        assertFalse(state.isPaused());
    }
}
