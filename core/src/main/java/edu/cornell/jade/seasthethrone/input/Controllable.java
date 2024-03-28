/*
 * Controllable.java
 *
 * This class provides an interface for identical movements between the player and the AI.
 * The player controls with an input device, while the AI is controlled with an AI algorithm.
 *
 */

package edu.cornell.jade.seasthethrone.input;

import com.badlogic.gdx.math.Vector2;

public interface Controllable {

  /** Updates the horizontal movement of the character. */
  default public void moveHorizontal(float movement) {}

  /** Updates the vertical movement of the character. */
  default public void moveVertical(float movement) {}

  /** Updates the primary attack state of the character. */
  default public void pressPrimary() {}

  /** Updates the secondary attack state of the character. */
  default public void pressSecondary() {}

  /** Updates the interact state of the character. */
  default public void pressInteract() {}

  /** Updates the direction the character is pointing for dash/shoot */
  default public void updateDirection(Vector2 mouseDir) {}

  /** Returns the current location of the controllable object */
  default public Vector2 getLocation(){ return null; }

  /** Changes the game state from paused - unpaused and vice versa */
  default public void pressPaused() {}
  /** Scrolls up in the pause menu */
  default public void pressUp() {}

  /** Scrolls down in the pause menu */
  default public void pressDown() {}

}
