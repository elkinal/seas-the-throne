package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

  /** Default to transparent texture if none is specified */
  private final TextureRegion DEFUALT_TEXTURE = new TextureRegion(new Texture("empty.png"));

  public PortalModel(float x, float y, float width, float height, String target) {
    super(x, y, width, height);
    this.setSensor(true);
    this.target = target;
    this.texture = DEFUALT_TEXTURE;
  }

  public PortalModel(float x, float y, float width, float height, String target, TextureRegion texture) {
    this(x, y, width, height, target);
    this.texture = texture;
  }

  public PortalModel(LevelObject portal) {
    this(portal.x, portal.y, portal.width, portal.height, portal.target, portal.texture);
  }

  public String getTarget() {
    return target;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    renderer.draw(texture, getX(), getY());
  }

  @Override
  public int getFrameNumber() {
    return 0;
  }

  @Override
  public void setFrameNumber(int frameNumber) {
  }

  @Override
  public FilmStrip getFilmStrip() {
    return null;
  }

  @Override
  public int getFramesInAnimation() {
    return 0;
  }
}
