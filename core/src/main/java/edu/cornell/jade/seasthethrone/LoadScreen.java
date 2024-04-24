package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class LoadScreen implements Screen {
  /** Internal assets for this loading screen */
  private AssetDirectory internal;
  /** The actual assets to be loaded */
  private AssetDirectory assets;

  /** Reference to GameCanvas created by the root */
  private GameCanvas canvas;

  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  /** Whether or not this player mode is still active */
  private boolean active;

  /** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
  private int budget;

  private int timer;

  /** Background texture for start-up */
  private Texture background;

  /** Font for display text */
  private BitmapFont textFont;

  /** Default budget for asset loader (do nothing but load 60 fps) */
  private static int DEFAULT_BUDGET = 100;

  private int exitCode;

  /**
   * Creates a LoadScreen with the default budget, size and position.
   *
   * @param file  	The asset directory to load in the background
   * @param canvas 	The game canvas to draw to
   */
  public LoadScreen(String file, GameCanvas canvas, int exitCode) {
    this(file, canvas, DEFAULT_BUDGET, exitCode);
  }

  public LoadScreen(String file, GameCanvas canvas, int millis, int exitCode) {
    this.canvas = canvas;
    budget = millis;
    timer = 0;
    this.exitCode = exitCode;

    internal = new AssetDirectory( "loading.json" );
    internal.loadAssets();
    internal.finishLoading();

    background = internal.getEntry( "background", Texture.class );


//    /** LOADING IN FONT, might be better to have an AssetDirectory later */
//    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Alagard.ttf"));
//    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//    parameter.size = 100; // font size
//    textFont = generator.generateFont(parameter);

    textFont = internal.getEntry("loading:alagard", BitmapFont.class);

    // Start loading the real assets
    assets = new AssetDirectory( file );
    assets.loadAssets();
    active = true;

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
    timer += 1;
  }

  private void draw() {
    canvas.begin();
    ;
    canvas.draw(background, Color.BLACK, 0, 0,
        0, 0, 0, 10f, 10f);

    canvas.drawTextCentered("Loading...", textFont, 0);
    canvas.end();
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

  @Override
  public void show() {
    active = true;
  }

  @Override
  public void hide() {
    active = false;
  }

  @Override
  public void resize(int i, int i1) {

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
