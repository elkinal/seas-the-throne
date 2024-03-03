package edu.cornell.jade.seasthethrone.render;

public interface PlayerRenderable extends Renderable {
  /**
   * Returns if the spear of the player is extended and the animation should begin
   * playing.
   *
   * @return if the spear animation should begin to play
   */
  public boolean spearExtended();
  /**
   * Returns the frame number the player is currently at
   *
   * @return current frame number for player
   */
  public int frameNumber();
  /**
   * Returns the direction player is facing
   *
   * @return direction of player, 0 if up, 1 if down, 2 if left, 3 if right
   */
  public int direction();
}
