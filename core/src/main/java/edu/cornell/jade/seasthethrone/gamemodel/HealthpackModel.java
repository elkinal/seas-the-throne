package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class HealthpackModel extends BoxModel implements Interactable, Renderable {

  private TextureRegion texture;

  private final float INTERACT_RANGE = 3f;

  public HealthpackModel(LevelObject obs) {
    super(obs.x, obs.y, obs.width, obs.height);

    this.texture = new TextureRegion(new Texture("levels/healthpack.png"));
    setBodyType(BodyDef.BodyType.StaticBody);
  }

  public boolean playerInRange(Vector2 playerPos) {
    return Math.abs(Vector2.dst(getX(), getY(), playerPos.x, playerPos.y)) < INTERACT_RANGE;
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
