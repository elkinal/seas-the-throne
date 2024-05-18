package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.InteractableController;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

/**
 * This is a controller to manage the UI interface for the game. This controller updates all UI, but
 * only draws UI items that are static on the screen. It does not, for example, draw the AmmoBar.
 *
 * <p>This controller draws and updates all UI items using UIModel.
 */
public class UIController {
  /** A UIModel containing all renderable gameplay UI objects */
  UIModel uiModel;

  /** The UI viewport */
  ScreenViewport viewport;

  /** A reference to the player */
  PlayerController player;

  /** A reference to the pause menu */
  PauseMenuController pauseMenuController;

  /** A reference to the interactable controller */
  InteractableController interactController;

  /** A reference to the current boss that the player is facing */
  BossController boss;

  /** If this controller should draw Game Saved text */
  private boolean drawSave;

  /** The rendering engine used to draw the UI elements */
  RenderingEngine render;

  /** Font for drawing on screen UI messages */
  private BitmapFont messageFont;

  /** Internal UI canvas to handle rendering */
  private GameCanvas canvas;

  private TextureRegion gameOver;

  /** Font for display text */
  private BitmapFont textFont;

  private float fontScale;


  /**
   * Constructs a UIController object.
   *
   * @param player the player
   * @param render the render engine
   * @param canvas the canvas of the render engine
   * @param view the UI viewport
   * @param
   */
  public UIController(
      PlayerController player,
      PauseMenuController pauseMenuController,
      RenderingEngine render,
      GameCanvas canvas,
      ScreenViewport view) {
    this.player = player;

    this.pauseMenuController = pauseMenuController;

    this.render = render;
    viewport = view;
    boss = null;
    drawSave = false;

    this.canvas = canvas;
    uiModel = new UIModel(viewport.getScreenWidth(), viewport.getScreenHeight());
    gameOver = new TextureRegion(new Texture("ui/game_over.png"));

  }

  public UIController (
          PauseMenuController pauseMenuController,
          RenderingEngine render,
          GameCanvas canvas,
          ScreenViewport view
  ) {
    this.pauseMenuController = pauseMenuController;

    this.render = render;
    viewport = view;
    boss = null;
    drawSave = false;

    this.canvas = canvas;
    uiModel = new UIModel(viewport.getScreenWidth(), viewport.getScreenHeight());
    gameOver = new TextureRegion(new Texture("ui/game_over.png"));

    fontScale = (float) canvas.getHeight() / 275;
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

  public void setPlayer(PlayerController player) {
    this.player = player;
  }
  public void setInteractController(InteractableController ic) {
    this.interactController = ic;
  }

  public void gatherAssets(AssetDirectory assets) {
    this.messageFont = assets.getEntry("ui:alagard", BitmapFont.class);
  }

  /**
   * Returns the AmmoBar UI element. This is only necessary for AmmoBar because it is not drawn by
   * this controller.
   */
  public AmmoBar getAmmoBar() {
    return uiModel.getAmmoBar();
  }
  /**
   * Returns the enemies health bars ui element.
   */
  public Array<EnemyHealthBar> getEnemies() {
    return uiModel.getEnemies();
  }

  /** Returns the pauseMenuController */
  public PauseMenuController getPauseMenuController() {
    return pauseMenuController;
  }

  /** Draws UI elements */
  public void drawUI() {
    canvas.beginUI();
    canvas.getUiBatch().setProjectionMatrix(viewport.getCamera().combined);
    if (boss != null) {
      uiModel.draw(render, boss.getBoss().getDeathCount());
    } else {
      uiModel.draw(render, 0);
    }

    if (drawSave) {
      String message = "Game Saved!";
      canvas.drawTextUI(message, messageFont, canvas.getWidth() - 300, 100, true);
    }
    pauseMenuController.getPauseMenu().draw(render);

    DialogueBox npcDialogue = interactController.getDialogueController().getDialogueBox();
    if (npcDialogue != null) npcDialogue.draw(render);

    canvas.endUI();
  }

  /** Updates states of all UI */
  public void update(Array<BossController> bosses) {
    uiModel.clearEnemies();
    // update health bar
    uiModel.update(player.getHealth());
    // update ammo
    uiModel.update(player.getAmmo(), player.getLocation());

    for (BossController b : bosses) {
      if (b.isBoss() && b.getBoss().isInRoom()) {
        boss = b;
      }
      else{
        if(b.getHealth()>0)
          uiModel.update(b);
      }
    }
    // update boss hp
    uiModel.update(boss);
  }

  public void drawGameOver() {
    canvas.beginUI();
    canvas.getUiBatch().setProjectionMatrix(viewport.getCamera().combined);

    // draw image
    float scale = 0.5f * canvas.getHeight() / gameOver.getRegionHeight();
    float width = scale * gameOver.getRegionWidth();
    float height = scale * gameOver.getRegionHeight();
    float ox = (canvas.getWidth()/2f - width/2f);
    float oy = (canvas.getHeight()/2f - height/2f);
    canvas.drawUI(gameOver, Color.WHITE, ox, oy, width, height);

    // draw text
    String text = "Try again? (Interact)";
    textFont.dispose();
    resizeFont();
    GlyphLayout layout = new GlyphLayout(textFont, text);
    ox = canvas.getWidth()/2f - layout.width / 2f;
    oy = 0.25f* canvas.getHeight();
    canvas.drawTextUI(text, textFont, ox,  oy, false);

    canvas.endUI();
  }

  private void resizeFont() {
    fontScale = (float) canvas.getHeight() / 275;

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

  public void setDrawSave(boolean draw) {
    drawSave = draw;
  }

  /** Runs when the viewport is resized */
  public void resize(int width, int height) {
    pauseMenuController.getPauseMenu().resize(width, height);
  }

  /** Clears all the UI elements */
  public void clear() {
    boss = null;
  }
}
