package com.artem.drop.state;

import lombok.Getter;

@Getter
public class GameState {

    private boolean paused;

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void togglePause() {
        paused = !paused;
    }

}
