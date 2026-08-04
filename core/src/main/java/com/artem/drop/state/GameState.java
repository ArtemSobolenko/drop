package com.artem.drop.state;

import lombok.Getter;

@Getter
public class GameState {

    private boolean paused;

    public void togglePause() {
        paused = !paused;
    }

}
