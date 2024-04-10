package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.jade.seasthethrone.render.Renderable;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class Tile implements Renderable {

  /** Position of the bottom left corner of the tile */
  private Vector2 pos;

  private TextureRegion texture;

  public Tile(TextureRegion tex, float x, float y) {
    pos = new Vector2(x, y);
    texture = tex;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    renderer.draw(getTexture(), getPosition().x, getPosition().y);
  }

  @Override
  public void progressFrame() {}

  @Override
  public void alwaysUpdate() {}

  @Override
  public void neverUpdate() {}

  @Override
  public boolean getUpdate() {
    return true;
  }

  public Vector2 getPosition() {
    return pos;
  }

  public TextureRegion getTexture() {
    return texture;
  }

  public void setTexture(TextureRegion tex) {
    texture = tex;
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
