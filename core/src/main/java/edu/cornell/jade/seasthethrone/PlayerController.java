/*
 * PlayerController.java
 *
 * This class processes the primary gameplay. It will update the states
 * of the player depending on which controls are activated.
 *
 * Contains code written by Walker M. White
 *
 */

package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.ui.AmmoBar;
import edu.cornell.jade.seasthethrone.ui.HealthBar;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.input.Controllable;
import com.badlogic.gdx.math.MathUtils;

import java.util.jar.JarInputStream;

public class PlayerController implements Controllable {

  /**
   * Error value for how close the mouse is to the player for dash to not count
   */
  private static final float NO_DASH_ERROR = 0.4f;

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

  /** If interact pressed in since last update */
  boolean interactPressed;

  /** If the player needs to aim the dash indicator to dash */
  boolean isAimToDashMode;

  /** How long until the player can toggle dash controls again */
  int dashToggleCounter;

  /**
   * The vector direction of the player for dashing NOTE: this vector will always
   * be normalized, and
   * nonzero
   */
  Vector2 dashDirection;

  /** The vector direction the player is moving */
  Vector2 moveDirection;

  /** Constructs PlayerController */
  public PlayerController(PhysicsEngine physicsEngine, PlayerModel player) {
    this.physicsEngine = physicsEngine;
    this.player = player;
    // start dash indicator down
    dashDirection = new Vector2(0, -1);
    moveDirection = new Vector2();
    this.isAimToDashMode = true;
    this.dashToggleCounter = 0;
  }

  /**
   * Returns true if the currently active player is terminated.
   *
   * @return true if the currently active player is terminated.
   */
  public boolean isDead() {
    return player.isDead();
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

  public void pressInteract() {
    interactPressed = true;
  }

  public void toggleDashMode() {
    if (dashToggleCounter == 0) {
      isAimToDashMode = !isAimToDashMode;
      dashToggleCounter = 50;
    }
  }

  public boolean isInteractPressed() {
    return interactPressed;
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

    // Player should not move while knocked back (first part of iframes)
    if (player.isKnockedBack()) {
      return;
    } else if (player.isDashing()) {
      moveSpeed *= 4;
      if (isAimToDashMode) {
        moveDirection.set(moveSpeed * dashDirection.x, moveSpeed * dashDirection.y);
      } else {
        // If not moving in a direction, just dash in currently facing direction
        if (xNorm == 0 && yNorm == 0) {
          moveDirection.set(-MathUtils.sin(player.getAngle()) * moveSpeed,
              MathUtils.cos(player.getAngle()) * moveSpeed);
        } else {
          moveDirection.set(xNorm * moveSpeed * mag, yNorm * moveSpeed * mag);
        }
      }
    } else {
      moveDirection.set(xNorm * moveSpeed * mag, yNorm * moveSpeed * mag);
    }
    player.setLinearVelocity(moveDirection);
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
  public void shoot() {
    Vector2 playerPos = player.getPosition();
    // TODO: stop hardcoding the offset
    Vector2 startPos = playerPos.add(dashDirection.x * 1.5f, dashDirection.y * 1.5f);
    physicsEngine.spawnBullet(startPos, dashDirection, 16, true);

    player.decrementFishCount();
  }

  /** Begin dashing */
  public void beginDashing() {
    player.startDashing();
    player.setDashDirection(dashDirection);
  }

  /** Begin shooting */
  public void beginShooting() {
    player.startShooting();
  }

  /** Set the player to spearing or shooting, depending on which is applicable. */
  public void spearOrShoot() {
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

    Vector2 diff = mousePos.sub(player.getPosition());

    if (diff.len2() > NO_DASH_ERROR) {
      dashDirection.set(diff.nor());
    }
  }

  @Override
  public Vector2 getLocation() {
    return player.getPosition();
  }

  /**
   * Set the player to always animate.
   *
   * @param b true if should always animate
   */
  public void setAlwaysAnimate(boolean b) {
    player.setAlwaysAnimate(b);
  }

  public void setPlayerLocation(Vector2 loc) {
    player.setPosition(loc);
  }

  public int getHealth() {
    return player.getHealth();
  }

  public int getAmmo() {
    return player.getSpearModel().getNumSpeared();
  }

  public void transferState(StateController state) {
    player.getBodyModel().setHealth(state.getPlayerHealth());
    player.getSpearModel().setNumSpeared(state.getPlayerAmmo());
  }

  public void update() {
    if (dashingPressed && player.canDash()) {
      // TODO: what happens if you get hit while dashing? (during iframes)
      beginDashing();
    } else if (shootingPressed && player.canShoot()) {
      beginShooting();
      shoot();
    }

    setVelPercentages(hoff, voff);
    player.setDirection(moveDirection);
    orientPlayer();

    player.updateDashIndicator(dashDirection);
    if (isAimToDashMode) {
      player.updateSpear(dashDirection);
    } else {
      player.updateSpear(moveDirection.nor());
    }

    dashToggleCounter = Math.max(dashToggleCounter - 1, 0);
    dashingPressed = false;
    shootingPressed = false;
  }
}
