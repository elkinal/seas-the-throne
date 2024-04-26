package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class Gradient implements Renderable {
  /** Texture for the gradient */
  private TextureRegion texture;

  /** Scale of health bar on the screen */
  private final float SCALE = 100f;

  /** Creates a new Gradient */
  public Gradient() {
    texture = new TextureRegion(new Texture("watergradient.png"));
  }

  @Override
  public void draw(RenderingEngine renderer) {
    renderer.draw(texture, 0, 0);
  }

  @Override
  public void progressFrame() {}

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
}
