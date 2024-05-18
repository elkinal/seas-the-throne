package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Credits implements Renderable{
  /** Texture for the credits */
  private TextureRegion texture;

  // origin is always 0,0
  private int originX;
  private int originY;
  public Credits (){
    texture = new TextureRegion(new Texture("credits.png"));
    originX = 0;
    originY = 0;
  }
  @Override
  public void draw(RenderingEngine renderer) {
    System.out.println("drawing");
    renderer.draw(
            texture,
            originX,
            originY,
            renderer.getGameCanvas().getWidth(),
            renderer.getGameCanvas().getHeight());
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
