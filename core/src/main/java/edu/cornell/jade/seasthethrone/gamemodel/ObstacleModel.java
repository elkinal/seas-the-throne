package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class ObstacleModel extends ComplexModel implements Renderable {
  private TextureRegion texture;

  private final float WORLD_SCALE;

  public ObstacleModel(LevelObject obs, float scale) {
    // TODO: extend for generic model, not just BoxModel
    super(obs.x, obs.y);
    this.texture = obs.texture;
    this.WORLD_SCALE = scale;
    BoxModel hitbox = new BoxModel(obs.x, obs.y, obs.width, obs.height);
    hitbox.setBodyType(BodyDef.BodyType.StaticBody);
    bodies.add(hitbox);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    Vector2 pos = getPosition();
    float y_offset = WORLD_SCALE * texture.getRegionHeight() / 2f - getModel().getHeight() / 2f;
    renderer.draw(texture, pos.x, pos.y + y_offset);
  }

  @Override
  public void progressFrame() {
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

  public BoxModel getModel() {
    return (BoxModel) bodies.get(0);
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

  protected boolean createJoints(World world) {
    return true;
  }

  protected void createFixtures() {}

  protected void releaseFixtures() {}
}
