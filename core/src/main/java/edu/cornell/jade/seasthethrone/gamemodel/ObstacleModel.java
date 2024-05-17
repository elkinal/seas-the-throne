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

public class ObstacleModel extends BoxModel implements Renderable {
  private TextureRegion texture;

  private FilmStrip filmStrip;

  private int framesInAnimation;

  private int frameCounter;

  private int frameDelay;

  private int animationFrame;
  private final float WORLD_SCALE;

  private boolean animated;

  public ObstacleModel(LevelObject obs, float scale) {
    // TODO: extend for generic model, not just BoxModel
    super(obs.x, obs.y, obs.width, obs.height);
    this.texture = obs.texture;
    this.WORLD_SCALE = scale;
    this.framesInAnimation = obs.framesInAnimation;
    this.animated = obs.animated;
    this.animationFrame = 0;
    this.frameDelay = 10;

    this.filmStrip = new FilmStrip(this.texture.getTexture(), 1, framesInAnimation);
    setBodyType(BodyDef.BodyType.StaticBody);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    if (animated) setActive(false);
    progressFrame();
    Vector2 pos = getPosition();

    float y_offset = (WORLD_SCALE * texture.getRegionHeight() - getHeight())/ 2f;
    float x_offset = WORLD_SCALE * texture.getRegionWidth()/2f - getWidth() / 2f;
    if (animated) {
      renderer.draw(filmStrip, pos.x, pos.y+y_offset);
    } else {
      renderer.draw(texture, pos.x, pos.y + y_offset);
    }
  }
  public void progressFrame() {
    FilmStrip filmStrip = getFilmStrip();
    int frame = getFrameNumber();
    filmStrip.setFrame(frame);

    if (frameCounter % frameDelay == 0) {
      setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
    }
    frameCounter += 1;
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

  public void setFrameNumber(int frameNumber) {
    this.animationFrame = frameNumber;
  }

  public FilmStrip getFilmStrip() {
    return filmStrip;
  }

  private int getFrameNumber() {
    return animationFrame;
  }

  private int getFramesInAnimation() {
    return framesInAnimation;
  }

}
