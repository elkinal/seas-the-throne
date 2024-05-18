package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.audio.SoundPlayer;
import edu.cornell.jade.seasthethrone.input.Controllable;
import edu.cornell.jade.seasthethrone.util.Controllers;

public class PauseMenuController implements Controllable {

  private PauseMenu pauseMenu;

  private SoundPlayer soundPlayer;
  private boolean toggle;
  private final int PRESS_DELAY = 7;

  /** Delay for player selecting an option on the pause menu */
  private int pressTimer;

  /** Delay for player pausing the game (CONTROLLER ONLY) */
  private int pressDisplayTimer;

  private GameplayController gameplayController;

  public PauseMenuController(PauseMenu pauseMenu, SoundPlayer soundPlayer) {
    this.pauseMenu = pauseMenu;
    this.soundPlayer = soundPlayer;
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
    if (Controllers.get().getXBoxControllers().isEmpty()) {
      pauseMenu.setPaused(!pauseMenu.isPaused());
    } else {
      if (pressDisplayTimer == 0) {
        pauseMenu.setPaused(!pauseMenu.isPaused());
      }
      if (pressDisplayTimer >= PRESS_DELAY) pressDisplayTimer = 0;
      else pressDisplayTimer++;
    }
  }

  /** Clicks on a pause menu item */
  @Override
  public void pressInteract() {
    if (!pauseMenu.isPaused()) return;
    if (pressTimer == 0) {
      switch (pauseMenu.getSelection()) {
        case RESUME -> resume();
        case RESTART -> restart();
        case OPTIONS -> options();
        case LEVEL_SELECT -> levelSelect();
        case QUIT -> quit();
      }
    }
    if (pressTimer >= PRESS_DELAY) {
      pressTimer = 0;
    } else pressTimer++;
  }

  private void resume() {
    soundPlayer.playSoundEffect("menu-select");
    pauseMenu.setPaused(false);
  }

  private void restart() {
    soundPlayer.playSoundEffect("menu-select");
    gameplayController.setRestart(true);
    pauseMenu.setPaused(false);
  }

  private void levelSelect() {
    soundPlayer.playSoundEffect("menu-select");
    gameplayController.setReturnToHub(true);
    pauseMenu.setPaused(false);
  }

  private void options() {
    soundPlayer.playSoundEffect("menu-select");
    gameplayController.setOptions(true);
  }

  private void quit() {
    soundPlayer.playSoundEffect("menu-select");
    gameplayController.setQuit(true);
    pauseMenu.setPaused(false);
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
