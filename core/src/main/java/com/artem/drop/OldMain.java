package com.artem.drop;

public class OldMain {

   // @Override
    public void create() {

        // Prepare your application here.

        //      backgroundTexture = new Texture("background.png");
//        bucketTexture = new Texture("bucket.png");
//        dropTexture = new Texture("drop.png");
//
//        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
//        speedSound = Gdx.audio.newSound(Gdx.files.internal("speed.mp3"));
//
//        dropMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
//
//        spriteBatch = new SpriteBatch();
//
//        fitViewport = new FitViewport(8, 5);
//
//        bucketSprite = new Sprite(bucketTexture);
//        bucketSprite.setSize(1, 1);
//
//        touchPos = new Vector2();
//
//        dropSprites = new Array<>();
//
//        bucketRectangle = new Rectangle();
//        dropRectangle = new Rectangle();
//
//        dropMusic.setLooping(true);
//        dropMusic.setVolume(0.5f);
//        dropMusic.play();

        // speedSound.setVolume(1, 0);

    }

 //   @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if (width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
        //  fitViewport.update(width, height, true);
    }

  //  @Override
    public void render() {
        // Draw your application here.
//        input();
//        logic();
//        draw();
    }
}
