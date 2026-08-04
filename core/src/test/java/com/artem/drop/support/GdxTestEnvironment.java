package com.artem.drop.support;

import com.artem.drop.service.AssetService;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxNativesLoader;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Installs just enough of a mocked libGDX runtime (Gdx.files/graphics/gl/audio/app)
 * for tests to construct real AssetManager-backed assets (real PNG decode, real
 * TTF parsing) without a real window/GPU/audio device. Normally this wiring is
 * done by the LWJGL3 backend at app bootstrap.
 */
public final class GdxTestEnvironment {

    public static final int SCREEN_HEIGHT = 500;

    private GdxTestEnvironment() {
    }

    public static void install() {
        GdxNativesLoader.load();

        File assetsDir = new File(System.getProperty("assetsDir"));
        Files files = mock(Files.class);
        when(files.internal(anyString()))
            .thenAnswer(invocation -> new FileHandle(new File(assetsDir, invocation.getArgument(0))));
        Gdx.files = files;

        Graphics graphics = mock(Graphics.class);
        when(graphics.getHeight()).thenReturn(SCREEN_HEIGHT);
        Gdx.graphics = graphics;

        GL20 gl = mock(GL20.class);
        when(gl.glGenTexture()).thenReturn(1);
        when(gl.glGetError()).thenReturn(GL20.GL_NO_ERROR);
        Gdx.gl = gl;
        Gdx.gl20 = gl;

        Audio audio = mock(Audio.class);
        when(audio.newSound(any())).thenReturn(mock(Sound.class));
        when(audio.newMusic(any())).thenReturn(mock(Music.class));
        Gdx.audio = audio;

        Gdx.app = mock(Application.class);
    }

    public static void uninstall() {
        Gdx.files = null;
        Gdx.graphics = null;
        Gdx.gl = null;
        Gdx.gl20 = null;
        Gdx.audio = null;
        Gdx.app = null;
    }

    public static AssetService loadRealAssetService() {
        AssetService service = new AssetService();
        service.loadAllAssets();
        return service;
    }
}
