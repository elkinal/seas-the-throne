package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class PortalModel extends BoxModel implements Renderable {

  /** Portal texture */
  private TextureRegion texture;

  /** The JSON of the room/level this portal leads to */
  private String target;

  /** The location to spawn the player at in the target level */
  private Vector2 playerLoc;

  /** Default to transparent texture if none is specified */
  private final TextureRegion DEFUALT_TEXTURE = new TextureRegion(new Texture("empty.png"));

  /** Checkpoint required for this portal to be active */
  private int requiredCheckpoint;

  /** If this portal is activated */
  private boolean activated;

  /**
   * Constructs a PortalModel
   *
   * @param x x coordiante of the portal
   * @param y y coordiante of the portal
   * @param width width of the portal model's collision rectangle
   * @param height height of the portal model's collision rectangle
   * @param target a code specifying where this portal leads
   */
  public PortalModel(float x, float y, float width, float height, String target, Vector2 playerLoc) {
    super(x, y, width, height);
    this.setSensor(true);
    this.target = target;
    this.texture = DEFUALT_TEXTURE;
    this.playerLoc = playerLoc;
    this.activated = false;
  }

  /**
   * Constructs a PortalModel
   *
   * @param x x coordiante of the portal
   * @param y y coordiante of the portal
   * @param width width of the portal model's collision rectangle
   * @param height height of the portal model's collision rectangle
   * @param target a code specifying where this portal leads
   * @param texture what the portal model will be rendered as
   */

  public PortalModel(float x, float y, float width, float height, String target,
                     Vector2 playerLoc, TextureRegion texture, int checkpointID) {
    this(x, y, width, height, target, playerLoc);
    this.texture = texture;
    this.requiredCheckpoint = checkpointID;
  }

  public PortalModel(LevelObject portal) {
    this(portal.x, portal.y, portal.width, portal.height,
            portal.target, portal.playerLoc, portal.texture, portal.checkpointID);
  }

  public String getTarget() {
    return target;
  }

  public Vector2 getPlayerLoc() { return playerLoc; }

  public void setActivated(boolean activated) { this.activated = activated; }

  public boolean isActivated() { return activated; }
  public int getRequiredCheckpoint() { return requiredCheckpoint; }

  @Override
  public void draw(RenderingEngine renderer) {
    if (activated) {
      renderer.draw(texture, getX(), getY());
    }
  }

  @Override
  public FilmStrip progressFrame() {
    return null;
  }

  @Override
  public void alwaysUpdate() {}

  @Override
  public void neverUpdate() {}

  @Override
  public void setAlwaysAnimate(boolean animate) {}

  @Override
  public boolean alwaysAnimate() {
    return false;
  }

  public int getFrameNumber() {
    return 0;
  }

  public void setFrameNumber(int frameNumber) {}

  public FilmStrip getFilmStrip() {
    return null;
  }

  public int getFramesInAnimation() {
    return 0;
  }
}
