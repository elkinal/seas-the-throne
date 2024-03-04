/*
 * PlayerController.java
 *
 * This class processes the primary gameplay. It will update the states
 * of the player depending on which controls are activated.
 *
 * Contains code written by Walker M. White
 *
 */

package edu.cornell.jade.seasthethrone.input;

// TODO: make this not have to import physics engine by moving logic directly in here
import edu.cornell.jade.seasthethrone.PhysicsEngine;

public class PlayerController implements Controllable {
  /** The player controlled by the controller */
  PhysicsEngine physicsEngine;

  /** Horizontal offset, -1 to 1 */
  float hoff;
  /** Vertical offset, -1 to 1 */
  float voff;
  /** If dashing pressed in since last update */
  boolean dashing;

  /**
   * Constructs PlayerController
   */
  public PlayerController(PhysicsEngine physicsEngine) {
    this.physicsEngine = physicsEngine;
  }

  public void moveHorizontal(float movement) {
    hoff = movement;
  }

  public void moveVertical(float movement) {
    voff = movement;
  }

  public void pressPrimary() {
    dashing = true;
  }

  public void update() {
    if (dashing) {
      physicsEngine.beginDashing();
    }
    physicsEngine.setVelPercentages(hoff, voff);
    dashing = false;
  }

}
