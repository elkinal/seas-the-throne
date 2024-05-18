package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.Controllers;
import edu.cornell.jade.seasthethrone.util.ScreenListener;
import edu.cornell.jade.seasthethrone.util.XBoxController;

import java.util.HashMap;

/**
 * Options screen which contains settings for player preferences and keybindings.
 *
 * <p>Keybindings: Keybindings are saved into a hashmap with key = action, value = keybind (string).
 * This hashmap will be saved in the save file (has default values which are there from beginning)
 * and will be loaded by InputController to get keybindings.
 *
 * <p>Based off code from Crested Gecko Studios' Bubblegum Bandit (2023)
 */
public class OptionScreen implements Screen {
  /** Internal assets for the options screen */
  private AssetDirectory internal;

  /** Background texture for start-up */
  private Texture background;

  /** The game canvas */
  private GameCanvas canvas;

  /** Custom colors */
  private Color blue = new Color(90 / 255f, 148 / 255f, 156 / 255f, 1);

  /** XBox Controller support */
  private XBoxController xbox;

  /** The stage for all the buttons */
  private Stage stage;

  /** Font for display text */
  private BitmapFont textFont;

  /** Heading style */
  private Label.LabelStyle headingStyle;

  /** Regular button style */
  private Label.LabelStyle buttonStyle;

  /** Button for going back to previous screen */
  private TextButton backButton;

  /** Button for toggling aim assist */
  private TextButton aimAssistButton;

  /** Button for setting dash direction as indicator or movement direction */
  private TextButton dashControlButton;

  /** Button for setting attack button (controller only) */
  private TextButton attackButton;

  /** Button for setting dash button (controller only) */
  private TextButton dashButton;

  /** Button for resetting settings to default */
  private TextButton resetButton;

  /** Hashmap containing default values for keybindings/preferences */
  private HashMap<TextButton, String> defaultSettings;

  /** Hashmap containing player's CURRENT keybindings/preferences (to update preferences) */
  private HashMap<String, String> currentControls;

  /** Dictionary that maps buttons to text (to update button, contains current player mapping) */
  private HashMap<TextButton, String> buttonMaps;

  /** Array of all buttons (for controller) */
  private TextButton[] buttons;

  /** Array of all button names (for preferences) */
  private String[] buttonNames;

  /** The hover index of the controller */
  private int hoverIndex;

  /** Scroll toggle cooldown (only move down one at a time): true if can scroll, false if not */
  boolean canScroll;

  private final int KEY_DELAY = 5;

  private final int CONTROL_DELAY = 10;

  /** Int to check if cooldown is reached (for clicks: faster cooldown than scroll) */
  private int clickCount;

  /** The screen listener to know when to exit screen */
  private ScreenListener listener;

  /** Preference file to save settings */
  private Preferences prefs;

  /** The table that contains and orients all the buttons and labels */
  private Table controlsTable;

  /** Indicates screen should close (exit options page) */
  private boolean exit;

  /** Enum to see if option screen should exit to the title or game Default exit to title */
  private int exitTo;

  public OptionScreen(String file, GameCanvas canvas) {
    internal = new AssetDirectory(file);
    internal.loadAssets();
    internal.finishLoading();

    this.prefs = Gdx.app.getPreferences("options");

    this.canvas = canvas;
    defaultSettings = new HashMap<>();
    buttonMaps = new HashMap<>();
    currentControls = new HashMap<>();
    buttonNames =
        new String[] {
          "aimAssist", "dashControl", "attackButton", "dashButton", "backButton", "resetButton"
        };
    exitTo = GDXRoot.EXIT_TITLE;

    // initialize controller
    if (Controllers.get().getControllers().size > 0) {
      xbox = Controllers.get().getXBoxControllers().get(0);
    }
    hoverIndex = 0;
    clickCount = 0;
    canScroll = true;

    background = internal.getEntry("options:background", Texture.class);
    textFont = internal.getEntry("loading:alagard", BitmapFont.class);
    headingStyle = new Label.LabelStyle(textFont, Color.WHITE);
    buttonStyle = new Label.LabelStyle(textFont, Color.WHITE);
    // make stage for options screen
    stage = new Stage();
    Gdx.input.setInputProcessor(stage);

    // make table for all the controls options
    controlsTable = new Table();
    controlsTable.setFillParent(true);
    stage.addActor(controlsTable);

    makeControls();
  }

  public void setViewport(FitViewport v) {
    stage.setViewport(v);
  }

  public void setExit(int exitCode) {
    exitTo = exitCode;
  }

