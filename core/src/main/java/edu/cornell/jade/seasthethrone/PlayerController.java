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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.util.*;
public class PlayerController implements Controllable {
    //fields to manage game state
    /** Whether the primary (dash) button is pressed. */
    protected boolean dashPressed;

    /** Whether the secondary (shoot) button is pressed. */
    protected boolean didShoot;

    /** Movement left/right */
    private float hOffset;

    /** Movement up/down */
    private float vOffset;

    /**
     * Returns horizontal movement of player.
     */
    public float getHOffset(){
        return hOffset;
    }

    /**
     * Returns vertical movement of player.
     */
    public float getVOffset(){
        return vOffset;
    }

    /**
     * Returns whether the player is dashing.
     */
    public boolean getPrimary(){
        return dashPressed;
    }

    /**
     * Returns whether the player is shooting.
     */
    public boolean getSecondary(){
        return didShoot;
    }

    /**
     * Creates a new player controller.
     */
    public PlayerController() {
        dashPressed = false;
        didShoot = false;
        hOffset = 0.0f;
        vOffset = 0.0f;
    }

    /**
     * Process horizontal movement.
     *
     * 1 = right, -1 = left, 0 = still
     *
     * @param movement amount of horizontal movement
     */
    public void moveHorizontal(float movement){
        hOffset = movement;
    }

    /**
     * Process horizontal movement.
     *
     * 1 = up, -1 = down, 0 = still
     *
     * @param movement amount of horizontal movement
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

    /**
     * Process secondary button pressed.
     *
     * @param pressed true if the button is pressed
     */
    public void pressSecondary(boolean pressed){
        didShoot = pressed;
    }


}
