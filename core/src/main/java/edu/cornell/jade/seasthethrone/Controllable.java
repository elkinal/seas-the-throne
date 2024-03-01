/*
 * Controllable.java
 *
 * This class provides an interface for identical movements between the player and the AI.
 * The player controls with an input device, while the AI is controlled with an AI algorithm.
 *
 */

package edu.cornell.jade.seasthethrone;

public interface Controllable {
//    /**
//     * Updates the horizontal (left) movement of the character.
//     */
//    public float moveLeft();
//
//    /**
//     * Updates the horizontal (right) movement of the character.
//     */
//    public void moveRight();
//
//    /**
//     * Updates the horizontal (right) movement of the character.
//     */
//    public void moveUp();
//
//    /**
//     * Updates the horizontal (right) movement of the character.
//     */
//    public void moveDown();

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
    public void pressPrimary(boolean pressed);

    /**
     * Updates the secondary attack state of the character.
     */
    public void pressSecondary(boolean pressed);


}
