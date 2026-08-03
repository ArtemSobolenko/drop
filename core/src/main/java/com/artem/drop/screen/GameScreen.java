package com.artem.drop.screen;

import com.artem.drop.GameContext;
import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameScreen implements Screen {

    private final GameContext gameContext;

    private final AssetService assetService;

    private final DesktopPlayerInput desktopPlayerInput;

    private Sprite bucketSprite;

    private Vector2 touchPos;

    private Array<Sprite> dropSprites;

    private final FitViewport viewport;

    private final SpriteBatch spriteBatch;

    private Rectangle bucketRectangle;
    private Rectangle dropRectangle;

    private float dropTimer;
    private int dropsGathered;

    public GameScreen(final GameContext context) {

        this.gameContext = context;

        this.assetService = context.assetService();

        this.desktopPlayerInput = context.desktopPlayerInput();

        this.viewport = context.viewport();
        this.spriteBatch = context.spriteBatch();

        bucketSprite = new Sprite(assetService.getBucketTexture());
        bucketSprite.setSize(1, 1);

        touchPos = new Vector2();

        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        dropTimer = 0f;
        dropsGathered = 0;
    }

    @Override
    public void show() {
        // start the playback of the background music when the screen is shown
        assetService.getConfiguredMusic().play();
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        draw();
    }

    private void input(float delta) {

        float speed = 4f;

        if (desktopPlayerInput.isLeftShiftPressed()) {
            speed *= 2;
            assetService.getSpeedSound().play(.1f);
        }

        //move right
        if (desktopPlayerInput.isMoveRightPressed()) {
            bucketSprite.translateX(speed * delta);
        }

        //move left
        if (desktopPlayerInput.isMoveLeftPressed()) {
            bucketSprite.translateX(-speed * delta);
        }

        if (desktopPlayerInput.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    private void logic(float delta) {

        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, viewport.getWorldWidth() - bucketWidth));
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            } else if (bucketRectangle.overlaps(dropRectangle)) {
                dropsGathered++;
                dropSprites.removeIndex(i);
                assetService.getDropSound().play();
            }
        }

        dropTimer += delta;
        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(assetService.getBackgroundTexture(), 0, 0, worldWidth, worldHeight);
        bucketSprite.draw(spriteBatch);

        gameContext.bitmapFont()
            .draw(spriteBatch, "Drops collected: " + dropsGathered, 0, worldHeight);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void createDroplet() {

        float dropWidth = 1;
        float dropHeight = 1;

        Sprite dropSprite = new Sprite(assetService.getDropTexture());

        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0F, viewport.getWorldWidth() - dropWidth));
        dropSprite.setY(viewport.getWorldHeight());

        dropSprites.add(dropSprite);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        //  assetService.disposeAllAssets(); do not call this here
    }
}
