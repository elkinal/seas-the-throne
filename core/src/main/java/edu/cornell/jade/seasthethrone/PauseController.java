package edu.cornell.jade.seasthethrone;

import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class PauseController {
  /** The player model */
  PlayerController player;

  /** The rendering engine */
  RenderingEngine renderEngine;

  /** The physics engine */
  PhysicsEngine physicsEngine;

  public PauseController(PlayerController player, RenderingEngine render, PhysicsEngine physics) {
    this.player = player;
    renderEngine = render;
    physicsEngine = physics;
  }

  public void pauseGame() {
    renderEngine.disableAnimation();
  }

  public void continueGame() {
    renderEngine.enableAnimation();
  }
}
