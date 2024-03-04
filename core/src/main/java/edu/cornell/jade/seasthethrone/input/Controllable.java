/*
 * Controllable.java
 *
 * This class provides an interface for identical movements between the player and the AI.
 * The player controls with an input device, while the AI is controlled with an AI algorithm.
 *
 */

package edu.cornell.jade.seasthethrone.input;

public interface Controllable {

  /**
   * Updates the horizontal movement of the character.
   */
  public void moveHorizontal(float movement);

  /**
   * Updates the vertical movement of the character.
   */
  public void moveVertical(float movement);

  /**
   * Updates the primary attack state of the character.
   */
  public void pressPrimary();

}
