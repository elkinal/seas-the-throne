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
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.util.Controllers;
import edu.cornell.jade.seasthethrone.util.XBoxController;
import java.util.*;

/** Processes movements for player and AI */
public class InputController {
  /** Viewport to unproject screen coordinates */
  Viewport viewport;

  /** List of all the player input controllers */
  private ArrayList<Controllable> controllables;

  /** XBox Controller support */
  private XBoxController xbox;

  /** Primary button from preferences */
  private String controllerPrimary;

  /** Secondary button from preferences */
  private String controllerSecondary;

  /** Whether the reset button was pressed. */
  protected boolean resetPressed;

  /** Cache vector to return containing the dash coordinates of the previous read */
  Vector2 dashCoordCache;

  /** Preference file for the player */
  Preferences prefs;

  /**
   * Returns true if the reset button was pressed.
   *
   * @return true if the reset button was pressed.
   */
  public boolean didReset() {
    return resetPressed;
  }

  /**
   * Adds c to 'controllables.' Adding a duplicated object does nothing.
   *
   * @param c the Controllable to be added
   */
  public void add(Controllable c) {
    if (!controllables.contains(c)) {
      controllables.add(c);
    }
  }

  /**
   * Removes c from 'controllables.' Removing an object which was never added does nothing.
   *
   * @param c the Controllable to be removed
   */
  public void remove(Controllable c) {
    if (controllables != null) {
      controllables.remove(c);
    }
  }

  /**
   * Constructor for InputController with a parameter to convert between screens and the world.
   *
   * @param screenToWorld viewport to support an unproject operation to convert screen coord to
   *     mouse coord.
   */
  public InputController(Viewport screenToWorld) {
    this.controllables = new ArrayList<>();
    this.viewport = screenToWorld;
    this.dashCoordCache = new Vector2();
    prefs = Gdx.app.getPreferences("options");
    getPrefs();

    // initializing the xbox controller
    if (Controllers.get().getControllers().size > 0) {
      xbox = Controllers.get().getXBoxControllers().get(0);
      xbox.setDeadZone(0.2f);
    }
  }

  /** Updates the state of this object (position) both vertically and horizontally. */
  public void update() {
    for (Controllable p : controllables) {
      readInput(p);
    }
  }

  public void getPrefs() {
    // get preference controls
    controllerPrimary = prefs.getString("attackButton");
    controllerSecondary = prefs.getString("dashButton");
  }

  /** Reads the input for the player and converts the result into game logic. */
  public void readInput(Controllable p) {
    // Check to see if a GamePad is connected
    if (xbox != null && xbox.isConnected()) {
      readController(p);
    } else {
      readMouse(p);
      readKeyboard(p);
    }
  }

