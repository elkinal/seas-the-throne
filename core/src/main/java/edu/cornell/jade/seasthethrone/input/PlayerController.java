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

import edu.cornell.jade.seasthethrone.gamemodel.PlayerModel;

public class PlayerController implements Controllable {

  /** The player */
  private PlayerModel player;
  /** Horizontal offset, -1 to 1 */
  float hoff;
  /** Vertical offset, -1 to 1 */
  float voff;
  /** If dashing pressed in since last update */
  boolean dashingPressed;

  /**
   * Constructs PlayerController
   */
  public PlayerController(PlayerModel player) {
    this.player = player;
  }

  /**
   * Returns true if the currently active player is alive.
   *
   * @return true if the currently active player is alive.
   */
  public boolean isAlive() {
    return player.isActive();
  }

  public void moveHorizontal(float movement) {
    hoff = movement;
  }

  public void moveVertical(float movement) {
    voff = movement;
  }

  public void pressPrimary() {
    dashingPressed = true;
  }

  /**
   * Move in given direction based on offset
   *
   * @param x a value from -1 to 1 representing the percentage of movement speed
   *          to be at in the given direction
   * @param y a value from -1 to 1 representing the percentage of movement speed
   *          to be at in the given direction
   */
  public void setVelPercentages(float x, float y) {
    float mag = (x * x + y * y) / (float) Math.sqrt(2);
    // TODO: Change this to compare with some epsilong probably
    // this does techinically work though
    if (mag == 0f) {
      mag = 1;
    }
    float moveSpeed = player.getMoveSpeed();
    if (player.isDashing()){
      moveSpeed *= 3;
    }

    // TODO: GET RID OF GETPOINTMODEL
    player.getPointModel().setVX(x * moveSpeed / mag);
    player.getPointModel().setVY(y * moveSpeed / mag);
  }

  /** Orients the player model based on their primary direction of movement */
  public void orientPlayer() {
    int dir = player.direction();
    // Up, down, left, right, NE, SE, SW, NW
    switch (dir) {
      case 0:
        player.setAngle(0f);
        break;
      case 1:
        player.setAngle((float)Math.PI);
        break;
      case 2:
        player.setAngle((float)Math.PI/2);
        break;
      case 3:
        player.setAngle(-(float)Math.PI/2);
        break;
      case 4:
        player.setAngle(-(float)Math.PI/4);
        break;
      case 5:
        player.setAngle(-3f*(float)Math.PI/4);
        break;
      case 6:
        player.setAngle(3f*(float)Math.PI/4);
        break;
      case 7:
        player.setAngle((float)Math.PI/4);
        break;
      default:
        break;
    }
  }

  /**
   * Begin dashing if possible
   */
  public void beginDashing() {
    if (player.canDash()) {
      player.setDashing(true);
      player.setDashCounter(player.getDashLength());
    }
  }

  public void update() {
    if (dashingPressed) {
      beginDashing();
    }
    setVelPercentages(hoff, voff);
    orientPlayer();

    // Handle dashing
    if (player.isDashing()) {
      player.decrementDashCounter();
      if (player.getDashCounter() <= 0) {
        // exit dash
        player.setDashing(false);
        player.setDashCounter(player.getDashCooldownLimit());
      }
    } else {
      player.setDashCounter(Math.max(0, player.getDashCounter() - 1));
    }

    dashingPressed = false;
  }

}
