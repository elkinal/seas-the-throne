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
import edu.cornell.jade.seasthethrone.PhysicsEngine;
import edu.cornell.jade.seasthethrone.gamemodel.PlayerModel;
import edu.cornell.jade.seasthethrone.util.Direction;
import com.badlogic.gdx.math.MathUtils;

public class PlayerController implements Controllable {

  /** Error value for how close the mouse is to the player for dash to not count */
  private static final float NO_DASH_ERROR = 0.5f;

  /** The player */
  private PlayerModel player;

  /** Need access to the world to add bullets */
  private PhysicsEngine physicsEngine;

  /** Horizontal offset, -1 to 1 */
  float hoff;

  /** Vertical offset, -1 to 1 */
  float voff;

  /** If dashing pressed in since last update */
  boolean dashingPressed;

  /** If shooting pressed in since last update */
  boolean shootingPressed;

  /** The vector direction of the player for dashing */
  Vector2 dashDirection;

  /** Constructs PlayerController */
  public PlayerController(PhysicsEngine physicsEngine, PlayerModel player) {
    this.physicsEngine = physicsEngine;
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

  public void pressSecondary() {
    shootingPressed = true;
  }

  /**
   * Move in given direction based on offset
   *
   * @param x a value from -1 to 1 representing the percentage of movement speed
   *          to be at in the
   *          given direction
   * @param y a value from -1 to 1 representing the percentage of movement speed
   *          to be at in the
   *          given direction
   */
  public void setVelPercentages(float x, float y) {
    float mag = (float) Math.sqrt(x * x + y * y);
    float xNorm, yNorm;
    if (mag < MathUtils.FLOAT_ROUNDING_ERROR) {
      xNorm = 0;
      yNorm = 0;
    } else {
      xNorm = x / mag;
      yNorm = y / mag;
    }
    mag = Math.min(mag, 1f);
    float moveSpeed = player.getMoveSpeed();

    // Player should not move while stunned (first part of iframes)
    if (player.isStunned()) {}
    else if (player.isDashing()) {
      moveSpeed *= 4;
      Vector2 dashDirection = normalize(player.getDashDirection());
      player.setVX(moveSpeed * dashDirection.x);
      player.setVY(moveSpeed * dashDirection.y);
    } else {
      player.setVX(xNorm * moveSpeed * mag);
      player.setVY(yNorm * moveSpeed * mag);
    }
  }

  /** Orients the player model based on their primary direction of movement */
  public void orientPlayer() {
    Direction dir = player.direction();
    // Up, down, left, right, NE, SE, SW, NW
    switch (dir) {
      case UP:
        player.setAngle(0f);
        break;
      case DOWN:
        player.setAngle((float) Math.PI);
        break;
      case LEFT:
        player.setAngle((float) Math.PI / 2);
        break;
      case RIGHT:
        player.setAngle(-(float) Math.PI / 2);
        break;
    }
  }

  /** Shoot a single bullet */
  public void shoot(){
    Vector2 playerPos = player.getPosition();
    //TODO: stop hardcoding the offset
    Vector2 startPos = playerPos.add(dashDirection.nor().scl(1.5f));
    physicsEngine.spawnBullet(startPos, dashDirection.nor(), 16, true);

    player.setShootCounter();
    player.decrementFishCount();
  }

  /** Begin dashing */
  public void beginDashing() {
    player.startDashing();
    player.setDashDirection(dashDirection);
  }

  /** Begin shooting */
  public void beginShooting(){
    player.startShooting();
  }

  /**
   * Set the player to spearing or shooting,
   * depending on which is applicable.
   */
  public void spearOrShoot(){
  }

  /**
   * Transforms the player and mouse positions to the same, centered coordinate
   * system and sets this
   * player's dash direction to the vector difference of those positions.
   *
   * @param mousePos the position of the mouse in screen coordinates
   */
  @Override
  public void updateDirection(Vector2 mousePos) {
    // TODO: actually figure out when player is set to null instead of simply
    // handling the case
    if (player == null) {
      return;
    }

    Vector2 playerPos = player.getPosition();

    dashDirection = mousePos.sub(playerPos);
  }

  @Override
  public Vector2 getLocation() {
    return player.getPosition();
  }

  public void update() {
    if (dashingPressed && player.canDash() && (dashDirection.len() > NO_DASH_ERROR)){
      //TODO: what happens if you get hit while dashing? (during iframes)
      beginDashing();
    } else if(shootingPressed && player.canShoot()){
      beginShooting();
    }
    setVelPercentages(hoff, voff);
    orientPlayer();

    player.updateSpear(dashDirection);

    if(player.canShootBullet()) {
      shoot();
    }

    dashingPressed = false;
    shootingPressed = false;
  }

  /** Returns the norm of a Vector2 */
  public Vector2 normalize(Vector2 v) {
    float magnitude = (float) Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
    return new Vector2(v.x / magnitude, v.y / magnitude);
  }
}
