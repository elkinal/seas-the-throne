/*
 * InputController.java
 *
 * This class collects all actions of characters. It reads from either the player
 * input or the AI controller to determine the move for each character. It then
 * calls respective methods in the controller class to process the input. This class also
 * contains any key remapping information.
 *
 * Contains code written by Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 *
 */

package edu.cornell.jade.seasthethrone.input;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import edu.cornell.jade.seasthethrone.util.XBoxController;

/**
 * Processes movements for player and AI
 */
public class InputController {

  /** List of all the player input controllers */
  private ArrayList<Controllable> players;

  /** XBox Controller support */
  private XBoxController xbox;

  /** Whether the reset button was pressed. */
  protected boolean resetPressed;

  /**
   * Returns true if the reset button was pressed.
   *
   * @return true if the reset button was pressed.
   */
  public boolean didReset() {
    return resetPressed;
  }

  /**
   * Adds p to 'players.' Adding a duplicated object does nothing.
   *
   * @param p the PlayerController to be added
   */
  public void add(PlayerController p) {
    if (!players.contains(p)) {
      players.add(p);
    }
  }

  /**
   * Removes p from 'players.' Removing an object which was never added does
   * nothing.
   *
   * @param p the PlayerController to be removed
   */
  public void remove(PlayerController p) {
    if (players != null) {
      players.remove(p);
    }
  }

  /**
   * Constructor for InputController.
   */
  public InputController() {
    players = new ArrayList<>();
  }

  /**
   * Updates the state of this object (position) both vertically and horizontally.
   */
  public void update() {
    for (Controllable p : players) {
      readInput(p);
    }
  }

  /**
   * Reads the input for the player and converts the result into game logic.
   */
  public void readInput(Controllable p) {
    // Check to see if a GamePad is connected
    if (xbox != null && xbox.isConnected()) {
      readController(p);
    } else {
      readKeyboard(p);
    }
  }

  /**
   * Reads input from an XBox controller connected to this computer to determine
   * the
   * actions of the player. Then calls the corresponding methods in the controller
   * to process the actions.
   *
   * Change the controller keys mapped to primary and secondary actions here.
   *
   * @param obj Controller for the player
   */
  private void readController(Controllable obj) {
    resetPressed = xbox.getA();

    obj.moveHorizontal(xbox.getLeftX());
    obj.moveVertical(xbox.getLeftY());

    if (xbox.getRightTrigger() > 0.6f) {
      obj.pressPrimary();
    }
  }

  /**
   * Reads input from the keyboard to determine the actions of the player.
   * Then calls the corresponding methods in the controller to process the
   * actions.
   * Reads from the keyboard regardless of whether an X-Box controller is
   * connected.
   *
   * Change the keyboard keys mapped to primary and secondary actions here.
   *
   * @param obj Controller for the player
   */
  private void readKeyboard(Controllable obj) {
    float hoff = 0;
    float voff = 0;
    resetPressed = Gdx.input.isKeyPressed(Input.Keys.R);

    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      hoff += 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      hoff -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
      voff += 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
      voff -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
      obj.pressPrimary();
    }
    obj.moveHorizontal(hoff);
    obj.moveVertical(voff);
  }

}
