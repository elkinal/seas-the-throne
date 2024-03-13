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

  public PlayerBodyModel(float[] vertices, float x, float y) {
    super(vertices, x, y);
    isHit = false;
    health = 3;
  }

  /** Create new player body at position (x,y) */
  public PlayerBodyModel(float x, float y){
    // Make a triangle for now
    this(new float[]{-0.5f, -1, 0.5f, -1, 0, 1}, x, y);
  }

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
}
