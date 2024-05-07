package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class CheckpointModel extends BoxModel implements Interactable, Renderable {

  /** The ID for this checkpoint */
  private int checkpointID;

  /** If this checkpoint has been activated */
  private boolean activated;

  private boolean playerInRange;

  private TextureRegion texture;

  private FilmStrip arrow;

  private final float WORLD_SCALE;

  private int frameDelay;

  private int frameCounter;


  /** Range within which the player can interact with this checkpoint */
  private final float INTERACT_RANGE = 5f;

  public CheckpointModel(LevelObject obs, float scale) {
    super(obs.x, obs.y, obs.width, obs.height);
    this.checkpointID = obs.checkpointID;
    this.WORLD_SCALE = scale;
    this.playerInRange = false;
    this.frameCounter = 0;
    this.frameDelay = 4;

    this.texture = new TextureRegion(new Texture("levels/mossytablet.png"));
    this.arrow = new FilmStrip(new Texture("levels/interactable_arrow.png"), 1, 20);
    CollisionMask.setCategoryMaskBits(this);
  }

  public int getCheckpointID() {
    return checkpointID;
  }

  public void setActivated(boolean a) {
    activated = a;
  }

  public void interact() {}

  /** Checks if the player is close enough to interact with this checkpoint */
  @Override
  public boolean isPlayerInRange(Vector2 playerPos) {
    return Math.abs(Vector2.dst(getX(), getY(), playerPos.x, playerPos.y)) < INTERACT_RANGE;
  }

  public void setPlayerInRange(boolean inRange) {
    playerInRange = inRange;
  }

  @Override
  public boolean getPlayerInRange() {
    return playerInRange;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    float y_offset = WORLD_SCALE*texture.getRegionHeight()/2f - getHeight()/2f;
    renderer.draw(texture, getX(), getY() + y_offset);

    if (playerInRange) {
      frameCounter += 1;
      if (frameCounter % frameDelay == 0) {
        progressFrame();
      }

      renderer.draw(arrow, getX(), getY() + y_offset + WORLD_SCALE*0.8f*texture.getRegionHeight());
    }
  }

  @Override
  public void progressFrame() {
    if (arrow.getFrame() < arrow.getSize()-1)  arrow.setFrame(arrow.getFrame()+1);
    else arrow.setFrame(0);
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
