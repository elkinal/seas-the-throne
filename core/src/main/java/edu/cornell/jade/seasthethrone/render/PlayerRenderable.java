package edu.cornell.jade.seasthethrone.render;

public interface PlayerRenderable extends Renderable {
  /**
   * Returns if the spear of the player is extended and the animation should begin
   * playing.
   *
   * @return if the spear animation should begin to play
   */
  public boolean spearExtended();
}
