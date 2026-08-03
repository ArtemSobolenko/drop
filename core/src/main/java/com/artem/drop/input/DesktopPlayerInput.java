package com.artem.drop.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class DesktopPlayerInput implements PlayerInput {

    @Override
    public boolean isMoveLeftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    @Override
    public boolean isMoveRightPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    @Override
    public boolean isLeftShiftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
    }

    @Override
    public boolean isTouched() {
        return Gdx.input.isTouched();
    }
}
