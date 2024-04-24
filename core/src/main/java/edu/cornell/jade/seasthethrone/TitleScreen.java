package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class TitleScreen implements Screen {

  /** Internal assets for this title screen */
  private AssetDirectory internal;

  /** Reference to GameCanvas created by the root */
  private GameCanvas canvas;

  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  /** Background texture for start-up */
  private Texture background;

  /** Font for display text */
  private BitmapFont textFont;

  private int exitCode;


  public TitleScreen(String file, GameCanvas canvas) {
    this.canvas = canvas;

    internal = new AssetDirectory( "loading.json" );
    internal.loadAssets();
    internal.finishLoading();

    background = internal.getEntry("title:background", Texture.class);

  }

  /**
   * Sets the ScreenListener for this mode
   *
   * The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
  }

  public void update() {
    if (buttonPressed()) {
      listener.exitScreen(this, 1);
    }
  }

  public void draw() {
    canvas.begin();
    canvas.draw(background, Color.WHITE, 0,0,canvas.getWidth(),canvas.getHeight());
    canvas.end();
  }

  public void render(float delta) {
    update();
    draw();
  }

  private boolean buttonPressed() {
    return Gdx.input.isKeyPressed(Input.Keys.P);
  }


  @Override
  public void show() {}

  @Override
  public void resize(int i, int i1) {}

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {}
}
