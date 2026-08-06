package com.artem.drop.input;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainMenuInputProcessorTest {

    private MainMenuInputProcessor inputProcessor;

    @BeforeEach
    void setUp() {
        inputProcessor = new MainMenuInputProcessor();
    }

    @Test
    void keyDownReturnsTrueForSpace() {
        assertTrue(inputProcessor.keyDown(Input.Keys.SPACE));
    }

    @ParameterizedTest
    @MethodSource("unhandledKeys")
    void keyDownReturnsFalseForOtherKeys(int keyCode) {
        assertFalse(inputProcessor.keyDown(keyCode));
    }

    static Stream<Integer> unhandledKeys() {
        return Stream.of(
            Input.Keys.ESCAPE,
            Input.Keys.Q,
            Input.Keys.R,
            Input.Keys.BACKSPACE,
            Input.Keys.A
        );
    }

    @Test
    void shouldRequestStartGameWhenSpaceIsPressed() {
        inputProcessor.keyDown(Input.Keys.SPACE);

        assertTrue(inputProcessor.consumeStartGameRequest());
    }

    @Test
    void shouldNotRequestStartGameWithoutAKeyPress() {
        assertFalse(inputProcessor.consumeStartGameRequest());
    }

    @Test
    void shouldConsumeStartGameRequestOnlyOnce() {
        inputProcessor.keyDown(Input.Keys.SPACE);

        assertTrue(inputProcessor.consumeStartGameRequest());
        assertFalse(inputProcessor.consumeStartGameRequest());
    }
}
