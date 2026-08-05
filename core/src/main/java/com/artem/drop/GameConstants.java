package com.artem.drop;

public final class GameConstants {

    private GameConstants() {
    }

    public static final float WORLD_WIDTH = 8f;
    public static final float WORLD_HEIGHT = 5f;

    public static final float DEFAULT_VOLUME = .5f;
    public static final float DEFAULT_SPEED_VOLUME = .1f;
    public static final float DEFAULT_SPEED = 4f;
    public static final int DEFAULT_SPEED_MULTIPLIER = 2;
    public static final float DEFAULT_DROPLET_CREATION_DELAY = 1f;
    public static final float DEFAULT_DROPLET_FALLING_SPEED = -2f;

    public static final float HUD_LINE_SPACING = 0.4f;
    public static final float PAUSE_OVERLAY_LINE_SPACING = 1f;

    public static final String BACKGROUND_TEXTURE = "images/background.png";
    public static final String MAIN_BACKGROUND_TEXTURE = "images/main_background.png";

    public static final String DROP_TEXTURE = "images/drop.png";
    public static final String BUCKET_TEXTURE = "images/bucket.png";

    public static final String DROP_SOUND = "sounds/drop.mp3";
    public static final String SPEED_SOUND = "sounds/speed.mp3";
    public static final String DROP_MISS_SOUND = "sounds/drop_miss.mp3";

    public static final String GAME_MUSIC = "music/music.mp3";
    public static final String MAIN_MENU_MUSIC = "music/main_menu.mp3";

    public static final String ROBOTO_REGULAR_FRONT = "fonts/Roboto-Regular.ttf";

    public static final String GAME_PAUSED_TEXT = "GAME PAUSED";
    public static final String GAME_EXIT_TEXT = "Press \"Q\" to exit game";
    public static final String GAME_RESTART_TEXT = "Press \"R\" to restart game";
    public static final String GAME_MAIN_MENU_TEXT = "Press \"BACKSPACE\" to go to main menu";
    public static final String MAIN_MENU_TITLE_TEXT = "Welcome to Drop Game!!!";
    public static final String MAIN_MENU_SUB_TITLE_TEXT = "Tap anywhere or press SPACE to begin!";

}