  /**
   * Reads input from an XBox controller connected to this computer to determine the actions of the
   * player. Then calls the corresponding methods in the controller to process the actions.
   *
   * <p>Change the controller keys mapped to primary and secondary actions here.
   *
   * @param obj Controller for the player
   */
  private void readController(Controllable obj) {
    // dash/shoot indicator
    float hind = xbox.getRightX();
    float vind = -xbox.getRightY();

    Vector2 location = obj.getLocation();
    if (location != null) {
      dashCoordCache.set(location.x + hind, location.y + vind);
      obj.updateDirection(dashCoordCache);
    }

    // dashing
    switch (controllerPrimary) {
      case "X":
        if (xbox.getX()) {
          obj.pressPrimary();
        }
        break;
      case "Y":
        if (xbox.getY()) {
          obj.pressPrimary();
        }
        break;
      case "A":
        if (xbox.getA()) {
          obj.pressPrimary();
        }
        break;
      case "B":
        if (xbox.getB()) {
          obj.pressPrimary();
        }
        break;
      case "LS":
        if (xbox.getLStick()) {
          obj.pressPrimary();
        }
        break;
      case "RS":
        if (xbox.getRStick()) {
          obj.pressPrimary();
        }
        break;
      case "LB":
        if (xbox.getLBumper()) {
          System.out.println("left bumper new preference");
          obj.pressPrimary();
        }
        break;
      case "RB":
        if (xbox.getRBumper()) {
          obj.pressPrimary();
        }
        break;
      case "LT":
        if (xbox.getLeftTrigger() > 0.6f) {
          System.out.println("left trigger (old)");

          obj.pressPrimary();
        }
        break;
      case "RT":
        if (xbox.getRightTrigger() > 0.6f) {
          System.out.println("right trigger (??)");
          obj.pressPrimary();
        }
        break;
      case "DU":
        if (xbox.getDPadUp()) {
          obj.pressPrimary();
        }
        break;
      case "DD":
        if (xbox.getDPadDown()) {
          obj.pressPrimary();
        }
        break;
      case "DL":
        if (xbox.getDPadLeft()) {
          obj.pressPrimary();
        }
        break;
      case "DR":
        if (xbox.getDPadRight()) {
          obj.pressPrimary();
        }
        break;
    }

    // shooting
    switch (controllerSecondary) {
      case "X":
        if (xbox.getX()) {
          obj.pressSecondary();
        }
        break;
      case "Y":
        if (xbox.getY()) {
          obj.pressSecondary();
        }
        break;
      case "A":
        if (xbox.getA()) {
          obj.pressSecondary();
        }
        break;
      case "B":
        if (xbox.getB()) {
          obj.pressSecondary();
        }
        break;
      case "LS":
        if (xbox.getLStick()) {
          obj.pressSecondary();
        }
        break;
      case "RS":
        if (xbox.getRStick()) {
          obj.pressSecondary();
        }
        break;
      case "LB":
        if (xbox.getLBumper()) {
          obj.pressSecondary();
        }
        break;
      case "RB":
        if (xbox.getRBumper()) {
          obj.pressSecondary();
        }
        break;
      case "LT":
        if (xbox.getLeftTrigger() > 0.6f) {
          obj.pressSecondary();
        }
        break;
      case "RT":
        if (xbox.getRightTrigger() > 0.6f) {
          obj.pressSecondary();
        }
        break;
      case "DU":
        if (xbox.getDPadUp()) {
          obj.pressSecondary();
        }
        break;
      case "DD":
        if (xbox.getDPadDown()) {
          obj.pressSecondary();
        }
        break;
      case "DL":
        if (xbox.getDPadLeft()) {
          obj.pressSecondary();
        }
        break;
      case "DR":
        if (xbox.getDPadRight()) {
          obj.pressSecondary();
        }
        break;
    }

    // interact
    if (xbox.getB()) {
      obj.pressInteract();
    }
    if (xbox.getX()) {
      obj.pressPause();
    }

    resetPressed = xbox.getY();

    // movement
    float hoff = xbox.getLeftX();
    float voff = -xbox.getLeftY();

    obj.moveHorizontal(hoff);
    obj.moveVertical(voff);
  }

  /**
   * Reads input from the keyboard to determine the actions of the player. Then calls the
   * corresponding methods in the controller to process the actions. Reads from the keyboard
   * regardless of whether an X-Box controller is connected.
   *
   * <p>Change the keyboard keys mapped to primary and secondary actions here.
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
    if (Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.E)) {
      obj.pressInteract();
    }

    // UI controls
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      obj.pressPause();
    }

    obj.moveHorizontal(hoff);
    obj.moveVertical(voff);
  }

  /**
   * Reads input from the mouse to determine the actions of the player. Then calls the corresponding
   * methods in the controller to process the actions. Reads from the mouse regardless of whether an
   * X-Box controller is connected.
   *
   * <p>Updates position in world coordinates.
   *
   * @param obj Controller for the player
   */
  private void readMouse(Controllable obj) {
    dashCoordCache.set(Gdx.input.getX(), Gdx.input.getY());
    viewport.unproject(dashCoordCache);
    obj.updateDirection(dashCoordCache);

    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      obj.pressSecondary();
    }
    if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
      obj.pressTertiary();
    }
  }
}
