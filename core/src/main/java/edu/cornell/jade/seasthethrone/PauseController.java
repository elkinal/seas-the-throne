package edu.cornell.jade.seasthethrone;

import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import java.util.ArrayList;

public class PauseController {
  /** The rendering engine */
  RenderingEngine renderEngine;

  /** The physics engine */
  PhysicsEngine physicsEngine;

  /** The player controller */
  PlayerController playerController;

  /** Whether the game is paused */
  boolean isPaused;

  /**
   * Constructor for Pause Controller. Initializes render and physics engines and sets isPaused to
   * false.
   *
   * @param physics the physics engine
   * @param render the render engine
   */
  public PauseController(RenderingEngine render, PhysicsEngine physics, PlayerController player) {
    renderEngine = render;
    physicsEngine = physics;
    this.playerController = player;
    isPaused = false;
  }

  public PauseController(RenderingEngine render) {
    renderEngine = render;
    isPaused = false;
  }

  public void setPhysicsEngine(PhysicsEngine physicsEngine) {
    this.physicsEngine = physicsEngine;
  }

  public void setPlayerController(PlayerController player) {
    this.playerController = player;
  }

  /**
   * Pauses the game. Disables animation for all renderables that do not have the always render
   * flag.
   */
  public void pauseGame() {
    playerController.setAlwaysAnimate(true);
    renderEngine.disableAnimation();
    isPaused = true;
  }

  /** Unpauses the game. Enables animation for all renderables. */
  public void continueGame() {
    renderEngine.enableAnimation();
    isPaused = false;
  }

  /**
   * Returns whether the game is paused.
   *
   * @return true if the game is paused
   */
  public boolean getPaused() {
    return isPaused;
  }

  /**
   * Sets all Renderables in 'play' to be always animated.
   *
   * @param r the Renderable that should play even if the game is paused
   */
  public void continueAnimating(Renderable r) {
    r.setAlwaysAnimate(true);
  }

  /**
   * Resets all possible renderables so they all do not continue being animated after pause.
   *
   * @param allRenderables the list of all renderables in the game
   */
  public void resetAnimating(ArrayList<Renderable> allRenderables) {
    for (Renderable r : allRenderables) {
      r.setAlwaysAnimate(false);
    }
  }
}
