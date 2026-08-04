package com.artem.drop.input;

import com.badlogic.gdx.math.Vector2;

public interface PlayerInput {

    boolean isMoveLeftPressed();

    boolean isMoveRightPressed();

    boolean isLeftShiftPressed();

    boolean isTouched();

    boolean isJustTouched();

    Vector2 getTouchPos();

    boolean isPausePressed();

}
