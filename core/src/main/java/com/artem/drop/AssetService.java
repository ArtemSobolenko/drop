package com.artem.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class AssetService {

    private final AssetManager assetManager = new AssetManager();

    public void loadAllAssets() {

        assetManager.load("main_background.png", Texture.class);
        assetManager.load("background.png", Texture.class);

        assetManager.load("drop.png", Texture.class);
        assetManager.load("bucket.png", Texture.class);

        assetManager.load("drop.mp3", Sound.class);
        assetManager.load("speed.mp3", Sound.class);

        assetManager.load("music.mp3", Music.class);
        assetManager.load("main_menu.mp3", Music.class);

        assetManager.finishLoading();

        Gdx.app.log("Game", "All assets loaded.");
    }

    public Texture getMainBackgroundTexture() {
        return assetManager.get("main_background.png", Texture.class);
    }

    public Texture getBackgroundTexture() {
        return assetManager.get("background.png", Texture.class);
    }

    public Texture getDropTexture() {
        return assetManager.get("drop.png", Texture.class);
    }

    public Texture getBucketTexture() {
        return assetManager.get("bucket.png", Texture.class);
    }

    public Sound getDropSound() {
        return assetManager.get("drop.mp3", Sound.class);
    }

    public Sound getSpeedSound() {
        return assetManager.get("speed.mp3", Sound.class);
    }

    public Music getMusic() {
        return assetManager.get("music.mp3", Music.class);
    }

    public Music getMainMenuMusic() {
        return assetManager.get("main_menu.mp3", Music.class);
    }

    public Music getConfiguredMainMenuMusic() {
        Music music = this.getMainMenuMusic();
        music.setLooping(true);
        music.setVolume(0.5f);
        return music;
    }

    public Music getConfiguredMusic() {
        Music music = this.getMusic();
        music.setLooping(true);
        music.setVolume(0.5f);
        return music;
    }

    public void unloadMainBackground() {
        if (assetManager.isLoaded("main_background.png")) {
            assetManager.unload("main_background.png");
        }
    }

    public void unloadMainMenuMusic() {
        if (assetManager.isLoaded("main_menu.mp3")) {
            assetManager.unload("main_menu.mp3");
        }
    }

    public void disposeAllAssets() {
        assetManager.dispose();
        Gdx.app.log("Game", "All assets disposed.");
    }
}
