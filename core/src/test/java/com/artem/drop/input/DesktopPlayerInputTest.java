package com.artem.drop.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DesktopPlayerInputTest {

    private Input input;
    private DesktopPlayerInput playerInput;

    @BeforeEach
    void setUp() {
        input = mock(Input.class);
        Gdx.input = input;
        playerInput = new DesktopPlayerInput();
    }

    @AfterEach
    void tearDown() {
        Gdx.input = null;
    }

    @Test
    void delegatesMoveLeftToLeftArrowKey() {
        when(input.isKeyPressed(Input.Keys.LEFT)).thenReturn(true);

        assertTrue(playerInput.isMoveLeftPressed());
    }

    @Test
    void delegatesMoveRightToRightArrowKey() {
        when(input.isKeyPressed(Input.Keys.RIGHT)).thenReturn(true);

        assertTrue(playerInput.isMoveRightPressed());
    }

    @Test
    void delegatesLeftShiftToShiftKey() {
        when(input.isKeyPressed(Input.Keys.SHIFT_LEFT)).thenReturn(true);

        assertTrue(playerInput.isLeftShiftPressed());
    }

    @Test
    void reportsFalseWhenKeyIsNotPressed() {
        assertFalse(playerInput.isMoveLeftPressed());
    }

    @Test
    void delegatesTouchAndJustTouchedState() {
        when(input.isTouched()).thenReturn(true);
        when(input.justTouched()).thenReturn(true);

        assertTrue(playerInput.isTouched());
        assertTrue(playerInput.isJustTouched());
    }

    @Test
    void getTouchPosReadsCurrentPointerPosition() {
        when(input.getX()).thenReturn(42);
        when(input.getY()).thenReturn(24);

        Vector2 pos = playerInput.getTouchPos();

        assertEquals(42f, pos.x);
        assertEquals(24f, pos.y);
    }

    @Test
    void delegatesPauseToEscapeKey() {
        when(input.isKeyJustPressed(Input.Keys.ESCAPE)).thenReturn(true);

        assertTrue(playerInput.isPausePressed());
    }
}
