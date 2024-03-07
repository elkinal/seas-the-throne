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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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

  /** The vector direction of the player for dashing */
  Vector2 dashDirection;

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

  /**
   * Transforms the player and mouse positions to the same, centered coordinate system
   * and sets this player's dash direction to the vector difference of those positions.
   *
   * @param mousePos the position of the mouse in screen coordinates
   *  */
  @Override
  public void updateDirection(Vector2 mousePos) {
    if (physicsEngine.getPlayerModel() == null) {return;}

    Vector2 playerPos = physicsEngine.getPlayerModel().getPosition();

    Vector2 centeredPlayerPos = physicsEngine.worldToCenteredCoords(playerPos);
    Vector2 centeredMousePos = physicsEngine.screenToCenteredCoords(mousePos);

    dashDirection = centeredMousePos.sub(centeredPlayerPos);
  }

  public void update() {
    if (dashing) {
      physicsEngine.getPlayerModel().setDashDirection(dashDirection);
      physicsEngine.beginDashing();
    }
    physicsEngine.setVelPercentages(hoff, voff);
    physicsEngine.orientPlayer();
    dashing = false;
  }

}
