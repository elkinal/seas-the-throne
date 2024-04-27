package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.input.Controllable;

public class PauseMenuController implements Controllable {

    private PauseMenu pauseMenu;
    private boolean toggle;

    private GameplayController gameplayController;

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

    public void setGameplayController(GameplayController gameplayController) {
        this.gameplayController = gameplayController;
    }

    /** Shows / hides the pause menu */
    @Override
    public void pressPause() {
        pauseMenu.setPaused(!pauseMenu.isPaused());
    }

    /** Clicks on a pause menu item*/
    @Override
    public void pressInteract() {
            //TODO: create actions
            switch (pauseMenu.getSelection()) {
                case RESUME -> pauseMenu.setPaused(false);
                case RESTART -> gameplayController.setRestart(true);
                case HELP -> UIController.dialogueBox.setTexts("hello");
                case QUIT -> System.exit(0);
            }
    }

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