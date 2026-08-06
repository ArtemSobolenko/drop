package com.artem.drop.input;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameInputProcessorTest {

    private GameInputProcessor inputProcessor;

    @BeforeEach
    void setUp() {
        inputProcessor = new GameInputProcessor();
    }

    @ParameterizedTest
    @MethodSource("handledKeys")
    void keyDownShouldReturnTrueForHandledKeys(int keyCode) {
        assertTrue(inputProcessor.keyDown(keyCode));
    }

    static Stream<Integer> handledKeys() {
        return Stream.of(
            Input.Keys.ESCAPE,
            Input.Keys.SPACE,
            Input.Keys.Q,
            Input.Keys.R,
            Input.Keys.BACKSPACE
        );
    }

    @ParameterizedTest
    @MethodSource("unhandledKeys")
    void keyDownShouldReturnFalseForOtherKeys(int keyCode) {
        assertFalse(inputProcessor.keyDown(keyCode));
    }

    static Stream<Integer> unhandledKeys() {
        return Stream.of(
            Input.Keys.A,
            Input.Keys.B,
            Input.Keys.F1,
            Input.Keys.TAB
        );
    }

    @Test
    void shouldRequestPauseWhenEscapeIsPressed() {
        inputProcessor.keyDown(Input.Keys.ESCAPE);

        assertTrue(inputProcessor.consumePauseRequest());
    }

    @Test
    void shouldNotRequestPauseWhenSpaceIsPressed() {
        inputProcessor.keyDown(Input.Keys.SPACE);

        assertFalse(inputProcessor.consumePauseRequest());
    }

    @Test
    void shouldConsumePauseRequestOnlyOnce() {
        inputProcessor.keyDown(Input.Keys.ESCAPE);

        assertTrue(inputProcessor.consumePauseRequest());
        assertFalse(inputProcessor.consumePauseRequest());
    }

    @Test
    void shouldRequestMainMenuWhenBackspaceIsPressed() {
        inputProcessor.keyDown(Input.Keys.BACKSPACE);

        assertTrue(inputProcessor.consumeMainMenuRequest());
    }

    @Test
    void shouldNotRequestMainMenuWhenSpaceIsPressed() {
        inputProcessor.keyDown(Input.Keys.SPACE);

        assertFalse(inputProcessor.consumeMainMenuRequest());
    }

    @Test
    void shouldConsumeMainMenuRequestOnlyOnce() {
        inputProcessor.keyDown(Input.Keys.BACKSPACE);

        assertTrue(inputProcessor.consumeMainMenuRequest());
        assertFalse(inputProcessor.consumeMainMenuRequest());
    }

    @Test
    void shouldRequestPlayerJumpWhenSpaceIsPressed() {
        inputProcessor.keyDown(Input.Keys.SPACE);

        assertTrue(inputProcessor.consumePlayerJumpRequest());
    }

    @Test
    void shouldNotRequestPlayerJumpWhenEscapeIsPressed() {
        inputProcessor.keyDown(Input.Keys.ESCAPE);

        assertFalse(inputProcessor.consumePlayerJumpRequest());
    }

    @Test
    void shouldConsumePlayerJumpRequestOnlyOnce() {
        inputProcessor.keyDown(Input.Keys.SPACE);

        assertTrue(inputProcessor.consumePlayerJumpRequest());
        assertFalse(inputProcessor.consumePlayerJumpRequest());
    }

    @Test
    void shouldRequestGameExitWhenQIsPressed() {
        inputProcessor.keyDown(Input.Keys.Q);

        assertTrue(inputProcessor.consumeGameExitRequest());
    }

    @Test
    void shouldRequestGameRestartWhenRIsPressed() {
        inputProcessor.keyDown(Input.Keys.R);

        assertTrue(inputProcessor.consumeGameRestartRequest());
    }

    /**
     * Regression test for a bug where pressing Q/R/BACKSPACE while actively
     * playing (not paused) left the corresponding flag stuck true - since
     * GameScreen only ever consumed these inside its "paused" branch - so the
     * next time the player paused, the stale request fired immediately as if
     * they'd pressed the key again from the pause menu.
     */
    @Test
    void discardPauseMenuRequestsClearsExitRestartAndMainMenuFlags() {
        inputProcessor.keyDown(Input.Keys.Q);
        inputProcessor.keyDown(Input.Keys.R);
        inputProcessor.keyDown(Input.Keys.BACKSPACE);

        inputProcessor.discardPauseMenuRequests();

        assertFalse(inputProcessor.consumeGameExitRequest());
        assertFalse(inputProcessor.consumeGameRestartRequest());
        assertFalse(inputProcessor.consumeMainMenuRequest());
    }

    @Test
    void discardPauseMenuRequestsDoesNotAffectPauseOrJumpFlags() {
        inputProcessor.keyDown(Input.Keys.ESCAPE);
        inputProcessor.keyDown(Input.Keys.SPACE);

        inputProcessor.discardPauseMenuRequests();

        assertTrue(inputProcessor.consumePauseRequest());
        assertTrue(inputProcessor.consumePlayerJumpRequest());
    }
}
