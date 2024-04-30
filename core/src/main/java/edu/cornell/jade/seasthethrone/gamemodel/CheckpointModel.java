package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class CheckpointModel extends BoxModel implements Interactable, Renderable {

  /** The ID for this checkpoint */
  private int checkpointID;

  /** If this checkpoint has been activated */
  private boolean activated;

  private TextureRegion texture;

  private final float WORLD_SCALE;

  private final float INTERACT_RANGE = 1f;

  public CheckpointModel(LevelObject obs, float scale) {
    super(obs.x, obs.y, obs.width, obs.height);
    this.checkpointID = obs.checkpointID;
    this.WORLD_SCALE = scale;
    this.texture = new TextureRegion(new Texture("levels/mossytablet.png"));
    CollisionMask.setCategoryMaskBits(this);
  }

  public int getCheckpointID() {
    return checkpointID;
  }

  public void setActivated(boolean a) {
    activated = a;
  }

  public void interact() {}

  @Override
  public boolean playerInRange(Vector2 playerPos) {
    return Vector2.dst(getX(), getY(), playerPos.x, playerPos.y) < INTERACT_RANGE;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    renderer.draw(texture, getX(), getY());
  }

  @Override
  public void progressFrame() {

  }

  @Override
  public void alwaysUpdate() {

  }

  @Override
  public void neverUpdate() {

  }

  @Override
  public void setAlwaysAnimate(boolean animate) {

  }

  @Override
  public boolean alwaysAnimate() {
    return false;
  }
}
