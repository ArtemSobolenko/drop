package com.artem.drop;

public final class GameConstants {

    private GameConstants() {
    }

    public static final float WORLD_WIDTH = 8f;
    public static final float WORLD_HEIGHT = 5f;
    public static final float WORLD_MIN_X = 0f;
    public static final float GROUND_Y = 0f;

    public static final float ENTITY_SIZE = 1f;

    public static final float DEFAULT_VOLUME = .5f;
    public static final float DEFAULT_SPEED_VOLUME = .1f;
    public static final float DEFAULT_SPEED = 4f;
    public static final int DEFAULT_SPEED_MULTIPLIER = 2;
    public static final float DEFAULT_DROPLET_CREATION_DELAY = 1f;
    public static final float DEFAULT_DROPLET_FALLING_SPEED = -2f;

    public static final float BUCKET_GRAVITY = -15f;
    public static final float BUCKET_JUMP_SPEED = 7f;

    public static final float HUD_LINE_SPACING = 0.4f;
    // Small enough that pause title + 3 option lines all stay within
    // WORLD_HEIGHT below the pause title's vertical center; a value of 1f
    // pushed the third line below y=0, off the bottom of the viewport.
    public static final float PAUSE_OVERLAY_LINE_SPACING = 0.6f;
    public static final int PAUSE_OVERLAY_EXIT_OPTION_LINE = 1;
    public static final int PAUSE_OVERLAY_RESTART_OPTION_LINE = 2;
    public static final int PAUSE_OVERLAY_MAIN_MENU_OPTION_LINE = 3;

    public static final float MAIN_MENU_TITLE_Y_FRACTION = 0.35f;
    public static final float MAIN_MENU_SUBTITLE_Y_FRACTION = 0.20f;

    public static final float RAIN_INTRO_DURATION_SECONDS = 5f;
    public static final float RAIN_INTRO_TEXT_MARGIN = 0.2f;

    public static final int HUD_FONT_SIZE = 24;
    public static final int MENU_FONT_SIZE = 36;
    public static final int PAUSE_FONT_SIZE = 64;
    public static final int GAME_EXIT_FONT_SIZE = 22;
    public static final int GAME_RESTART_FONT_SIZE = 22;
    public static final int BACK_TO_MENU_FONT_SIZE = 22;

    public static final String BACKGROUND_TEXTURE = "images/background.png";
    public static final String MAIN_BACKGROUND_TEXTURE = "images/main_background.png";

    public static final String DROP_TEXTURE = "images/drop.png";
    public static final String BUCKET_TEXTURE = "images/bucket.png";

    public static final String DROP_SOUND = "sounds/drop.mp3";
    public static final String SPEED_SOUND = "sounds/speed.mp3";
    public static final String DROP_MISS_SOUND = "sounds/drop_miss.mp3";
    public static final String THUNDER_INTRO_SOUND = "sounds/thunder-intro.mp3";

    public static final String GAME_MUSIC = "music/music.mp3";
    public static final String MAIN_MENU_MUSIC = "music/main_menu.mp3";

    public static final String ROBOTO_REGULAR_FRONT = "fonts/Roboto-Regular.ttf";

    public static final String GAME_PAUSED_TEXT = "GAME PAUSED";
    public static final String GAME_EXIT_TEXT = "Press \"Q\" to exit game";
    public static final String GAME_RESTART_TEXT = "Press \"R\" to restart game";
    public static final String GAME_MAIN_MENU_TEXT = "Press \"BACKSPACE\" to go to main menu";
    public static final String MAIN_MENU_TITLE_TEXT = "Welcome to Drop Game!!!";
    public static final String MAIN_MENU_SUB_TITLE_TEXT = "Tap anywhere or press SPACE to begin!";
    public static final String RAIN_INTRO_TEXT = "It is raining now...";

}
