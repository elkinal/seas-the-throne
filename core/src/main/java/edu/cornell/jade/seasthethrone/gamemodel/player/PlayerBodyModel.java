package edu.cornell.jade.seasthethrone.gamemodel.player;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.model.CircleModel;
import edu.cornell.jade.seasthethrone.model.PolygonModel;

/**
 * Model for the player body. This class extends {@link PlayerModel} for a flexible frame. The
 * PlayerBodyModel accounts for the main hitbox of the player.
 */
public class PlayerBodyModel extends CircleModel {

  /** Radius of the player */
  private static float PLAYER_RADIUS = 0.65f;

  /** Number of health points the player has */
  private int health;

  /** The number of frames an iframe lasts */
  private int iframeLimit;

  /** Frame counter for iframes */
  private int iframeCounter;

  /** Frame time of the knockback duration */
  private int knockbackTime;

  /** Location of the body knocking the player back */
  private Vector2 knockingBodyPos;

  /** The force of knockback applied by the knocking body */
  private float knockbackForce;

  /** If the player is flagged for knockback application */
  private boolean justKnocked;

  /** Create new player body at position (x,y) */
  public PlayerBodyModel(float x, float y) {
    super(x, y, PLAYER_RADIUS);
    health = 5;
    iframeCounter = 0;
    iframeLimit = 70;
    knockbackTime = iframeLimit / 10;
    justKnocked = false;
  }


  /** Decreases the hitpoints of the player by 1. Also sets the player to invincible. */
  public void decrementHealth() {
    health -= 1;
    setInvincible();
  }

  /** Number of current health points of the player */
  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {this.health = health;}

  /** Returns if the player is currently invincible */
  public boolean isInvincible() {
    return iframeCounter > 0;
  }

  /** Returns if the player is stunned (during iframes) */
  public boolean isKnockedBack() {
    return knockbackTime > 0;
  }

  /** Returns if the player is flagged for knockback application */
  public boolean isJustKnoocked() {
    return justKnocked;
  }

  public void setJustKnocked(boolean b) {
    justKnocked = b;
  }

  public Vector2 getKnockingBodyPos() {
    return knockingBodyPos;
  }

  public float getKnockbackForce() {
    return knockbackForce;
  }

  public void setKnockedBack(Vector2 b2pos, float force) {
    this.justKnocked = true;
    this.knockingBodyPos = b2pos;
    this.knockbackForce = force;
  }

  /** Sets the player invincible according to the iframe limit */
  public void setInvincible() {
    iframeCounter = iframeLimit;
    knockbackTime = iframeLimit / 10;
  }

  public void setKnockbackTime(int time) {
    knockbackTime = time;
  }

  @Override
  public void update(float delta) {
    if (isInvincible()) {
      iframeCounter -= 1;
    }
    knockbackTime = Math.max(0, knockbackTime - 1);
  }
}
