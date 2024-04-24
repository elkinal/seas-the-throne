package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.input.Controllable;

public class PauseMenuController implements Controllable {

    private PauseMenu pauseMenu;
    private boolean toggle;

    public PauseMenuController(PauseMenu pauseMenu) {
        this.pauseMenu = pauseMenu;
    }

    /** Sets the pause menu */
    public void setPauseMenu(PauseMenu pauseMenu) {
        this.pauseMenu = pauseMenu;
    }

    /** Returns the pause menu*/
    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    /** Shows / hides the pause menu */
    @Override
    public void pressPause() {
        pauseMenu.setPaused(!pauseMenu.isPaused());
    }

    /** Clicks on a pause menu item*/
    @Override
    public void pressPrimary() { pauseMenu.clicked(); }

    /** Selects between menu options */
    @Override
    public void moveVertical(float movement) {
        if (movement > 0 && !toggle) {
            pauseMenu.cycleUp();
            toggle = true;
        }
        if (movement < 0 && !toggle){
            pauseMenu.cycleDown();
            toggle = true;
        }
        if (movement == 0) {
            toggle = false;
        }
    }

    @Override
    public Vector2 getLocation() {
        return null;
    }
}