  /** Makes the controls table and adds buttons/listeners to it. */
  private void makeControls() {
    // make labels for all buttons
    Label controls = new Label("CONTROLS", headingStyle);
    Label aimAssist = new Label("Aim Assist", buttonStyle);
    Label dashControl = new Label("Dash Direction", buttonStyle);
    Label attackBindController = new Label("Attack (Controller Only)", buttonStyle);
    Label dashBindController = new Label("Dash (Controller Only)", buttonStyle);

    // make text smaller
    aimAssist.setFontScale(.5f);
    dashControl.setFontScale(.5f);
    attackBindController.setFontScale(.5f);
    dashBindController.setFontScale(.5f);

    // set appearance of buttons
    TextButton.TextButtonStyle buttonStyle =
        new TextButton.TextButtonStyle(null, null, null, textFont);
    // color on hover (mouse)
    buttonStyle.overFontColor = blue;

    aimAssistButton = new TextButton("", buttonStyle);
    aimAssistButton.getLabel().setFontScale(.5f);

    dashControlButton = new TextButton("", buttonStyle);
    dashControlButton.getLabel().setFontScale(.5f);
    stage.addActor(dashControlButton);

    attackButton = new TextButton("", buttonStyle);
    attackButton.getLabel().setFontScale(.5f);
    stage.addActor(attackButton);

    dashButton = new TextButton("", buttonStyle);
    dashButton.getLabel().setFontScale(.5f);
    stage.addActor(dashButton);

    // add all defaults to the maps and array
    defaultSettings.put(aimAssistButton, "Off");
    defaultSettings.put(dashControlButton, "Movement");
    defaultSettings.put(attackButton, "LT");
    defaultSettings.put(dashButton, "RT");

    // fill controls as preference, or default if prefs don't exist
    buttonMaps.put(aimAssistButton, prefs.getString("aimAssist", "Off"));
    currentControls.put("aimAssist", prefs.getString("aimAssist", "Off"));
    buttonMaps.put(dashControlButton, prefs.getString("dashControl", "Movement"));
    currentControls.put("dashControl", prefs.getString("dashControl", "Movement"));
    buttonMaps.put(attackButton, prefs.getString("attackButton", "LT"));
    currentControls.put("attackButton", prefs.getString("attackButton", "LT"));
    buttonMaps.put(dashButton, prefs.getString("dashButton", "RT"));
    currentControls.put("dashButton", prefs.getString("dashButton", "RT"));

    TextButton[] button =
        new TextButton[] {aimAssistButton, dashControlButton, attackButton, dashButton};

    for (int i = 0; i < button.length; i++) {
      button[i].setText(currentControls.get(buttonNames[i]));
    }

    // back and reset buttons
    backButton = new TextButton("BACK", buttonStyle);
    backButton.getLabel().setFontScale(.5f);

    resetButton = new TextButton("RESET CONTROLS", buttonStyle);
    resetButton.getLabel().setFontScale(.5f);

    controlsTable.add(controls).padBottom(40);
    controlsTable.row();

    controlsTable.add(aimAssist).pad(80, 100, 0, 0);
    controlsTable.add(aimAssistButton);
    controlsTable.row();

    controlsTable.add(dashControl).pad(80, 100, 0, 0);
    controlsTable.add(dashControlButton);
    controlsTable.row();

    controlsTable.add(attackBindController).pad(80, 100, 0, 0);
    controlsTable.add(attackButton);
    controlsTable.row();

    controlsTable.add(dashBindController).pad(80, 100, 0, 0);
    controlsTable.add(dashButton);
    controlsTable.row();

    // back and reset buttons
    controlsTable.add(backButton).padTop(40).bottom();
    controlsTable.add(resetButton).padTop(40).bottom();

    buttons =
        new TextButton[] {
          aimAssistButton, dashControlButton, attackButton, dashButton, backButton, resetButton
        };

    addListeners();
  }

  /** Add listeners to buttons */
  private void addListeners() {
    // button listener to listen for first click
    InputListener buttonListener =
        new InputListener() {
          @Override
          public void touchUp(InputEvent event, float x, float y, int pointer, int button) {}

          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
          }
        };

    attackButton.addListener(buttonListener);
    dashButton.addListener(buttonListener);

    // custom click listeners
    aimAssistButton.addListener(
        new ClickListener() {
          public void clicked(InputEvent event, float x, float y) {
            if (buttonMaps.get(aimAssistButton).equals("Off")) {
              buttonMaps.put(aimAssistButton, "On");
              currentControls.put("aimAssist", "On");
              aimAssistButton.setText("On");
            } else {
              buttonMaps.put(aimAssistButton, "Off");
              currentControls.put("aimAssist", "Off");
              aimAssistButton.setText("Off");
            }
          }
        });

