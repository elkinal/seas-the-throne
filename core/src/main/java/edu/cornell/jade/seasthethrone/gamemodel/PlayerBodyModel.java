package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.model.PolygonModel;

/**
 * Model for the player body. This class extends {@link PlayerModel} for a flexible frame. The
 * PlayerBodyModel accounts for the main hitbox of the player.
 */
public class PlayerBodyModel extends PolygonModel {

  /** Whether the player body has been hit */
  private boolean isHit;

  /** Number of health points the player has */
  private int health;

  /** The number of frames an iframe lasts */
  private int iframeLimit;

  /** Frame counter for iframes */
  private int iframeCounter;

  PlayerShadowModel shadow;

  public PlayerBodyModel(float[] vertices, float x, float y) {
    super(vertices, x, y);
    isHit = false;
    health = 3;
    iframeCounter = 0;
    iframeLimit = 50;
  }

  /** Create new player body at position (x,y) */
  public PlayerBodyModel(float x, float y){
    // Make a triangle for now
    this(new float[]{-0.5f, -1, 0.5f, -1, 0, 1}, x, y);
  }

  public void setShadow(PlayerShadowModel shadow) { this.shadow = shadow; }
  public PlayerShadowModel getShadow() {return shadow;}

  /** Returns if the player body was hit */
  public boolean isHit() {
    return isHit;
  }

  /** Sets if the player body was hit
   *  If health drops to 0, will remove body from the world
   * */
  public void setHit(boolean hit) {
    isHit = hit;
    health -= 1;
    if(health <= 0){
      markRemoved(true);
    }
  }

  /** Number of current health points of the player */
  public int getHealth() {
    return health;
  }

  /** Returns if the player is currently invincible */
  public boolean isInvincible() {
    return iframeCounter > 0;
  }

  /** Returns if the player is stunned (during iframes) */
  public boolean isStunned(){
    return iframeCounter > iframeLimit/5*4;
  }

  /** Sets the player invincible according to the iframe limit */
  public void setInvincible(){
    iframeCounter  = iframeLimit;
  }

  @Override
  public void update(float delta){
    if (isInvincible()){
      iframeCounter -= 1;
    }
  }
}
