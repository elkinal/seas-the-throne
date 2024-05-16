package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.input.Controllable;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class TitleScreen implements Screen, Controllable {

  /** Internal assets for this title screen */
  private AssetDirectory internal;

  /** Reference to GameCanvas created by the root */
  private GameCanvas canvas;

  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  /** Background texture for start-up */
  private Texture background;

  private ScreenViewport viewport;

  /** Logo texture */
  private Texture logo;

  private TitleSelection selection = TitleSelection.PLAY;

  /** Font for display text */
  private BitmapFont textFont;

  private boolean toggle;

  private float MENU_SPACING = 200f;

  public enum TitleSelection {
    PLAY(0, "Play"),
    OPTIONS(1, "Options"),
    QUIT(2, "Quit");

    public final String optionName;
    public final int optionValue;

    private TitleSelection(int value, String name) {
      this.optionName = name;
      this.optionValue = value;
    }

    public TitleSelection cycleUp() {
      return values()[(optionValue > 0 ? optionValue - 1 : TitleSelection.values().length - 1)];
    }

    public TitleSelection cycleDown() {
      return values()[(optionValue < 2 ? optionValue + 1 : 0)];
    }
  }

  public TitleScreen(String file, GameCanvas canvas, ScreenViewport viewport) {
    this.canvas = canvas;
    this.viewport = viewport;

    internal = new AssetDirectory(file);
    internal.loadAssets();
    internal.finishLoading();

    background = internal.getEntry("title:background", Texture.class);
    logo = internal.getEntry("title:logo", Texture.class);
    textFont = internal.getEntry("loading:alagard", BitmapFont.class);

    // Calculating spacings between menu options
    canvas.resize();
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * <p>The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
  }

  public void update() {
    canvas.resize();
    viewport.update(canvas.getWidth(), canvas.getHeight());
    viewport.apply();
  }

  public void resize() {}

  /** Switches to a lower menu item */
  public void cycleDown() {
    selection = selection.cycleDown();
  }

  /** Switches to a higher menu item */
  public void cycleUp() {
    selection = selection.cycleUp();
  }

  public void draw() {
    canvas.clear(Color.BLACK);
    canvas.begin();
    canvas.getSpriteBatch().setProjectionMatrix(viewport.getCamera().combined);

    // draw the background
    float ox = -canvas.getWidth()/2f;
    float oy = -canvas.getHeight()/2f;
    canvas.draw(background, Color.WHITE, ox, oy, canvas.getWidth(), canvas.getHeight());

    // draw the logo
    float scale = Math.min(1 / 2f, (float) canvas.getWidth() / logo.getWidth());
    float width = logo.getWidth() * scale;
    ox = -canvas.getWidth()/2f + 10f;

    canvas.draw(logo, Color.WHITE, ox, 0, width, scale * logo.getHeight());

    // draw the menu
    float y_offset = -canvas.getHeight()/15f;
    float x_offset =  canvas.getWidth()*(1/20f - 1/2f);
    float menuSpacing = canvas.getHeight() / 8f;
    for (TitleSelection s : TitleSelection.values()) {
      if (selection == s) {
        canvas.drawText(s.optionName, textFont, x_offset, y_offset, Color.GOLDENROD);
        canvas.drawText(s.optionName, textFont, x_offset + 3, y_offset + 6, Color.WHITE);
      } else {
        canvas.drawText(s.optionName, textFont, x_offset, y_offset, Color.WHITE);
      }

      y_offset -= menuSpacing;
    }
    canvas.end();
  }

  public void render(float delta) {
    update();
    draw();
  }

  /** Selects the current menu option */
  public void pressInteract() {
    switch (selection) {
      case PLAY -> {
        listener.exitScreen(this, 1);
      }
      case OPTIONS -> {
        listener.exitScreen(this, 3);
      }
      case QUIT -> System.exit(0);
    }
  }

  /** Selects between menu options */
  public void moveVertical(float movement) {
    if (movement > 0 && !toggle) {
      cycleUp();
      toggle = true;
    }
    if (movement < 0 && !toggle) {
      cycleDown();
      toggle = true;
    }
    if (movement == 0) {
      toggle = false;
    }
  }

  @Override
  public Vector2 getLocation() {
    return null;
  }

  @Override
  public void show() {}

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
    canvas.resize();
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {}
}
