package com.artem.drop.service;

import com.artem.drop.support.GdxTestEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.artem.drop.GameConstants.WORLD_HEIGHT;
import static com.artem.drop.support.GdxTestEnvironment.SCREEN_HEIGHT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
