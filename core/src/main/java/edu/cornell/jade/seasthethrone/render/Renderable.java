package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public interface Renderable {

  /**
   * Returns the position of the object.
   *
   * @return the position of the object
   */
  Vector2 getPosition();

  /**
   * Draws the given player
   *
   * @param renderer the renderer to render the given object.
   */
  public void draw(RenderingEngine renderer);

  /**
   * Returns the frame in animation the player is currently at
   *
   * @return current frame in animation the player is at
   */
  public int getFrameNumber();

  /**
   * Sets the frame in animation the player is currently at
   *
   * @param frameNumber the frame in animation the player is currently at
   */
  public void setFrameNumber(int frameNumber);

  /**
   * Gets a cache film strip to be used for animation. It should be the same object (or a copy of
   * it) over all calls
   *
   * @return film strip used for animation
   */
  public FilmStrip getFilmStrip();

  /**
   * Returns the number of frames in the walking animation
   *
   * @return the number of frames in the walking animation
   */
  public int getFramesInAnimation();
}
