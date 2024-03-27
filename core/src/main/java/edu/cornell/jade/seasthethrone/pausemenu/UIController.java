package edu.cornell.jade.seasthethrone.pausemenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.input.Controllable;
import edu.cornell.jade.seasthethrone.util.Controllers;
import edu.cornell.jade.seasthethrone.util.XBoxController;

public class UIController implements Controllable {

    private PauseMenu pauseMenu;

    /** Viewport to unproject screen coordinates */
    Viewport viewport;

    /** XBox Controller support */
    private XBoxController xbox;


    /**
     * Cache vector to return containing the dash coordinates of the previous read
     */
    Vector2 dashCoordCache;


    /**
     * Constructor for UIController with a parameter to convert between screens
     * and the world.
     *
     * @param screenToWorld viewport to support an unproject operation to convert
     *                      screen coord to
     *                      mouse coord.
     */
    public UIController(Viewport screenToWorld) {
        viewport = screenToWorld;

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
        for (Controllable p : players) {
            readInput(p);
        }
    }


    /** Reads the input for the player and converts the result into game logic. */
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
     * the actions of the
     * player. Then calls the corresponding methods in the controller to process the
     * actions.
     * <p>
     * @param obj Controller for the player
     */
    private void readController(Controllable obj) {

        //TODO: Requires testing with a controller
        if (xbox.getDPadUp()) {
            pauseMenu.cycleUp();
        }
        if (xbox.getDPadDown()) {
            pauseMenu.cycleDown();
        }

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
        // Pause menu inputs
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pauseMenu.setPaused(!pauseMenu.isPaused());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            pauseMenu.cycleUp();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            pauseMenu.cycleDown();
        }
    }

    public void setPauseMenu(PauseMenu pauseMenu) {
        this.pauseMenu = pauseMenu;
    }

    @Override
    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    @Override
    public void cycleUp() {
        pauseMenu.cycleUp();
    }

    @Override
    public void cycleDown() {
        pauseMenu.cycleDown();
    }
}
