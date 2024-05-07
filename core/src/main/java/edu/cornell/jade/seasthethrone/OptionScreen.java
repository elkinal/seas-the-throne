package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
 * Options screen which contians settings for player preferences and keybindings.
 *
 * <p>Keybindings: Keybindings are saved into a hashmap with key = action, value = keybind. This
 * hashmap will be saved in the save file (has default values which are there from beginning) and
 * will be loaded by InputController to get keybindings.
 */
public class OptionScreen implements Screen {
  /** Internal assets for this title screen */
  private AssetDirectory internal;

  /** Background texture for start-up */
  private Texture background;

  /** The game canvas */
  private GameCanvas canvas;

  /** === Custom colors */
  private Color blue = new Color(90 / 255f, 148 / 255f, 156 / 255f, 1);

  private Color coral = new Color(185 / 255f, 75 / 255f, 78 / 255f, 1);
  private Color sand = new Color(223 / 255f, 209 / 255f, 160 / 255f, 1);

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

  /** Button for toggling easy mode (aim assist) True = off, False = on */
  private TextButton easyModeButton;

  /**
   * Button for setting dash direction as indicator or movement direction True = movement direction,
   * False = indicator
   */
  private TextButton dashControlButton;

  /**
   * Button for setting attack button (controller only) True = left trigger, False = right trigger
   */
  private TextButton attackButton;

  /** Button for setting dash button (controller only) True = right trigger, False = left trigger */
  private TextButton dashButton;

  /** Button for resetting settings to default */
  private TextButton resetButton;

  /** Hashmap containing default values for keybindings/preferences */
  private HashMap<TextButton, String> defaultSettings;

  // todo: xbox controller support-- cycle through buttons when going up/down
  // todo: for keymapping, press a on xbox to select the keybinding, then click button to change
  // todo: for preference (true/false), press a on xbox to change to the other preference

  /** Hashmap containing player's CURRENT keybindings/preferences TODO: Use? */
  private HashMap<String, String> currentControls;

  /** Dictionary that maps buttons to text (to change button appearance when changing settings) */
  private HashMap<TextButton, String> buttonNames;

  /** Array of all buttons (for controller) */
  private TextButton[] buttons;

  /** The hover index of the controller */
  private int hoverIndex;

  private int cooldown = 10;
  private int moveCount;

  /** The screen listener to know when to exit screen */
  private ScreenListener listener;

  /** The table that contains and orients all the buttons and labels */
  private Table controlsTable;

  /** Indicates screen should close (exit options page) */
  private boolean exit;

  public OptionScreen(String file, GameCanvas canvas) {
    internal = new AssetDirectory(file);
    internal.loadAssets();
    internal.finishLoading();

    this.canvas = canvas;
    defaultSettings = new HashMap<>();
    currentControls = new HashMap<>();
    buttonNames = new HashMap<>();

    // initialize controller
    if (Controllers.get().getControllers().size > 0) {
      xbox = Controllers.get().getXBoxControllers().get(0);
    }
    hoverIndex = 0;
    moveCount = 0;

    background = internal.getEntry("title:background", Texture.class);
    textFont = internal.getEntry("loading:alagard", BitmapFont.class);
    headingStyle = new Label.LabelStyle(textFont, blue);
    buttonStyle = new Label.LabelStyle(textFont, Color.WHITE);
    // make stage for options screen
    stage = new Stage();
    Gdx.input.setInputProcessor(stage);

    // make table for all the controls options
    controlsTable = new Table();
    controlsTable.setFillParent(true);
    //    controlsTable.setDebug(true);
    //    controlsTable.align(Align.topLeft);
    stage.addActor(controlsTable);

    setDefault();
    makeControls();
  }

  public void setViewport(FitViewport v) {
    stage.setViewport(v);
  }

