package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/**
 * An interface defining the required information for a {@link RenderingEngine} to render a player.
 */
public interface PlayerRenderable extends Renderable {
  /**
   * Returns the player's texture for when they face up
   *
   * @return the player's texture when facing up
   */
  public Texture getTextureUp();

  /**
   * Returns the player's texture for when they face down
   *
   * @return the player's texture when facing down
   */
  public Texture getTextureDown();

  /**
   * Returns the player's texture for when they face left
   *
   * @return the player's texture when facing left
   */
  public Texture getTextureLeft();

  /**
   * Returns the player's texture for when they face right
   *
   * @return the player's texture when facing right
   */
  public Texture getTextureRight();
  /**
   * Returns if the spear of the player is extended and the animation should begin playing.
   *
   * @return if the spear animation should begin to play
   */
  public boolean spearExtended();

  /**
   * Returns the direction player is facing
   *
   * @return direction of player
   */
  public Direction direction();

  /**
   * Returns whether the player is dashing
   *
   * @return true if player is dashing and false if not
   */
  public boolean isDashing();

  public default void draw(RenderingEngine renderer) {

    int frame = getFrameNumber();
    FilmStrip filmStrip = getFilmStrip();
    switch (direction()) {
      case UP:
        filmStrip.setTexture(getTextureUp());
        break;
      case DOWN:
        filmStrip.setTexture(getTextureDown());
        break;
      case LEFT:
        filmStrip.setTexture(getTextureLeft());
        break;
      case RIGHT:
        filmStrip.setTexture(getTextureRight());

        break;
    }
    filmStrip.setFrame(frame);

    Vector2 pos = getPosition();
    renderer.draw(filmStrip, pos.x, pos.y);

//    setFrameNumber((frame + 1) % getFramesInAnimation());


  }
}
