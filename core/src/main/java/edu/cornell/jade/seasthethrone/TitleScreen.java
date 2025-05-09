package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.audio.SoundPlayer;
import edu.cornell.jade.seasthethrone.input.Controllable;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class TitleScreen implements Screen, Controllable {
  /** Sound player for the title screen */
  private SoundPlayer soundPlayer;

  public TitleScreen() {
  }

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

  private float fontScale;

  private boolean toggle;

  /** Delay to prevent infinite loop between options and title screen */
  private final int PRESS_DELAY = 5;

  /** Timer since last click */
  private int pressTimer;

  private float MENU_SPACING = 200f;

  public enum TitleSelection {
    PLAY(0, "Play"),

    NEW_GAME(1, "New Game"),
    OPTIONS(2, "Options"),
    QUIT(3, "Quit");

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
      return values()[(optionValue < TitleSelection.values().length - 1 ? optionValue + 1 : 0)];
    }
  }

  public TitleScreen(String file, GameCanvas canvas, ScreenViewport viewport, SoundPlayer soundPlayer) {
    this.soundPlayer = soundPlayer;
    this.canvas = canvas;
    this.viewport = viewport;
    fontScale = (float) canvas.getHeight() / 275;

    internal = new AssetDirectory(file);
    internal.loadAssets();
    internal.finishLoading();

    background = internal.getEntry("title:background", Texture.class);
    logo = internal.getEntry("title:logo", Texture.class);
    textFont = internal.getEntry("loading:alagard", BitmapFont.class);
    pressTimer = 0;

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
    float ox = -canvas.getWidth() / 2f;
    float oy = -canvas.getHeight() / 2f;
    canvas.draw(background, Color.WHITE, ox, oy, canvas.getWidth(), canvas.getHeight());

    // draw the logo
    float scale = 0.4f * canvas.getHeight() / logo.getHeight();
    ox = -canvas.getWidth() / 2f + 10f;

    canvas.draw(logo, Color.WHITE, ox, 0, scale * logo.getWidth(), scale * logo.getHeight());

    // draw the menu
    // NOTE: this is just a hardcoded magic number to get text scaling right
    textFont.dispose();
    resizeFont();

    float y_offset = -canvas.getHeight() / 15f;
    float x_offset = canvas.getWidth() * (1 / 20f - 1 / 2f);
    float menuSpacing = canvas.getHeight() / 10f;
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
    if (pressTimer == 0) {
      soundPlayer.playSoundEffect("menu-select");
      switch (selection) {
        case PLAY -> {
          ((GDXRoot) listener).setLoadSave(true);
          listener.exitScreen(this, 1);
        }
        case NEW_GAME -> {
          ((GDXRoot) listener).setLoadSave(false);
          listener.exitScreen(this, 1);
        }
        case OPTIONS -> {
          listener.exitScreen(this, 3);
        }
        case QUIT -> System.exit(0);
      }
    }
    if (pressTimer >= PRESS_DELAY) pressTimer = 0;
    else pressTimer++;
  }

  /** Selects between menu options */
  public void moveVertical(float movement) {
    if (movement > 0 && !toggle) {
      soundPlayer.playSoundEffect("menu-change");
      cycleUp();
      toggle = true;
    }
    if (movement < 0 && !toggle) {
      soundPlayer.playSoundEffect("menu-change");
      cycleDown();
      toggle = true;
    }
    if (movement == 0) {
      toggle = false;
    }
  }

  private void resizeFont() {
    fontScale = (float) canvas.getHeight() / 275;

    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Alagard.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter =
        new FreeTypeFontGenerator.FreeTypeFontParameter();

    textFont = generator.generateFont(parameter);
    textFont.setUseIntegerPositions(false);
    textFont.getData().setScale(fontScale);
    textFont.setColor(Color.WHITE);
    generator.dispose();
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