  /** Makes the controls table and adds buttons/listeners to it. */
  private void makeControls() {
    // make labels for all buttons
    Label controls = new Label("CONTROLS", headingStyle);
    Label easyMode = new Label("Easy Mode", buttonStyle);
    Label dashControl = new Label("Dash Direction", buttonStyle);
    Label attackBindController = new Label("Attack (Controller Only)", buttonStyle);
    Label dashBindController = new Label("Dash (Controller Only)", buttonStyle);

    // make text smaller
    easyMode.setFontScale(.5f);
    dashControl.setFontScale(.5f);
    attackBindController.setFontScale(.5f);
    dashBindController.setFontScale(.5f);

    // set appearance of buttons
    TextButton.TextButtonStyle buttonStyle =
        new TextButton.TextButtonStyle(null, null, null, textFont);
    // color on hover (mouse)
    buttonStyle.overFontColor = coral;

    easyModeButton = new TextButton("", buttonStyle);
    easyModeButton.getLabel().setFontScale(.5f);

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
    defaultSettings.put(easyModeButton, "Off");
    defaultSettings.put(dashControlButton, "Movement");
    defaultSettings.put(attackButton, "LT");
    defaultSettings.put(dashButton, "RT");

    buttonNames.put(easyModeButton, "Off");
    buttonNames.put(dashControlButton, "Movement");
    buttonNames.put(attackButton, "LT");
    buttonNames.put(dashButton, "RT");

    TextButton[] button =
        new TextButton[] {easyModeButton, dashControlButton, attackButton, dashButton};

    for (TextButton t : button) {
      t.setText(defaultSettings.get(t));
    }

    // back and reset buttons
    backButton = new TextButton("BACK", buttonStyle);
    backButton.getLabel().setFontScale(.5f);

    resetButton = new TextButton("RESET CONTROLS", buttonStyle);
    resetButton.getLabel().setFontScale(.5f);

    controlsTable.add(controls).padBottom(40);
    controlsTable.row();

    controlsTable.add(easyMode).pad(80, 100, 0, 0);
    controlsTable.add(easyModeButton);
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
          easyModeButton, dashControlButton, attackButton, dashButton, backButton, resetButton
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
    easyModeButton.addListener(
        new ClickListener() {
          public void clicked(InputEvent event, float x, float y) {
            if (buttonNames.get(easyModeButton).equals("Off")) {
              buttonNames.put(easyModeButton, "On");
              easyModeButton.setText("On");
            } else {
              buttonNames.put(easyModeButton, "Off");
              easyModeButton.setText("Off");
            }
          }
        });

    dashControlButton.addListener(
        new ClickListener() {
          public void clicked(InputEvent event, float x, float y) {
            if (buttonNames.get(dashControlButton).equals("Movement")) {
              buttonNames.put(dashControlButton, "Indicator");
              dashControlButton.setText("Indicator");
            } else {
              buttonNames.put(dashControlButton, "Movement");
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
    /** --------- scrolling to the option */
    // controller buttonstyles
    TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(null, null, null, textFont);
    style.fontColor = Color.WHITE;
    TextButton.TextButtonStyle hoverStyle =
        new TextButton.TextButtonStyle(null, null, null, textFont);
    hoverStyle.fontColor = coral;

    // go down
    if (moveCount == 0) {
      if (xbox.getLeftY() == 1 || xbox.getRightY() == 1) {
        if (hoverIndex >= buttons.length - 1) {
          hoverIndex = 0;
        } else {
          hoverIndex++;
        }
      }

      // go up
      if (xbox.getLeftY() == -1 || xbox.getRightY() == -1) {
        if (hoverIndex == 0) {
          hoverIndex = buttons.length - 1;
        } else {
          hoverIndex--;
        }
      }

      for (int i = 0; i < buttons.length; i++) {
        buttons[i].setStyle(style);
      }

      buttons[hoverIndex].setStyle(hoverStyle);

      /** --------- selecting the option */
      if (xbox.getA()) {
        // 2 options only
        if (hoverIndex == 0) {
          if (buttonNames.get(easyModeButton).equals("Off")) {
            buttonNames.put(easyModeButton, "On");
            easyModeButton.setText("On");
          } else {
            buttonNames.put(easyModeButton, "Off");
            easyModeButton.setText("Off");
          }
        } else if (hoverIndex == 1) {
          if (buttonNames.get(dashControlButton).equals("Movement")) {
            buttonNames.put(dashControlButton, "Indicator");
            dashControlButton.setText("Indicator");
          } else {
            buttonNames.put(dashControlButton, "Movement");
            dashControlButton.setText("Movement");
          }
        } else if (hoverIndex == 4) {
          exit = true;
        } else if (hoverIndex == 5) {
          setDefault();
        }
      }
      // any key
      if (hoverIndex == 2 && xbox.getPressed() != null) {
        buttonNames.put(attackButton, xbox.getPressed());
        attackButton.setText(xbox.getPressed());
      } else if (hoverIndex == 3 && xbox.getPressed() != null) {
        buttonNames.put(dashButton, xbox.getPressed());
        dashButton.setText(xbox.getPressed());
      }
    }

    if (moveCount == cooldown) {
      moveCount = 0;
    } else {
      moveCount++;
    }
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * <p>The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
  }

  // TODO: set controls back to normal but need to update the buttons
  private void setDefault() {
    for (TextButton k : buttonNames.keySet()) {
      k.setText(defaultSettings.get(k));
    }
  }

  public void draw() {
    canvas.clear(Color.DARK_GRAY);
    stage.getBatch().begin();
    stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
    // draw background
    //      stage.getBatch().draw(background.getRegion(), 0, 0, stage.getViewport().getWorldWidth(),
    // stage.getViewport().getWorldHeight());
    if (xbox != null) {
      xboxListener();
    }
    stage.getBatch().end();
    stage.draw();
  }

  public void update() {
    stage.getViewport().update(canvas.getWidth(), canvas.getHeight());
    stage.getViewport().apply();
  }

  public void render(float delta) {
    update();
    draw();
    stage.act(delta);
    if (exit) {
      listener.exitScreen(this, 4);
    }
  }

  @Override
  public void show() {
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

  /** Options inputcontroller to see key up/down presses. */
  public class OptionsInputController extends InputAdapter {}
}
