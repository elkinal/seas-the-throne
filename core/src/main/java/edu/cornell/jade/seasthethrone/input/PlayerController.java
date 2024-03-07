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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.jade.seasthethrone.GameplayController;
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

  /** The vector direction of the player for dashing */
  Vector2 dashDirection;

  /** The boundary of the world */
  private Rectangle bounds;

  /** Dimensions of the game canvas */
  private Vector2 screenDims;

  /**
   * Stores the canvas dimensions in pixels as a Vector2
   *
   * @param dims the vector of screen dimensions
   * */
  public void setScreenDims(Vector2 dims) { screenDims = dims; }

  /**
   * Constructs PlayerController
   */
  public PlayerController(Rectangle bounds, PlayerModel player) {
    this.player = player;
    this.bounds = bounds;
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
      moveSpeed *= 4;
      Vector2 dashDirection = normalize(player.getDashDirection());
      player.setVX(moveSpeed * dashDirection.x);
      player.setVY(moveSpeed * dashDirection.y);
    } else {
      player.setVX(x * moveSpeed / mag);
      player.setVY(y * moveSpeed / mag);
    }
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

  /**
   * Converts world coordinates to centered coords with the dimensions of the game canvas
   * The origin is correct, this involves scaling.
   * */
  public Vector2 worldToCenteredCoords(Vector2 pos) {
    float scaleX = screenDims.x / bounds.width;
    float scaleY = screenDims.y / bounds.height;

    return new Vector2(pos.x * scaleX, pos.y * scaleY);
  }

  /**
   * Converts screen coordinates to centered coords with the dimensions of the game canvas.
   * The scale is correct, this involves reflecting about y and translating the origin.
   */
  public Vector2 screenToCenteredCoords(Vector2 pos) {
    Vector2 centeredCoords = pos.sub(screenDims.x/2, screenDims.y/2);
    centeredCoords.y = -centeredCoords.y;
    return centeredCoords;
  }

  /**
   * Transforms the player and mouse positions to the same, centered coordinate system
   * and sets this player's dash direction to the vector difference of those positions.
   *
   * @param mousePos the position of the mouse in screen coordinates
   *  */
  @Override
  public void updateDirection(Vector2 mousePos) {
    if (player == null) {return;}

    Vector2 playerPos = player.getPosition();

    Vector2 centeredPlayerPos = worldToCenteredCoords(playerPos);
    Vector2 centeredMousePos = screenToCenteredCoords(mousePos);

    dashDirection = centeredMousePos.sub(centeredPlayerPos);
  }

  public void update() {
    if (dashingPressed) {
      beginDashing();
      player.setDashDirection(dashDirection);
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

  /** Returns the norm of a Vector2 */
  public Vector2 normalize(Vector2 v) {
    float magnitude = (float)Math.sqrt(Math.pow(v.x,2) + Math.pow(v.y,2));
    return new Vector2(v.x/magnitude, v.y/magnitude);
  }

}
