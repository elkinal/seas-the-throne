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

import javax.swing.*;
import javax.swing.text.View;

public class TitleScreen implements Screen, Controllable {

  /** Internal assets for this title screen */
  private AssetDirectory internal;

  /** Reference to GameCanvas created by the root */
  private GameCanvas canvas;

  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  /** Background texture for start-up */
  private Texture background;

  private Viewport viewport;

  /** Logo texture */
  private Texture logo;

  private TitleSelection selection = TitleSelection.PLAY;

  /** Font for display text */
  private BitmapFont textFont;

  private float textSpacingY;

  private boolean toggle;

  private static final int MENU_SIZE = 3;

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
      return values()[(optionValue > 0 ? optionValue - 1 : TitleSelection.values().length-1)];
    }

    public TitleSelection cycleDown() {
      return values()[(optionValue < 2 ? optionValue + 1 : 0)];
    }
  }

  public TitleScreen(String file, GameCanvas canvas, Viewport viewport) {
    this.canvas = canvas;
    this.viewport = viewport;

    internal = new AssetDirectory("loading.json");
    internal.loadAssets();
    internal.finishLoading();

    background = internal.getEntry("title:background", Texture.class);
    logo = internal.getEntry("title:logo", Texture.class);
    textFont = internal.getEntry("loading:alagard", BitmapFont.class);

    // Calculating spacings between menu options
    GlyphLayout layout = new GlyphLayout(textFont, "Sample");
    textSpacingY = layout.height + 25;
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
    viewport.update(canvas.getWidth(), canvas.getHeight());
    viewport.apply();
  }

  public void resize() {

  }

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
    //    canvas.getSpriteBatch().setProjectionMatrix(canvas.getCamera().combined);
    float ratio = (float) logo.getHeight()/logo.getWidth();
    float width = canvas.getWidth() / 2f;
    float height = ratio*width;
    canvas.draw(
        logo,
        Color.WHITE,
        (canvas.getWidth() - width) / 2f,
        canvas.getHeight() - 1.1f*height,
        width,
        height);
    drawMenu();
    canvas.end();
  }

  private void drawMenu() {
    float y_offset = -250f;
    for (TitleSelection s : TitleSelection.values()) {
      canvas.drawTextCentered(s.optionName, textFont, y_offset, Color.WHITE);

      if (selection == s) {
        canvas.drawTextCentered(s.optionName, textFont, y_offset + 6, Color.GOLDENROD);
      }

      y_offset -= 150f;
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
