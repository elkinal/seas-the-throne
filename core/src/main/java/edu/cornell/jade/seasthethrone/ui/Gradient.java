package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class Gradient implements Renderable {
  /** Texture for the gradient */
  private TextureRegion texture;

  // origin is always 0,0
  private int originX;
  private int originY;

  /** Creates a new Gradient */
  public Gradient() {
    texture = new TextureRegion(new Texture("watergradient.png"));
    originX = 0;
    originY = 0;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    renderer
        .getGameCanvas()
        .drawUI(
            texture,
            originX,
            originY,
            renderer.getGameCanvas().getWidth(),
            renderer.getGameCanvas().getHeight());
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
