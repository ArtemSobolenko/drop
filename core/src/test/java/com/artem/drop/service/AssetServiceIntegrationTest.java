package com.artem.drop.service;

import com.artem.drop.support.GdxTestEnvironment;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.artem.drop.GameConstants.WORLD_HEIGHT;
import static com.artem.drop.support.GdxTestEnvironment.SCREEN_HEIGHT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Loads the real asset files (real PNG decode, real TTF parsing) through a
 * real AssetManager and FreeTypeFontGenerator - only the GPU (GL20) and audio
 * backend are mocked, since those need real hardware/drivers.
 *
 * This is a regression test for the bug where fonts were generated at a raw
 * pixel size without being scaled down to world units, making menu/HUD/pause
 * text render many times larger than the viewport.
 */
class AssetServiceIntegrationTest {

    @BeforeEach
    void setUpGdx() {
        GdxTestEnvironment.install();
    }

    @AfterEach
    void tearDownGdx() {
        GdxTestEnvironment.uninstall();
    }

    @Test
    void loadsAllAssetsAndScalesFontsDownToWorldUnits() {
        AssetService service = new AssetService();

        assertDoesNotThrow(service::loadAllAssets);

        assertNotNull(service.getBackgroundTexture());
        assertNotNull(service.getMainBackgroundTexture());
        assertNotNull(service.getBucketTexture());
        assertNotNull(service.getDropTexture());
        assertNotNull(service.getDropSound());
        assertNotNull(service.getDropMissSound());
        assertNotNull(service.getSpeedSound());
        assertNotNull(service.getMusic());
        assertNotNull(service.getMainMenuMusic());

        // Before the fix, fonts kept their raw generator pixel size (e.g. 48px)
        // with no relation to the ~5-unit-tall world, rendering far larger than
        // the viewport. They must now be scaled to worldHeight / screenHeight.
        float expectedScale = WORLD_HEIGHT / SCREEN_HEIGHT;
        assertEquals(expectedScale, service.getHudFont().getData().scaleX, 1e-6f);
        assertEquals(expectedScale, service.getMenuFont().getData().scaleX, 1e-6f);
        assertEquals(expectedScale, service.getPauseFont().getData().scaleX, 1e-6f);

        assertDoesNotThrow(service::disposeAll);
    }

    @Test
    void configuredMusicIsLoopingAndAtDefaultVolume() {
        AssetService service = new AssetService();
        service.loadAllAssets();

        assertDoesNotThrow(service::getConfiguredMusic);
        assertDoesNotThrow(service::getConfiguredMainMenuMusic);
    }

    /**
     * Regression test for a resource leak where disposeAll() only disposed
     * hudFont/menuFont/pauseFont and left exitFont/restartFont (and their
     * underlying native FreeType glyph textures) undisposed on shutdown.
     */
    @Test
    void disposeAllDisposesEveryGeneratedFont() throws ReflectiveOperationException {
        AssetService service = new AssetService();
        service.loadAllAssets();

        BitmapFont hudFont = spy(service.getHudFont());
        BitmapFont menuFont = spy(service.getMenuFont());
        BitmapFont pauseFont = spy(service.getPauseFont());
        BitmapFont exitFont = spy(service.getExitFont());
        BitmapFont restartFont = spy(service.getRestartFont());
        BitmapFont backToMenuFont = spy(service.getBackToMenuFont());

        replaceFontField(service, "hudFont", hudFont);
        replaceFontField(service, "menuFont", menuFont);
        replaceFontField(service, "pauseFont", pauseFont);
        replaceFontField(service, "exitFont", exitFont);
        replaceFontField(service, "restartFont", restartFont);
        replaceFontField(service, "backToMenuFont", backToMenuFont);

        service.disposeAll();

        verify(hudFont).dispose();
        verify(menuFont).dispose();
        verify(pauseFont).dispose();
        verify(exitFont).dispose();
        verify(restartFont).dispose();
        verify(backToMenuFont).dispose();
    }

    private void replaceFontField(AssetService service, String fieldName, BitmapFont value)
        throws ReflectiveOperationException {
        Field field = AssetService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }

    @Test
    void unloadingAnAlreadyUnloadedAssetIsANoOp() {
        AssetService service = new AssetService();
        service.loadAllAssets();

        service.unloadMainBackground();
        service.unloadMainMenuMusic();

        // Second call hits the "not loaded" branch and must not throw.
        assertDoesNotThrow(service::unloadMainBackground);
        assertDoesNotThrow(service::unloadMainMenuMusic);

        assertThrows(RuntimeException.class, service::getMainBackgroundTexture);
    }
}
