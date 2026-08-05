package com.artem.drop.screen;

import com.artem.drop.service.AssetService;
import com.artem.drop.support.GdxTestEnvironment;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.artem.drop.GameConstants.GAME_EXIT_TEXT;
import static com.artem.drop.GameConstants.GAME_MAIN_MENU_TEXT;
import static com.artem.drop.GameConstants.GAME_PAUSED_TEXT;
import static com.artem.drop.GameConstants.GAME_RESTART_TEXT;
import static com.artem.drop.GameConstants.PAUSE_OVERLAY_LINE_SPACING;
import static com.artem.drop.GameConstants.WORLD_HEIGHT;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for a bug where the pause overlay's "back to main menu" line
 * was drawn below y=0 (off the bottom of the viewport): PAUSE_OVERLAY_LINE_SPACING
 * was large enough that a 5-unit-tall world couldn't fit the pause title plus
 * three option lines below it, so the text rendered without throwing but was
 * never visible. Mirrors the same title-centering + per-line-offset formula
 * GameScreen.drawPauseOverlay()/drawPauseOverlayOption() use, with real font
 * metrics, so a future spacing/line change that pushes text off-screen fails
 * here instead of silently shipping invisible UI.
 */
class GameScreenPauseOverlayGeometryTest {

    @BeforeEach
    void setUp() {
        GdxTestEnvironment.install();
    }

    @AfterEach
    void tearDown() {
        GdxTestEnvironment.uninstall();
    }

    @Test
    void everyPauseOverlayOptionLineStaysWithinTheVisibleWorldHeight() {
        AssetService assetService = GdxTestEnvironment.loadRealAssetService();

        GlyphLayout pausedLayout = new GlyphLayout(assetService.getPauseFont(), GAME_PAUSED_TEXT);
        float pausedTextY = (WORLD_HEIGHT + pausedLayout.height) / 2f;

        assertLineIsOnScreen(assetService.getExitFont(), GAME_EXIT_TEXT, pausedTextY, 1);
        assertLineIsOnScreen(assetService.getRestartFont(), GAME_RESTART_TEXT, pausedTextY, 2);
        assertLineIsOnScreen(assetService.getBackToMenuFont(), GAME_MAIN_MENU_TEXT, pausedTextY, 3);
    }

    private void assertLineIsOnScreen(BitmapFont font, String text, float pausedTextY, int lineIndex) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float y = pausedTextY - lineIndex * PAUSE_OVERLAY_LINE_SPACING;

        assertTrue(y >= 0f && y <= WORLD_HEIGHT,
            "line " + lineIndex + " (\"" + text + "\") draws at y=" + y
                + ", which is outside the visible [0, " + WORLD_HEIGHT + "] world height");
    }
}
