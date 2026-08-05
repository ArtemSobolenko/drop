package com.artem.drop.state;

import lombok.Getter;

@Getter
public class GameState {

    public enum Mode {
        PLAYING,
        PAUSED
    }

    private Mode mode = Mode.PLAYING;

    public boolean isPlaying() {
        return mode == Mode.PLAYING;
    }

    public boolean isPaused() {
        return mode == Mode.PAUSED;
    }

    public void togglePause() {
        mode = isPlaying()
            ? Mode.PAUSED
            : Mode.PLAYING;
    }

    public void setPlaying() {
        mode = Mode.PLAYING;
    }

    public void setPaused() {
        mode = Mode.PAUSED;
    }

}
