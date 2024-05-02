package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

import java.util.HashMap;

public class BackgroundImage implements Renderable {
  private Vector2 position;
  private int width;
  private int height;
  private TextureRegion texture;
  private float opacity;

  private Array<HashMap<String, Object>> properties;

  public BackgroundImage(Vector2 pos, int width, int height, TextureRegion texture, float op) {
    position = pos;
    this.texture = texture;
    this.height = height;
    this.width = width;
    opacity = op;
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
  public void setAlwaysAnimate(boolean animate) {}

  @Override
  public boolean alwaysAnimate() {
    return false;
  }

  public Vector2 getPosition() {
    return position;
  }

  public void setPosition(Vector2 pos) {
    position = pos;
  }

  public TextureRegion getTexture() {
    return texture;
  }

  public float getOpacity() {
    return opacity;
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
