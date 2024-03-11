package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.model.PolygonModel;

/**
 * Model for the player body. This class extends {@link PlayerModel} for a flexible frame. The
 * PlayerBodyModel accounts for the main hitbox of the player.
 */
public class PlayerBodyModel extends PolygonModel {

  public PlayerBodyModel(float[] vertices, float x, float y) {
    super(vertices, x, y);
  }

  /** Create new player body at position (x,y) */
  public PlayerBodyModel(float x, float y){
    // Make a triangle for now
    this(new float[]{-0.5f, -1, 0.5f, -1, 0, 1}, x, y);
  }

}
