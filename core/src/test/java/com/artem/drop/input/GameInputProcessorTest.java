package com.artem.drop.input;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameInputProcessorTest {

    private GameInputProcessor inputProcessor;

    @BeforeEach
    void setUp() {
        inputProcessor = new GameInputProcessor();
    }

    @Test
    void keyDownShouldReturnTrueForEscape() {
        assertTrue(inputProcessor.keyDown(Input.Keys.ESCAPE));
    }

    @Test
    void keyDownShouldReturnFalseForOtherKeys() {
        assertFalse(inputProcessor.keyDown(Input.Keys.SPACE));
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
