package edu.cornell.jade.seasthethrone;

import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class PauseController {
  /** The player model */
  PlayerModel player;

  /** The rendering engine */
  RenderingEngine renderEngine;

  /** The physics engine */
  PhysicsEngine physicsEngine;

  boolean isPaused;

  public PauseController(RenderingEngine render, PhysicsEngine physics) {
    this.player = player;
    renderEngine = render;
    physicsEngine = physics;
    isPaused = false;
  }

  public void pauseGame() {
    renderEngine.disableAnimation();
    isPaused = true;
  }

  public void continueGame() {
    renderEngine.enableAnimation();
    isPaused = false;
  }

  public boolean getPaused() {
    return isPaused;
  }
}
