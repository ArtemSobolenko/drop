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
            Input.Keys.Q
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
}
