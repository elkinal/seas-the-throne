/*
 * PlayerController.java
 *
 * This class processes the primary gameplay. It will update the states
 * of the player depending on which controls are activated.
 *
 * Contains code written by Walker M. White
 *
 */

package edu.cornell.jade.seasthethrone;

public class PlayerController implements Controllable {
    /** Whether the primary (dash) button is pressed. */
    protected boolean dashPressed;

    /** Movement left/right */
    private float hOffset;

    /** Movement up/down */
    private float vOffset;

    /**
     * Returns horizontal movement of player. This value should be multiplied by
     * the player's velocity to calculate the actual movement.
     *
     * @return the horizontal movement of player (from -1 to 1)
     */
    public float getHOffset(){
        return hOffset;
    }

    /**
     * Returns vertical movement of player. This value should be multipleid by
     * the player's velocity to calculate the actual movement.
     *
     * @return the vertical movement of player (from -1 to 1)
     */
    public float getVOffset(){
        return vOffset;
    }

    /**
     * Returns whether the player is dashing.
     *
     * @return true if the player is dashing
     */
    public boolean getPrimary(){
        return dashPressed;
    }

    /**
     * Creates a new player controller.
     */
    public PlayerController() {
        dashPressed = false;
        hOffset = 0.0f;
        vOffset = 0.0f;
    }

    /**
     * Process horizontal movement.
     *
     * 1 (or positive) = right, -1 (or negative) = left, 0 = still
     *
     * @param movement amount of horizontal movement
     */
    public void moveHorizontal(float movement){
        hOffset = movement;
    }

    /**
     * Process vertical movement.
     *
     * 1 (or positive) = up, -1 (or negative) = down, 0 = still
     *
     * @param movement amount of vertical movement
     */
    public void moveVertical(float movement){
        vOffset = movement;
    }

    /**
     * Process primary button pressed.
     *
     * @param pressed true if the button is pressed
     */
    public void pressPrimary(boolean pressed){
        dashPressed = pressed;
    }

}