    dashControlButton.addListener(
        new ClickListener() {
          public void clicked(InputEvent event, float x, float y) {
            if (buttonMaps.get(dashControlButton).equals("Movement")) {
              buttonMaps.put(dashControlButton, "Indicator");
              currentControls.put("dashControl", "Indicator");
              dashControlButton.setText("Indicator");
            } else {
              buttonMaps.put(dashControlButton, "Movement");
              currentControls.put("dashControl", "Movement");
              dashControlButton.setText("Movement");
            }
          }
        });

    resetButton.addListener(
        new ClickListener() {
          public void clicked(InputEvent event, float x, float y) {
            setDefault();
          }
        });

    backButton.addListener(
        new ClickListener() {
          public void clicked(InputEvent event, float x, float y) {
            exit = true;
          }
        });
  }

  private void xboxListener() {
    // controller buttonstyles
    TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(null, null, null, textFont);
    style.fontColor = Color.WHITE;
    TextButton.TextButtonStyle hoverStyle =
        new TextButton.TextButtonStyle(null, null, null, textFont);
    hoverStyle.fontColor = blue;

    /** ---- scrolling to the option */
    // go down
    if ((xbox.getLeftY() >= 1 || xbox.getRightY() >= 1) && canScroll) {
      if (hoverIndex >= buttons.length - 1) {
        hoverIndex = 0;
      } else {
        hoverIndex++;
      }
      canScroll = false;
    }
    // go up
    else if ((xbox.getLeftY() <= -1 || xbox.getRightY() <= -1) && canScroll) {
      if (hoverIndex == 0) {
        hoverIndex = buttons.length - 1;
      } else {
        hoverIndex--;
      }
      canScroll = false;
    }
    // no movement
    else if (xbox.getLeftY() <= 0.2
        && xbox.getLeftY() >= -0.2
        && xbox.getRightY() <= 0.2
        && xbox.getRightY() >= -0.2) {
      canScroll = true;
    }

    for (int i = 0; i < buttons.length; i++) {
      buttons[i].setStyle(style);
    }

    buttons[hoverIndex].setStyle(hoverStyle);

    /** --------- selecting the option */
    if (clickCount == 0) {
      if (xbox.getB()) {
        // 2 options only
        if (hoverIndex == 0) {
          if (buttonMaps.get(aimAssistButton).equals("Off")) {
            buttonMaps.put(aimAssistButton, "On");
            aimAssistButton.setText("On");
            currentControls.put("aimAssist", "On");
          } else {
            buttonMaps.put(aimAssistButton, "Off");
            aimAssistButton.setText("Off");
            currentControls.put("aimAssist", "Off");
          }
        } else if (hoverIndex == 1) {
          if (buttonMaps.get(dashControlButton).equals("Movement")) {
            buttonMaps.put(dashControlButton, "Indicator");
            dashControlButton.setText("Indicator");
            currentControls.put("dashControl", "Indicator");
          } else {
            buttonMaps.put(dashControlButton, "Movement");
            dashControlButton.setText("Movement");
            currentControls.put("dashControl", "Movement");
          }
        } else if (hoverIndex == 4) {
          exit = true;
        } else if (hoverIndex == 5) {
          setDefault();
        }
      }
      /** ------- changing keybindings: cannot remap B, X, or Y (interact, pause, restart) */
      // change attack button
      if (xbox.getPressed() != null) {
        String b = xbox.getPressed();
        System.out.println(b);
        if (b.equals("B") || b.equals("X") || b.equals("Y")) {
          TextButton.TextButtonStyle badKeyStyle =
              new TextButton.TextButtonStyle(null, null, null, textFont);
          badKeyStyle.fontColor = Color.RED;
          attackButton.setStyle(badKeyStyle);
        } else if (hoverIndex == 2) {
          buttonMaps.put(attackButton, xbox.getPressed());
          currentControls.put("attackButton", xbox.getPressed());
          attackButton.setText(xbox.getPressed());
        }
        // change dash button
        else if (hoverIndex == 3) {
          buttonMaps.put(dashButton, xbox.getPressed());
          currentControls.put("dashButton", xbox.getPressed());

          dashButton.setText(xbox.getPressed());
        }
      }
    }

    if (clickCount >= CONTROL_DELAY) {
      clickCount = 0;
    } else clickCount++;
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * <p>The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
  }

  private void setDefault() {
    // update button draw
    for (TextButton k : buttonMaps.keySet()) {
      k.setText(defaultSettings.get(k));
    }

    // update preferences
    for (int i = 0; i < buttonMaps.size(); i++) {
      currentControls.put(buttonNames[i], defaultSettings.get(buttons[i]));
    }
  }

  public void draw() {
    canvas.clear(Color.BLACK);
    canvas.begin();
    stage.getBatch().begin();
    stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

    // draw the background
    float ox = -canvas.getWidth() / 2f;
    float oy = -canvas.getHeight() / 2f;
    canvas.draw(
        background,
        Color.WHITE,
        0,
        0,
        stage.getViewport().getWorldWidth(),
        stage.getViewport().getWorldHeight());

    if (xbox != null) {
      xboxListener();
    } else {
      keyboardListener();
    }
    stage.getBatch().end();
    canvas.end();
    stage.draw();
  }

  public void update() {
    stage.getViewport().update(canvas.getWidth(), canvas.getHeight());
    stage.getViewport().apply();
  }

  @Override
  public void render(float delta) {
    update();
    draw();
    stage.act(delta);
    if (exit) {
      saveOptions();
      listener.exitScreen(this, exitTo);
    }
  }

  /** Saves the current options to a preference file */
  private void saveOptions() {
    for (int i = 0; i < buttonMaps.size(); i++) {
      prefs.putString(buttonNames[i], currentControls.get(buttonNames[i]));
    }
    System.out.println(prefs.get());
    prefs.flush();
  }

  /**
   * ======================================================================
   *
   * <p>Make OptionScreen scrollable on keyboard
   *
   * <p>======================================================================
   */
  private void keyboardListener() {
    // controller buttonstyles
    TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(null, null, null, textFont);
    style.fontColor = Color.WHITE;
    TextButton.TextButtonStyle hoverStyle =
        new TextButton.TextButtonStyle(null, null, null, textFont);
    hoverStyle.fontColor = blue;

    /** --------- scrolling to the option */
    // go down
    if (Gdx.input.isKeyJustPressed(Input.Keys.S) && canScroll) {
      if (hoverIndex >= buttons.length - 1) {
        hoverIndex = 0;
      } else if (hoverIndex == 1 || hoverIndex == 2) {
        hoverIndex = 4;
      } else {
        hoverIndex++;
      }
      canScroll = false;
      System.out.println("go down");
    }
    // go up
    else if (Gdx.input.isKeyJustPressed(Input.Keys.W) && canScroll) {
      if (hoverIndex == 0) {
        hoverIndex = buttons.length - 1;
      } else if (hoverIndex == 3 || hoverIndex == 4) {
        hoverIndex = 1;
      } else {
        hoverIndex--;
      }
      canScroll = false;
      System.out.println("go up");
    } else {
      canScroll = true;
      System.out.println("can scroll true");
    }

    for (int i = 0; i < buttons.length; i++) {
      buttons[i].setStyle(style);
    }

    buttons[hoverIndex].setStyle(hoverStyle);

    /** selecting the option (can only select non controller settings) */
    if (clickCount == 0) {
      if (Gdx.input.isKeyPressed(Input.Keys.E) || Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
        // 2 options only
        if (hoverIndex == 0) {
          if (buttonMaps.get(aimAssistButton).equals("Off")) {
            buttonMaps.put(aimAssistButton, "On");
            aimAssistButton.setText("On");
            currentControls.put("aimAssist", "On");
          } else {
            buttonMaps.put(aimAssistButton, "Off");
            aimAssistButton.setText("Off");
            currentControls.put("aimAssist", "Off");
          }
        } else if (hoverIndex == 1) {
          if (buttonMaps.get(dashControlButton).equals("Movement")) {
            buttonMaps.put(dashControlButton, "Indicator");
            dashControlButton.setText("Indicator");
            currentControls.put("dashControl", "Indicator");
          } else {
            buttonMaps.put(dashControlButton, "Movement");
            dashControlButton.setText("Movement");
            currentControls.put("dashControl", "Movement");
          }
        } else if (hoverIndex == 4) {
          exit = true;
        } else if (hoverIndex == 5) {
          setDefault();
        }
      }
    }
    if (clickCount >= KEY_DELAY) {
      clickCount = 0;
    } else clickCount++;
  }

  @Override
  public void show() {
    hoverIndex = 0;
    exit = false;
  }

  @Override
  public void resize(int width, int height) {
    canvas.resize();
    stage.getViewport().update(width, height, true);
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
