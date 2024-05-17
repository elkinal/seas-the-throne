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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.EnemyModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.input.Controllable;
import com.badlogic.gdx.math.MathUtils;

public class PlayerController implements Controllable {

  /** Error value for how close the mouse is to the player for indicator to not update. */
  private static final float NO_DASH_ERROR = 0.4f;

  /** The maximum distance an enemy can be from the player for aim assist to lock on. */
  private static final float AIM_ASSIST_RANGE = 40f;

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

  /** If assisted shooting pressed in since last update */
  boolean assistedShootingPressed;

  /** If easy mode is on and aim assist should be used on all shots */
  boolean toggleEasyMode;

  /** If interact pressed in since last update */
  boolean interactPressed;

  /** If the player needs to aim the dash indicator to dash */
  boolean isAimToDashMode;

  /** How long until the player can toggle dash controls again */
  int dashToggleCounter;

  /**
   * The vector direction of the player indicator NOTE: this vector will always
   * be normalized, and nonzero
   */
  Vector2 indicatorDirection;

  /** The vector direction the player is moving */
  Vector2 moveDirection;

  /** Bullet model builder */
  BulletModel.Builder bulletBuilder;

  /** Constructs PlayerController */
  public PlayerController(PhysicsEngine physicsEngine, PlayerModel player) {
    this.physicsEngine = physicsEngine;
    this.player = player;
    // start dash indicator down
    indicatorDirection = new Vector2(0, -1);
    moveDirection = new Vector2();

    Preferences prefs = Gdx.app.getPreferences("options");
    String savedDashControl = prefs.getString("dashControl");
    String savedEasyMode = prefs.getString("easyMode");

    if (!savedDashControl.isEmpty()) this.isAimToDashMode = savedDashControl.equals("Indicator");
    else this.isAimToDashMode = true;
    this.dashToggleCounter = 0;

    if (!savedEasyMode.isEmpty()) toggleEasyMode = savedEasyMode.equals("On");
    else this.toggleEasyMode = false;
    bulletBuilder = BulletModel.Builder.newInstance()
            .setBaseTexture(new Texture("bullet/whitefish.png"))
            .setType(BulletModel.Builder.Type.PLAYER);

  }

  public PlayerController(PhysicsEngine physicsEngine) {
    this.physicsEngine = physicsEngine;
    // start dash indicator down
    indicatorDirection = new Vector2(0, -1);
    moveDirection = new Vector2();

    Preferences prefs = Gdx.app.getPreferences("options");
    String savedDashControl = prefs.getString("dashControl");
    String savedEasyMode = prefs.getString("easyMode");

    if (!savedDashControl.isEmpty()) this.isAimToDashMode = savedDashControl.equals("Indicator");
    else this.isAimToDashMode = true;
    this.dashToggleCounter = 0;

    if (!savedEasyMode.isEmpty()) toggleEasyMode = savedEasyMode.equals("On");
    else this.toggleEasyMode = false;
    bulletBuilder = BulletModel.Builder.newInstance()
            .setBaseTexture(new Texture("bullet/whitefish.png"))
            .setType(BulletModel.Builder.Type.PLAYER);
  }

  public void setPlayer(PlayerModel player) {this.player = player;}

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
    if (toggleEasyMode) assistedShootingPressed = true;
    else shootingPressed = true;
  }

  public void pressTertiary() { assistedShootingPressed = true; }

  public void pressInteract() {
    interactPressed = true;
  }
  public boolean isFinishExecute(){ return player.isFinishExecute();}
  public void setFinishExecute(boolean execute){ player.setFinishExecute(execute);}

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
      moveSpeed *= 3;
      if (isAimToDashMode) {
        moveDirection.set(moveSpeed * indicatorDirection.x, moveSpeed * indicatorDirection.y);
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

  /** Begin dashing */
  public void beginDashing() {
    player.startDashing();
  }

  /** Begin shooting */
  public void beginShooting() {
    player.startShooting();

    Vector2 playerPos = player.getPosition();
    // TODO: stop hardcoding the offset
    Vector2 startPos = playerPos.add(indicatorDirection.x * 1.5f, indicatorDirection.y * 1.5f);
    physicsEngine.spawnBullet(startPos, indicatorDirection, 30, bulletBuilder);

    player.decrementFishCount();
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
      indicatorDirection.set(diff.nor());
    }
  }

  /**
   * Sets the {@link #indicatorDirection} field to point in the direction
   * of the nearest enemy, if the indicator is already close enough.
   * If no enemies are near the player, this method will not do anything.
   */
  public void pointToClosestEnemy() {
    EnemyModel closestEnemy = null;
    float closestDist = Float.MAX_VALUE;
    for (EnemyModel b : physicsEngine.getEnemies()) {
      if (!b.isActive()) continue;
      float dist = player.getPosition().dst(b.getPosition());
      if (dist < closestDist) {
        //Check if the angle is "close enough"
        float angle = b.getPosition().sub(player.getPosition()).angleDeg(indicatorDirection);
        if (angle > 45 && angle < 315) continue;
        closestEnemy = b;
        closestDist = dist;
      }
    }
    if (closestDist < AIM_ASSIST_RANGE) {
      indicatorDirection.set(closestEnemy.getPosition().sub(player.getPosition())).nor();
    }
  }

  @Override
  public Vector2 getLocation() {
    return player.getPosition();
  }

  public Vector2 getShadowLocation() { return player.getShadowModel().getPosition(); }

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

  public void setHealth(int hp) {player.setHealth(hp);}

  public int getAmmo() {
    return player.getSpearModel().getNumSpeared();
  }
  public boolean isExecuting(){return player.isExecuting();}

  public void transferState(StateController state) {
    player.getBodyModel().setHealth(state.getPlayerHealth());
    player.getSpearModel().setNumSpeared(state.getPlayerAmmo());
  }

  public void update() {
    if (isAimToDashMode) {
      player.updateSpear(indicatorDirection);
    } else {
      player.updateSpear(moveDirection.nor());
    }

    if (assistedShootingPressed) { pointToClosestEnemy(); }
    player.updateDashIndicator(indicatorDirection);

    if (dashingPressed && player.canDash()) {
      beginDashing();
    } else if ((shootingPressed || assistedShootingPressed) && player.canShoot()) {
      beginShooting();
    }

    setVelPercentages(hoff, voff);
    if (isExecuting()){
      setVelPercentages(0,0);
    }
    player.setDirection(moveDirection);
    orientPlayer();

    dashToggleCounter = Math.max(dashToggleCounter - 1, 0);
    dashingPressed = false;
    shootingPressed = false;
    assistedShootingPressed = false;
  }
}
