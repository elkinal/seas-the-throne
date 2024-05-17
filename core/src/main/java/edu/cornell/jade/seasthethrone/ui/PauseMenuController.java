package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.input.Controllable;

public class PauseMenuController implements Controllable {

  private PauseMenu pauseMenu;
  private boolean toggle;
  private final int PRESS_DELAY = 10;

  /** Delay for player selecting an option on the pause menu */
  private int pressTimer;

  /** Delay for player pausing the game */
  private int pressDisplayTimer;

  private GameplayController gameplayController;

  public PauseMenuController(PauseMenu pauseMenu) {
    this.pauseMenu = pauseMenu;
    pressTimer = 0;
    pressDisplayTimer = 0;
  }

  /** Sets the pause menu */
  public void setPauseMenu(PauseMenu pauseMenu) {
    this.pauseMenu = pauseMenu;
  }

  /** Returns the pause menu */
  public PauseMenu getPauseMenu() {
    return pauseMenu;
  }

  public void setGameplayController(GameplayController gameplayController) {
    this.gameplayController = gameplayController;
  }

  /** Shows / hides the pause menu */
  @Override
  public void pressPause() {
    if (pressDisplayTimer == 0) {
      pauseMenu.setPaused(!pauseMenu.isPaused());
    }
    if (pressDisplayTimer >= PRESS_DELAY) pressDisplayTimer = 0;
    else {
      pressDisplayTimer++;
    }
  }

  /** Clicks on a pause menu item */
  @Override
  public void pressInteract() {
    if (!pauseMenu.isPaused()) return;
    if (pressTimer == 0) {
      switch (pauseMenu.getSelection()) {
        case RESUME -> pauseMenu.setPaused(false);
        case RESTART -> restart();
        case OPTIONS -> options();
        case LEVEL_SELECT -> levelSelect();
        case QUIT -> gameplayController.setQuit(true);
      }
    }
    if (pressTimer >= PRESS_DELAY) pressTimer = 0;
    else pressTimer++;
  }

  private void restart() {
    gameplayController.setRestart(true);
    pauseMenu.setPaused(false);
  }

  private void levelSelect() {
    gameplayController.setReturnToHub(true);
    pauseMenu.setPaused(false);
  }

  private void options() {
    gameplayController.setOptions(true);
  }
  
  /** Selects between menu options */
  @Override
  public void moveVertical(float movement) {
    if (movement > 0 && !toggle) {
      pauseMenu.cycleUp();
      toggle = true;
    }
    if (movement < 0 && !toggle) {
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
