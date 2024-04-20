package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class UIController {
  /** A UIModel containing all renderable gameplay UI objects */
  UIModel uiModel;

  /** The UI viewport */
  ScreenViewport viewport;

  /** A reference to the player */
  PlayerController player;

  /** A reference to the current boss that the player is facing */
  BossController boss;

  /** The rendering engine used to draw the UI elements */
  RenderingEngine render;

  /** Internal UI canvas to handle rendering */
  private GameCanvas canvas;

  /** Constructs a UIController object. */
  public UIController(
      PlayerController player, RenderingEngine render, GameCanvas canvas, ScreenViewport view) {
    this.player = player;
    this.render = render;
    viewport = view;
    boss = null;

    this.canvas = canvas;
    uiModel = new UIModel();
  }

  /** Adds boss once player encounters it */
  public void addBoss(BossController b) {
    boss = b;
  }

  /** Removes boss once player defeats it */
  public void removeBoss() {
    boss = null;
  }

  /** Adds UI element to the model */
  public void add(Renderable r) {
    uiModel.add(r);
  }

  /** Draws UI elements */
  public void drawUI() {
    canvas.begin();
    canvas.beginUI();
    canvas.getUiBatch().setProjectionMatrix(viewport.getCamera().combined);
    uiModel.draw(render);
    canvas.endUI();
    canvas.end();
  }

  /** Updates states of all UI */
  public void update() {
    // update health bar
    uiModel.update(player.getHealth());

    // update bullet count
    uiModel.update(player.getAmmo(), player.getLocation());
  }

  /** Clears all the UI elements */
  public void clear() {
    uiModel.clear();
  }
}
