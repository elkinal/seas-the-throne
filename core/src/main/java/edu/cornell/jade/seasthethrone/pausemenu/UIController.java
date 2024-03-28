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

    public UIController(PauseMenu pauseMenu) {
        this.pauseMenu = pauseMenu;
    }

    /** Sets the pause menu */
    public void setPauseMenu(PauseMenu pauseMenu) {
        this.pauseMenu = pauseMenu;
    }

    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    @Override
    public void pressPaused() {
        pauseMenu.setPaused(!pauseMenu.isPaused());
    }

    @Override
    public void pressUp() {
        pauseMenu.cycleUp();
    }

    @Override
    public void pressDown() {
        pauseMenu.cycleDown();
    }
}
