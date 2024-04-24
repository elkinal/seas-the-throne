package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;

public class CheckpointModel extends BoxModel implements Interactable {

  /** The ID for this checkpoint */
  private int checkpointID;

  /** If this checkpoint has been activated */
  private boolean activated;

  private TextureRegion texture;

  private final float WORLD_SCALE;

  public CheckpointModel(LevelObject obs, float scale) {
    super(obs.x, obs.y, obs.width, obs.height);
    this.checkpointID = obs.checkpointID;
    this.WORLD_SCALE = scale;
    CollisionMask.setCategoryMaskBits(this);
  }

  public int getCheckpointID() {
    return checkpointID;
  }

  public void setActivated(boolean a) {
    activated = a;
  }
}
