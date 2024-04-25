package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.input.Controllable;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.ui.PauseMenu;
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

  private TitleSelection selection = TitleSelection.PLAY;

  /** Font for display text */
  private BitmapFont textFont;

  private float textSpacingY;

  private boolean toggle;

  private static final int MENU_SIZE = 3;

  private int exitCode;

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
      return values()[(optionValue > 0 ? optionValue - 1 : optionValue)];
    }

    public TitleSelection cycleDown() {
      return values()[(optionValue < 3 ? optionValue + 1 : optionValue)];
    }
  }

  public TitleScreen(String file, GameCanvas canvas) {
    this.canvas = canvas;

    internal = new AssetDirectory("loading.json");
    internal.loadAssets();
    internal.finishLoading();

    background = internal.getEntry("title:background", Texture.class);
    textFont = internal.getEntry("loading:alagard", BitmapFont.class);

    // Calculating spacings between menu options
    GlyphLayout layout = new GlyphLayout(textFont, "Sample");
    textSpacingY = layout.height + 25;
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * <p>The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
  }

  public void update() {}

  /** Switches to a lower menu item */
  public void cycleDown() {
    if (selection.optionValue < MENU_SIZE-1) {
      selection = selection.cycleDown();
    }
  }

  /** Switches to a higher menu item */
  public void cycleUp() {
    if (selection.optionValue > 0) selection = selection.cycleUp();
  }

  public void draw() {
    canvas.begin();
    canvas.draw(background, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
    drawMenu();
    canvas.end();
  }

  private void drawMenu() {
    float y_offset = -150f;
    for (TitleSelection s : TitleSelection.values()) {
      if (selection == s) {
        canvas.drawTextCentered(
                s.optionName,
                textFont, y_offset-5,
                Color.GRAY);
      }

      canvas.drawTextCentered(
              s.optionName,
              textFont, y_offset,
              Color.BLACK);

      y_offset -= 120f;
    }
  }

  public void render(float delta) {
    update();
    draw();
  }

  /** Selects the current menu option */
  public void pressInteract() {
    switch (selection) {
      case PLAY -> listener.exitScreen(this, 1);
      case OPTIONS -> System.out.println("Options");
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
