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
   * Returns the player's texture for when they dash up
   *
   * @return the player's texture when facing up
   */
  public Texture getTextureUpDash();

  /**
   * Returns the player's texture for when they dash down
   *
   * @return the player's texture when facing down
   */
  public Texture getTextureDownDash();

  /**
   * Returns the player's texture for when they dash left
   *
   * @return the player's texture when facing left
   */
  public Texture getTextureLeftDash();

  /**
   * Returns the player's texture for when they dash right
   *
   * @return the player's texture when facing right
   */
  public Texture getTextureRightDash();
  /**
   * Returns if the spear of the player is extended and the animation should begin playing.
   *
   * @return if the spear animation should begin to play
   */
  public boolean spearExtended();
  /**
   * Returns the player's texture for when they face up idle
   *
   * @return the player's texture when facing up idle
   */
  public Texture getIdleUp();
  /**
   * Returns the player's texture for when they face down idle
   *
   * @return the player's texture when facing down idle
   */
  public Texture getIdleDown();
  /**
   * Returns the player's texture for when they face right idle
   *
   * @return the player's texture when facing right idle
   */
  public Texture getIdleRight();
  /**
   * Returns the player's texture for when they face left idle
   *
   * @return the player's texture when facing left idle
   */
  public Texture getIdleLeft();

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
  /**
   * Returns whether the player is idle
   *
   * @return true if player is idle and false if not
   */
  public boolean isIdle();

  /** Returns the number of current health points of the player. */
  public int getHealth();

  public default void draw(RenderingEngine renderer) {

    FilmStrip filmStrip = getFilmStrip();
    switch (direction()) {
      case UP:
        if (isDashing())
          filmStrip.setTexture(getTextureUpDash());
        else if (isIdle())
          filmStrip.setTexture(getIdleUp());
        else
          filmStrip.setTexture(getTextureUp());
        break;
      case DOWN:
        if (isDashing())
          filmStrip.setTexture(getTextureDownDash());
        else if (isIdle())
          filmStrip.setTexture(getIdleDown());
        else
          filmStrip.setTexture(getTextureDown());
        break;
      case LEFT:
        if (isDashing())
          filmStrip.setTexture(getTextureLeftDash());
        else if (isIdle())
          filmStrip.setTexture(getIdleLeft());
        else
          filmStrip.setTexture(getTextureLeft());
        break;
      case RIGHT:
        if (isDashing())
          filmStrip.setTexture(getTextureRightDash());
        else if (isIdle())
          filmStrip.setTexture(getIdleRight());
        else
          filmStrip.setTexture(getTextureRight());
        break;
    }
    int frame = getFrameNumber();
    filmStrip.setFrame(frame);

    Vector2 pos = getPosition();
    renderer.draw(filmStrip, pos.x, pos.y, 0.12f);

//    setFrameNumber((frame + 1) % getFramesInAnimation());


  }
}
