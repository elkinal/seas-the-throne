package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import java.util.ArrayList;

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
      canvas.drawTextUI(message, messageFont, canvas.getWidth() - 300, 100);
    }

    pauseMenuController.getPauseMenu().draw(render);
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
      if (b.isBoss() && b.getBoss().isAttack()) {
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

  public void setDrawSave(boolean draw) {
    drawSave = draw;
  }

  /** Runs when the viewport is resized */
  public void resize(int width, int height) {
    pauseMenuController.getPauseMenu().resize(width, height);
  }

  /** Clears all the UI elements */
  public void clear() {
    uiModel.clear();
  }
}
