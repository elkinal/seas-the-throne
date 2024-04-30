package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.jade.seasthethrone.InteractableController;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class HealthpackModel extends BoxModel implements Interactable, Renderable {

  private TextureRegion texture;

  public HealthpackModel(LevelObject obs) {
    super(obs.x, obs.y, obs.width, obs.height);

    this.texture = new TextureRegion(new Texture("levels/healthpack.png"));

  }

  @Override
  public void interact() {

  }

  @Override
  public boolean isInteracted() {
    return false;
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
