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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.pausemenu.UIController;
import edu.cornell.jade.seasthethrone.util.Controllers;
import edu.cornell.jade.seasthethrone.util.XBoxController;
import java.util.*;

/** Processes movements for player and AI */
public class InputController {
  /** Viewport to unproject screen coordinates */
  Viewport viewport;

  /** List of all the player input controllers */
  private ArrayList<Controllable> players;

  /** List of all the UI input controllers */
  private ArrayList<Controllable> overlays;

  /** XBox Controller support */
  private XBoxController xbox;

  /** Whether the reset button was pressed. */
  protected boolean resetPressed;

  /**
   * Cache vector to return containing the dash coordinates of the previous read
   */
  Vector2 dashCoordCache;

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
  public void add(Controllable p) {
    if (!players.contains(p) && p instanceof PlayerController) {
      players.add(p);
    }
    if (!overlays.contains(p) && p instanceof UIController) {
      overlays.add(p);
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
   * Constructor for InputController with a parameter to convert between screens
   * and the world.
   *
   * @param screenToWorld viewport to support an unproject operation to convert
   *                      screen coord to
   *                      mouse coord.
   */
  public InputController(Viewport screenToWorld) {
    this.players = new ArrayList<>();
    this.overlays = new ArrayList<>();
    this.viewport = screenToWorld;
    this.dashCoordCache = new Vector2();

    // initializing the xbox controller
    if (Controllers.get().getControllers().size > 0) {
      xbox = Controllers.get().getXBoxControllers().get(0);
      xbox.setDeadZone(0.2f);
    }
  }

  /**
   * Updates the state of this object (position) both vertically and horizontally.
   */
  public void update() {
    // Reading inputs for players
    for (Controllable p : players) {
      readInput((PlayerController) p);
    }

    // Reading inputs for menu items
    for (Controllable o : overlays) {
      readInput((UIController) o);
    }
  }

  /** Reads the input for the player and converts the result into game logic. */
  public void readInput(PlayerController p) {
    // Check to see if a GamePad is connected

    if (xbox != null && xbox.isConnected()) {
      readController(p);
    } else {
      readMouse(p);
      readKeyboard(p);
    }
  }

  /** Reads the input for the UI and converts the result into game logic. */
  public void readInput(UIController p) {
    // Check to see if a GamePad is connected

    if (xbox != null && xbox.isConnected()) {
      readController(p);
    } else {
      readKeyboard(p);
    }
  }

  /**
   * Reads input from an XBox controller connected to this computer to determine
   * the actions of the
   * player. Then calls the corresponding methods in the controller to process the
   * actions.
   *
   * <p>
   * Change the controller keys mapped to primary and secondary actions here.
   *
   * @param obj Controller for the player
   */
  private void readController(Controllable obj) {
    float hoff = 0;
    float voff = 0;

    hoff = xbox.getLeftX();
    voff = -xbox.getLeftY();

    Vector2 location = obj.getLocation();
    dashCoordCache.set(location.x + hoff, location.y + voff);
    obj.updateDirection(dashCoordCache);

    // dashing
    if (xbox.getRightTrigger() > 0.6f) {
      obj.pressPrimary();
    }
    // dashing
    if (xbox.getLeftTrigger() > 0.6f) {
      obj.pressSecondary();
    }

    resetPressed = xbox.getY();

    obj.moveHorizontal(hoff);
    obj.moveVertical(voff);
  }

  /**
   * Reads input from the keyboard to determine the actions of the player. Then
   * calls the
   * corresponding methods in the controller to process the actions. Reads from
   * the keyboard
   * regardless of whether an X-Box controller is connected.
   *
   * <p>
   * Change the keyboard keys mapped to primary and secondary actions here.
   *
   * @param obj Controller for the player
   */
  private void readKeyboard(Controllable obj) {
    float hoff = 0;
    float voff = 0;
    resetPressed = Gdx.input.isKeyPressed(Input.Keys.R);

    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      hoff += 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      hoff -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      voff += 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      voff -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
      obj.pressPrimary();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
      obj.pressSecondary();
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      System.out.println(obj.getClass());
      obj.switchPaused();
    }

    obj.moveHorizontal(hoff);
    obj.moveVertical(voff);
  }

  /**
   * Reads input from the mouse to determine the actions of the player. Then calls
   * the corresponding
   * methods in the controller to process the actions. Reads from the mouse
   * regardless of whether an
   * X-Box controller is connected.
   *
   * <p>
   * Updates position in world coordinates.
   *
   * @param obj Controller for the player
   */
  private void readMouse(Controllable obj) {
    Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
    dashCoordCache.set(mousePos.x, mousePos.y);
    viewport.unproject(dashCoordCache);
    obj.updateDirection(dashCoordCache);
  }

}
