package com.artem.drop.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

@Getter
public class DesktopPlayerInput implements PlayerInput {

    private final Vector2 touchPos;

    public DesktopPlayerInput() {
        this.touchPos = new Vector2();
    }

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

    @Override
    public boolean isJustTouched() {
        return Gdx.input.justTouched();
    }

    @Override
    public Vector2 getTouchPos() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY());
        return touchPos;
    }

    @Override
    public boolean isPausePressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    }

}
