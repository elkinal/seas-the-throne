package edu.cornell.jade.seasthethrone.render;

import edu.cornell.jade.seasthethrone.util.FilmStrip;

public interface Renderable {
  /**
   * Draws the given player
   *
   * @param renderer the renderer to render the given object.
   */
  public void draw(RenderingEngine renderer);

  /** Progresses the frames of the renderable. The default does nothing (static renderables). */
  public FilmStrip progressFrame();

  /** Marks a Renderable as updateable (will continue to be animated). */
  public void alwaysUpdate();

  /** Marks a Renderable as not updateable (will not continue to be animated). */
  public void neverUpdate();

  /**
   * Returns whether Renderable should override rendering engine's animation state (always/never
   * animate). This is used for things such as player death, when everything else pauses.
   *
   * @param animate true if the Renderable should continue to be animated while the others are not
   */
  public void setAlwaysAnimate(boolean animate);

  /**
   * Returns whether a Renderable should always be animated. Default should be false.
   *
   * @return true if should always be animated
   */
  public boolean alwaysAnimate();
}
