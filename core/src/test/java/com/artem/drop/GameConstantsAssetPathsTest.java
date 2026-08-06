package com.artem.drop;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every asset-path constant in GameConstants must point to a file that
 * actually exists under assets/, at exactly that relative path. AssetService
 * loads these through a mocked audio/GL backend in other tests, which never
 * touches the real file on disk - so a moved/renamed asset whose constant
 * wasn't updated to match would go unnoticed there. This test exists to catch
 * exactly that class of bug directly, independent of loading machinery.
 */
class GameConstantsAssetPathsTest {

    private static Stream<String> assetPaths() {
        return Stream.of(
            GameConstants.BACKGROUND_TEXTURE,
            GameConstants.MAIN_BACKGROUND_TEXTURE,
            GameConstants.DROP_TEXTURE,
            GameConstants.BUCKET_TEXTURE,
            GameConstants.DROP_SOUND,
            GameConstants.SPEED_SOUND,
            GameConstants.DROP_MISS_SOUND,
            GameConstants.THUNDER_INTRO_SOUND,
            GameConstants.GAME_MUSIC,
            GameConstants.MAIN_MENU_MUSIC,
            GameConstants.ROBOTO_REGULAR_FRONT
        );
    }

    @ParameterizedTest
    @MethodSource("assetPaths")
    void assetConstantPointsToAnExistingFile(String relativePath) {
        File assetsDir = new File(System.getProperty("assetsDir"));
        File file = new File(assetsDir, relativePath);

        assertTrue(file.isFile(),
            "GameConstants references \"" + relativePath + "\" but no such file exists at "
                + file.getAbsolutePath());
    }
}
