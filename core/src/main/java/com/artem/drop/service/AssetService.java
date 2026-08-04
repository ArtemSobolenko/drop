package com.artem.drop.service;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import static com.artem.drop.GameConstants.BACKGROUND_TEXTURE;
import static com.artem.drop.GameConstants.BUCKET_TEXTURE;
import static com.artem.drop.GameConstants.DROP_MISS_SOUND;
import static com.artem.drop.GameConstants.DROP_SOUND;
import static com.artem.drop.GameConstants.DROP_TEXTURE;
import static com.artem.drop.GameConstants.GAME_MUSIC;
import static com.artem.drop.GameConstants.MAIN_BACKGROUND_TEXTURE;
import static com.artem.drop.GameConstants.MAIN_MENU_MUSIC;
import static com.artem.drop.GameConstants.SPEED_SOUND;
import static com.artem.drop.GameConstants.DEFAULT_VOLUME;

public class AssetService {

    private final AssetManager assetManager = new AssetManager();

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

    public void disposeAllAssets() {
        assetManager.dispose();
    }
}
