package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.audio.SoundPlayer;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.FilmStrip;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class LoadScreen implements Screen {
  /** Internal assets for this loading screen */
  private AssetDirectory internal;
  /** The actual assets to be loaded */
  private AssetDirectory assets;

  /** Reference to GameCanvas created by the root */
  private GameCanvas canvas;

  private ScreenViewport viewport;


  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  /** Whether or not this player mode is still active */
  private boolean active;

  /** If the load screen has begun loading assets */
  private boolean begunAssetLoading;

  /** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
  private int budget;

  private int timer;

  /** Background texture for start-up */
  private Texture background;

  /** Animation to play while loading */
  private FilmStrip playerRun;

  /** Font for display text */
  private BitmapFont textFont;

  private int frameCounter;

  private int frameDelay;

  private int framesInAnimation;

  /** Default budget for asset loader (do nothing but load 60 fps) */
  private static int DEFAULT_BUDGET = 50;

  private int exitCode;

  /** object to play music and sound effects */
  private SoundPlayer soundPlayer;

  /** Stored canvas width to know when the canvas is resized */
  private int storedWidth;

  /** Scale of font */
  private float fontScale;

  /**
   * Creates a LoadScreen with the default budget, size and position.
   *
   * @param file  	The asset directory to load in the background
   * @param canvas 	The game canvas to draw to
   */
  public LoadScreen(String file, GameCanvas canvas, int exitCode, SoundPlayer soundPlayer) {
    this(file, canvas, DEFAULT_BUDGET, exitCode, soundPlayer);
  }

  public LoadScreen(String file, GameCanvas canvas, int millis, int exitCode, SoundPlayer soundPlayer) {
    this.canvas = canvas;
    this.soundPlayer = soundPlayer;
    storedWidth = canvas.getWidth();
    fontScale = (float) canvas.getHeight() / 200;
    this.viewport = new ScreenViewport();
    budget = millis;
    timer = 0;
    frameCounter = 0;
    frameDelay = 4;
    begunAssetLoading = false;
    this.exitCode = exitCode;

    internal = new AssetDirectory( "loading.json" );
    internal.loadAssets();
    internal.finishLoading();

    background = internal.getEntry( "loading:background", Texture.class );

    Texture runTexture = internal.getEntry("loading:player_run", Texture.class);
    framesInAnimation = runTexture.getWidth() / runTexture.getHeight();
    playerRun = new FilmStrip(runTexture, 1, framesInAnimation);

    textFont = internal.getEntry("loading:alagard", BitmapFont.class);
    resizeFont();

    assets = new AssetDirectory( file );
    active = true;
  }

  /** Sets exit code of the loading screen
   *
   * @param exitCode exit code returns when the screen changes
   */
  public void resetWithExitCode(int exitCode) {
    this.exitCode = exitCode;
    this.active = true;
    this.timer = 0;
  }

  /**
   * Update the status of this player mode.
   *
   * We prefer to separate update and draw from one another as separate methods,
   * instead
   * of using the single render() method that LibGDX does. We will talk about why
   * we
   * prefer this in lecture.
   *
   * @param delta Number of seconds since last animation frame
   */
  private void update(float delta) {
    viewport.update(canvas.getWidth(), canvas.getHeight());
    viewport.apply();

    timer += 1;
  }

  private void draw() {
    canvas.clear(Color.BLACK);
    canvas.begin();
    canvas.getSpriteBatch().setProjectionMatrix(viewport.getCamera().combined);

    // draw animation
    drawPlayer();

    // draw text
    String text = "Loading...";
    textFont.dispose();
    resizeFont();

    GlyphLayout layout = new GlyphLayout(textFont, text);
    float ox = - layout.width / 2.0f;
    float oy = 0.15f* canvas.getHeight();
    canvas.drawText(text, textFont, ox,  oy);

    canvas.end();
  }

  private void drawPlayer() {
    frameCounter += 1;
    if (frameCounter % frameDelay == 0) {
      if (playerRun.getFrame() >= framesInAnimation-1) playerRun.setFrame(0);
      else playerRun.setFrame(playerRun.getFrame()+1);
    }

    float width = playerRun.getRegionWidth();
    float height = playerRun.getRegionHeight();
    float ox = -width/2f - 2*width + playerRun.getFrame()*(4f/11)*width;
    float scale = fontScale / 5f;
    canvas.draw(playerRun, Color.WHITE, ox, -scale*height, scale*width, scale*height);
  }

  private void resizeFont() {
    fontScale = (float) canvas.getHeight() / 200;

    FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("Alagard.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

    textFont = generator.generateFont(parameter);
    textFont.setUseIntegerPositions(false);
    textFont.getData().setScale(fontScale);
    textFont.setColor(Color.WHITE);
    generator.dispose();
  }

  /**
   * Called when the Screen should render itself.
   *
   * We defer to the other methods update() and draw(). However, it is VERY
   * important
   * that we only quit AFTER a draw.
   *
   * @param delta Number of seconds since last animation frame
   */
  public void render(float delta) {
    if (active) {
      update(delta);
      draw();

      if (!begunAssetLoading) {
        // Start loading the real assets
        assets.loadAssets();
        assets.finishLoading();
        begunAssetLoading = true;
      }

      // We are are ready, notify our listener
      if (isReady() && listener != null) {
        listener.exitScreen(this, exitCode);
      }
    }
  }

  /**
   * Returns true if all assets are loaded and the player is ready to go.
   *
   * @return true if the player is ready to go
   */
  public boolean isReady() {
    return timer >= budget;
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
  }

  public AssetDirectory getAssets() { return assets; }

  @Override
  public void show() {
    active = true;
  }

  @Override
  public void hide() {
    active = false;
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
    canvas.resize();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {

  }
}
