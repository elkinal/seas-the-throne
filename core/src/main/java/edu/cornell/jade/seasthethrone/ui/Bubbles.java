package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class Bubbles implements Renderable {
  /** The number of frames in the animation */
  private int framesInAnimation;

  /** Texture for the bubbles */
  private Texture texture;

  /** Filmstrip for the bubbles */
  public FilmStrip filmstrip;

  /** current animation frame */
  private int animationFrame;

  /** The number of frames to skip before animating the next player frame */
  private int frameDelay;

  /** The number of frames (to account for frame delay) */
  private int frameCounter;

  private final int ORIGIN_X = 0;
  private final int ORIGIN_Y = 0;

  /** Creates bubbles */
  public Bubbles() {
    framesInAnimation = 6;
    animationFrame = 0;
    frameDelay = 13;

    texture = new Texture("ui/bubbleeffect.png");
    filmstrip = new FilmStrip(texture, 1, framesInAnimation);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    progressFrame();
    renderer.draw(
        filmstrip,
        ORIGIN_X,
        ORIGIN_Y,
        renderer.getGameCanvas().getWidth(),
        renderer.getGameCanvas().getHeight());
  }

  @Override
  public void progressFrame() {
    filmstrip.setFrame(animationFrame);

    if (frameCounter % frameDelay == 0) {
      animationFrame = (animationFrame + 1) % framesInAnimation;
      //        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
    }
    frameCounter++;
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
}
