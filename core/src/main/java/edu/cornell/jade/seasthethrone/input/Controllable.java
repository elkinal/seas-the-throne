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
  default public void moveHorizontal(float movement) {
  }

  /** Updates the vertical movement of the character. */
  default public void moveVertical(float movement) {
  }

  /** Updates the primary attack state of the character. */
  default public void pressPrimary() {
  }

  /** Updates the secondary attack state of the character. */
  default public void pressSecondary() {
  }

  /** Updates the tertiary attack state of the character. */
  default public void pressTertiary() {
  }

  /** Updates the interact state of the character. */
  default public void pressInteract() {
  }

  /** Toggles the dash mode of the player. */
  default public void toggleDashMode() {
  }

  /** Updates the direction the character is pointing for dash/shoot */
  default public void updateDirection(Vector2 mousePos) {
  }

  /** Updates the pause state of the game */
  default public void pressPause() {}

  /**
   * Returns the current location of the controllable object. This is useful for
   * computing the position of a simulated "mouse" when a controller is used as
   * the input device.
   */
  public Vector2 getLocation();
}
