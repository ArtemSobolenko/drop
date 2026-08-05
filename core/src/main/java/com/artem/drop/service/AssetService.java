package com.artem.drop.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import lombok.Getter;

import static com.artem.drop.GameConstants.BACKGROUND_TEXTURE;
import static com.artem.drop.GameConstants.BUCKET_TEXTURE;
import static com.artem.drop.GameConstants.DEFAULT_VOLUME;
import static com.artem.drop.GameConstants.DROP_MISS_SOUND;
import static com.artem.drop.GameConstants.DROP_SOUND;
import static com.artem.drop.GameConstants.DROP_TEXTURE;
import static com.artem.drop.GameConstants.GAME_MUSIC;
import static com.artem.drop.GameConstants.MAIN_BACKGROUND_TEXTURE;
import static com.artem.drop.GameConstants.MAIN_MENU_MUSIC;
import static com.artem.drop.GameConstants.ROBOTO_REGULAR_FRONT;
import static com.artem.drop.GameConstants.SPEED_SOUND;
import static com.artem.drop.GameConstants.WORLD_HEIGHT;

public class AssetService {

    private static final int HUD_FONT_SIZE = 24;
    private static final int MENU_FONT_SIZE = 36;
    private static final int PAUSE_FONT_SIZE = 64;
    private static final int GAME_EXIT_FONT_SIZE = 36;
    private static final int GAME_RESTART_FONT_SIZE = 36;

    private final AssetManager assetManager;
    private final FreeTypeFontGenerator fontGenerator;

    @Getter
    private BitmapFont hudFont;
    @Getter
    private BitmapFont menuFont;
    @Getter
    private BitmapFont pauseFont;
    @Getter
    private BitmapFont exitFont;
    @Getter
    private BitmapFont restartFont;

    public AssetService() {
        this.assetManager = new AssetManager();
        this.fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(ROBOTO_REGULAR_FRONT));
    }

    public void loadAllAssets() {

        assetManager.load(MAIN_BACKGROUND_TEXTURE, Texture.class);
        assetManager.load(BACKGROUND_TEXTURE, Texture.class);

        assetManager.load(DROP_TEXTURE, Texture.class);
        assetManager.load(BUCKET_TEXTURE, Texture.class);

        assetManager.load(DROP_SOUND, Sound.class);
        assetManager.load(SPEED_SOUND, Sound.class);
        assetManager.load(DROP_MISS_SOUND, Sound.class);

        assetManager.load(GAME_MUSIC, Music.class);
        assetManager.load(MAIN_MENU_MUSIC, Music.class);

        assetManager.finishLoading();

        // Fonts are generated at a fixed pixel size, so without scaling them down to
        // world units they render dozens of world-units tall on our ~5-unit-tall
        // viewport (that's what was making menu text on MainMenuScreen huge/off-screen).
        float fontScale = WORLD_HEIGHT / Gdx.graphics.getHeight();

        this.hudFont = this.generateFont(HUD_FONT_SIZE, fontScale);
        this.menuFont = this.generateFont(MENU_FONT_SIZE, fontScale);
        this.pauseFont = this.generateFont(PAUSE_FONT_SIZE, fontScale);
        this.exitFont = this.generateFont(GAME_EXIT_FONT_SIZE, fontScale);
        this.restartFont = this.generateFont(GAME_RESTART_FONT_SIZE, fontScale);
    }

    public Texture getMainBackgroundTexture() {
        return assetManager.get(MAIN_BACKGROUND_TEXTURE, Texture.class);
    }

    public Texture getBackgroundTexture() {
        return assetManager.get(BACKGROUND_TEXTURE, Texture.class);
    }

    public Texture getDropTexture() {
        return assetManager.get(DROP_TEXTURE, Texture.class);
    }

    public Texture getBucketTexture() {
        return assetManager.get(BUCKET_TEXTURE, Texture.class);
    }

    public Sound getDropSound() {
        return assetManager.get(DROP_SOUND, Sound.class);
    }

    public Sound getDropMissSound() {
        return assetManager.get(DROP_MISS_SOUND, Sound.class);
    }

    public Sound getSpeedSound() {
        return assetManager.get(SPEED_SOUND, Sound.class);
    }

    public Music getMusic() {
        return assetManager.get(GAME_MUSIC, Music.class);
    }

    public Music getMainMenuMusic() {
        return assetManager.get(MAIN_MENU_MUSIC, Music.class);
    }

    public Music getConfiguredMainMenuMusic() {
        Music music = this.getMainMenuMusic();
        music.setLooping(true);
        music.setVolume(DEFAULT_VOLUME);
        return music;
    }

    public Music getConfiguredMusic() {
        Music music = this.getMusic();
        music.setLooping(true);
        music.setVolume(DEFAULT_VOLUME);
        return music;
    }

    public void unloadMainBackground() {
        if (assetManager.isLoaded(MAIN_BACKGROUND_TEXTURE)) {
            assetManager.unload(MAIN_BACKGROUND_TEXTURE);
        }
    }

    public void unloadMainMenuMusic() {
        if (assetManager.isLoaded(MAIN_MENU_MUSIC)) {
            assetManager.unload(MAIN_MENU_MUSIC);
        }
    }

    private BitmapFont generateFont(int size, float scale) {

        FreeTypeFontGenerator.FreeTypeFontParameter parameter
            = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;

        BitmapFont font = fontGenerator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        font.getData().setScale(scale);

        return font;
    }

    public void disposeAll() {
        hudFont.dispose();
        menuFont.dispose();
        pauseFont.dispose();
        fontGenerator.dispose();
        assetManager.dispose();
    }
}
