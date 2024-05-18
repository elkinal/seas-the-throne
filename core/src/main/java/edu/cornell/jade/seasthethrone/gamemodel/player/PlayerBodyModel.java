package edu.cornell.jade.seasthethrone.gamemodel.player;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.model.CircleModel;

/**
 * Model for the player body. This class extends {@link PlayerModel} for a flexible frame. The
 * PlayerBodyModel accounts for the main hitbox of the player.
 */
public class PlayerBodyModel extends CircleModel {

  /** Radius of the player */
  private static float PLAYER_RADIUS = 0.65f;

  /** Number of iFrames of the player on hit */
  private static int HIT_IFRAMES = 60;
  /** Number of frames for heal */
  private static int HEAL_FRAMES = 60;

  /** Number of health points the player has */
  private int health;

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

  /** The counter for the time the player is hit */
  private float hitCounter;

  /** The counter for the time the player is healed */
  private float healCounter;
  /** Flag to be passed to the main model to stop dashing */
  private boolean stopDashing;
  private boolean execute;

  /** Create new player body at position (x,y) */
  public PlayerBodyModel(float x, float y) {
    super(x, y, PLAYER_RADIUS);
    health = 5;
    iframeCounter = 0;
    knockbackTime = 0;
    justKnocked = false;
    hitCounter = 0;
    healCounter = 0;
  }


  /** Decreases the hitpoints of the player by 1. */
  public void decrementHealth() {
    health -= 1;
  }

  /** Number of current health points of the player */
  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {this.health = health;}

  public int getHitIFrames(){ return HIT_IFRAMES; }

  /** Returns if the player is currently invincible */
  public boolean isInvincible() {
    return iframeCounter > 0;
  }

  /** Returns if the player is in iFrames & was hit */
  public boolean isHit() { return hitCounter > 0; }
  /** Returns whether the player is healing */
  public boolean isHeal() { return healCounter > 0;}

  /** Returns if the player is stunned (during iframes) */
  public boolean isKnockedBack() {
    return knockbackTime > 0;
  }

  /** Returns if the player is flagged for knockback application */
  public boolean isJustKnocked() {
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

  /** Set the player knocked back
   *
   * @param b2pos   The vector of the knockback
   * @param force   The force of knockback
   * @param time    The amount of time the knockback is applied
   */
  public void setKnockedBack(Vector2 b2pos, float force, int time) {
//    if (knockbackTime > 0) return;
    this.justKnocked = true;
    this.knockingBodyPos = b2pos;
    this.knockbackForce = force;
    this.knockbackTime = time;
  }

  /** Sets the player invincible for the set period of time */
  public void setInvincible(int time) {
    iframeCounter = Math.max(iframeCounter, time);
  }

  /** Sets the player in hit status for the set period of time */
  public void setHit(int time) {
    hitCounter = time;
  }
  /** Sets the player in heal status for short period of time */
  public void setHeal(){
    healCounter = HEAL_FRAMES;
  }

  public boolean shouldStopDashing() { return stopDashing; }
  public void setStopDashing(boolean value) { stopDashing = value; }
  public void startExecuting(){execute = true;}
  public void stopExecuting(){execute = false;}
  public boolean isExecute(){return execute;}


  @Override
  public void update(float delta) {
    if (isInvincible()) {
      iframeCounter -= 1;
    }
    if (isHit()) {
      hitCounter -= 1;
    }
    if (isHeal()) {
      healCounter -= 1;
    }
    knockbackTime = Math.max(0, knockbackTime - 1);
  }
}
