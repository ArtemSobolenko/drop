package com.artem.drop.screen;

import com.artem.drop.GameContext;
import com.artem.drop.entity.Bucket;
import com.artem.drop.input.DesktopPlayerInput;
import com.artem.drop.service.AssetService;
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

import static com.artem.drop.GameConstants.DEFAULT_SPEED;
import static com.artem.drop.GameConstants.DEFAULT_SPEED_MULTIPLIER;
import static com.artem.drop.GameConstants.DEFAULT_SPEED_VOLUME;

@Slf4j
public class GameScreen implements Screen {

    private final GameContext gameContext;

    private final AssetService assetService;

    private final DesktopPlayerInput desktopPlayerInput;

    private Bucket bucket;

    private Array<Sprite> dropSprites;

    private final FitViewport viewport;

    private final SpriteBatch spriteBatch;

    private Rectangle dropRectangle;

    private float dropTimer;
    private int dropsGathered;

    public GameScreen(final GameContext context) {

        this.gameContext = context;

        this.assetService = context.assetService();

        this.desktopPlayerInput = context.desktopPlayerInput();

        this.viewport = context.viewport();
        this.spriteBatch = context.spriteBatch();

        bucket = new Bucket(new Sprite(assetService.getBucketTexture()));

        dropSprites = new Array<>();

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
        viewport.apply();
        input(delta);
        logic(delta);
        draw();
    }

    private void input(float delta) {

        float speed = DEFAULT_SPEED;
        float dx;

        if (desktopPlayerInput.isLeftShiftPressed()) {
            speed *= DEFAULT_SPEED_MULTIPLIER;
            assetService.getSpeedSound().play(DEFAULT_SPEED_VOLUME);
        }

        //move right
        if (desktopPlayerInput.isMoveRightPressed()) {
            dx = speed * delta;
            bucket.move(dx);
        }

        //move left
        if (desktopPlayerInput.isMoveLeftPressed()) {
            dx = speed * delta;
            bucket.move(-dx);
        }

        if (desktopPlayerInput.isTouched()) {
            Vector2 touchPos = desktopPlayerInput.getTouchPos();
            viewport.unproject(touchPos);
            bucket.setCenterX(touchPos.x);
        }
    }

    private void logic(float delta) {

        bucket.setX(MathUtils.clamp(bucket.getX(), 0, viewport.getWorldWidth() - bucket.getWidth()));

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            } else if (bucket.getBounds().overlaps(dropRectangle)) {
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

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(assetService.getBackgroundTexture(), 0, 0, worldWidth, worldHeight);

        bucket.render(spriteBatch);

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
        //do not call dispose here
    }
}
