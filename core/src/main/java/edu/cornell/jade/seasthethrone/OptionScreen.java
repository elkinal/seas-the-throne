package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
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
import edu.cornell.jade.seasthethrone.util.ScreenListener;
import edu.cornell.jade.seasthethrone.util.XBoxController;

import java.util.HashMap;

public class OptionScreen implements Screen {
  /** Internal assets for this title screen */
  private AssetDirectory internal;

  /** Background texture for start-up */
  private Texture background;

  /** The game canvas */
  private GameCanvas canvas;

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

  /** Dictionary that maps buttons to indices in an array */
  private HashMap<TextButton, Integer> buttonMap = new HashMap<>();

  /** The screen listener to know when to exit screen */
  private ScreenListener listener;

  private Table controlsTable;

  /**
   * indices:
   *
   * <p>0 = easy mode toggle 1 = movement indication 2 = controller keymap for attack 3 = controller
   * keymap for dash
   */
  private final String[] defaultSettings = {"Off", "Movement", "LT", "RT"};

  /** The current controls set by the player */
  private String[] currentControls;

  /** Indicates screen should close (exit options page) */
  private boolean exit;

  public OptionScreen(String file, GameCanvas canvas) {
    internal = new AssetDirectory(file);
    internal.loadAssets();
    internal.finishLoading();

    this.canvas = canvas;
    //    controlsButtons = new HashMap<>();
    background = internal.getEntry("title:background", Texture.class);
    textFont = internal.getEntry("loading:alagard", BitmapFont.class);
    headingStyle = new Label.LabelStyle(textFont, Color.CYAN);
    buttonStyle = new Label.LabelStyle(textFont, Color.WHITE);
    // make stage for options screen
    stage = new Stage();
    Gdx.input.setInputProcessor(stage);

    // make table for all the controls options
    controlsTable = new Table();
    controlsTable.setFillParent(true);
    controlsTable.setDebug(true);
    //    controlsTable.align(Align.topLeft);
    stage.addActor(controlsTable);
    currentControls = defaultSettings;
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
    buttonStyle.overFontColor = Color.CYAN;

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

    // make buttons have text
    TextButton buttons[] =
        new TextButton[] {easyModeButton, dashControlButton, attackButton, dashButton};

    for (int i = 0; i < buttons.length; i++) {
      buttons[i].setText(defaultSettings[i]);
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

    addListeners();

    // add buttons to map to make them remappable
    //    controlsButtons.put(easyModeButton, "Off");
    //    controlsButtons.put(dashControlButton, "Movement");
    //    controlsButtons.put(attackButton, "LT");
    //    controlsButtons.put(dashButton, "RT");
  }

  /** Add listeners to buttons */
  private void addListeners() {
    // button listener to listen for key remapping
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
            if (currentControls[0].equals("Off")) {
              currentControls[0] = "On";
              easyModeButton.setText("On");
            } else {
              currentControls[0] = "Off";
              easyModeButton.setText("Off");
            }
          }
        });

    dashControlButton.addListener(
        new ClickListener() {
          public void clicked(InputEvent event, float x, float y) {
            if (currentControls[1].equals("Movement")) {
              currentControls[1] = "Indicator";
              dashControlButton.setText("Indicator");
            } else {
              currentControls[1] = "Movement";
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
            System.out.println("clicked exit");
            exit = true;
          }
        });
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
    for (int i = 0; i < currentControls.length; i++) {
      currentControls[i] = defaultSettings[i];
    }
  }

  public void draw() {
    canvas.clear(Color.BLACK);
    stage.getBatch().begin();
    stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
    // draw background
    //      stage.getBatch().draw(background.getRegion(), 0, 0, stage.getViewport().getWorldWidth(),
    // stage.getViewport().getWorldHeight());
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
    //    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    if (exit) {
      listener.exitScreen(this, 4);
    }
  }

  private void updateSettings() {}

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
