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

//    public void pause() {
//        pauseMenu.setPaused(true);
//    }
//
//    public void unPause() {
//        pauseMenu.setPaused(false);
//    }
//
//    public boolean isPaused() {
//        return getPauseMenu() != null && getPauseMenu().isPaused();
//    }


    @Override
    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    @Override
    public void switchPaused() {
        pauseMenu.setPaused(!pauseMenu.isPaused());
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
