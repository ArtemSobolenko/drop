package com.artem.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {

    final Drop game;

    private final AssetService assetService;

   // Texture backgroundTexture;
   // Texture bucketTexture;
   // Texture dropTexture;

  //  Sound dropSound;
  //  Sound speedSound;

 //   Music music;

    Sprite bucketSprite;

    Vector2 touchPos;

    Array<Sprite> dropSprites;

    Rectangle bucketRectangle;
    Rectangle dropRectangle;

    float dropTimer;
    int dropsGathered;

    public GameScreen(final Drop game) {

        this.game = game;

        this.assetService = new AssetService();
        assetService.loadAllAssets();

        // load the images for the background, bucket and droplet
       // backgroundTexture = new Texture("background.png");
      //  bucketTexture = new Texture("bucket.png");
        //dropTexture = new Texture("drop.png");

        // load the drop sound effect and background music
     //   dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
      //  speedSound = Gdx.audio.newSound(Gdx.files.internal("speed.mp3"));

//        music = assetService.getMusic();
//        music.setLooping(true);
//        music.setVolume(0.5F);

        bucketSprite = new Sprite(assetService.getBucketTexture());
        bucketSprite.setSize(1, 1);

        touchPos = new Vector2();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        dropSprites = new Array<>();

    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
//        Music music = assetService.getMusic();
//        music.setLooping(true);
//        music.setVolume(0.5F);
        assetService.getConfiguredMusic().play();
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
    }

    private void input() {

        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            speed *= 2;
            assetService.getSpeedSound().play(.1f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        float delta = Gdx.graphics.getDeltaTime();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
            else if (bucketRectangle.overlaps(dropRectangle)) {
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

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(assetService.getBackgroundTexture(), 0, 0, worldWidth, worldHeight);
        bucketSprite.draw(game.batch);

        game.font.draw(game.batch, "Drops collected: " + dropsGathered, 0, worldHeight);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(game.batch);
        }

        game.batch.end();
    }

    private void createDroplet() {

        float dropWidth = 1;
        float dropHeight = 1;

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(assetService.getDropTexture());

        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0F, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);

        dropSprites.add(dropSprite);
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
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
       // backgroundTexture.dispose();
     //   dropSound.dispose();
     //   music.dispose();
      //  dropTexture.dispose();
        //bucketTexture.dispose();

        assetService.disposeAllAssets();
    }
}
