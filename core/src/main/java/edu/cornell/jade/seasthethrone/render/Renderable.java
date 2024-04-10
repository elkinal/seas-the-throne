package edu.cornell.jade.seasthethrone.render;

public interface Renderable {
  /**
   * Draws the given player
   *
   * @param renderer the renderer to render the given object.
   */
  public void draw(RenderingEngine renderer);

  /** Progresses the frames of the renderable. The default does nothing (static renderables). */
  public void progressFrame();

  /** Marks a Renderable as updateable (will continue to be animated). */
  public void alwaysUpdate();

  /** Marks a Renderable as not updateable (will not continue to be animated). */
  public void neverUpdate();

  /**
   * Returns whether Renderable should be updateable (will continue to be animated). Default does
   * nothing (static renderables).
   */
  public boolean getUpdate();
}
