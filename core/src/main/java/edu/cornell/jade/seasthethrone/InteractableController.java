package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.CheckpointModel;
import edu.cornell.jade.seasthethrone.gamemodel.HealthpackModel;
import edu.cornell.jade.seasthethrone.gamemodel.Interactable;
import edu.cornell.jade.seasthethrone.input.Controllable;

public class InteractableController implements Controllable {

  /** Array of interactables managed by this controller */
  private Array<Interactable> interactables;

  /** If the interact button has been pressed */
  private boolean interactPressed;

  /** Checkpoint ID of the last interacted checkpoint */
  private int checkpointID;

  /** Flag for gameplayController indicating that a checkpoint has just been set */
  private boolean checkpointActivated;

  /** PlayerController updated by certain interactables (health packs) */
  private PlayerController player;

  public InteractableController() {
    this.interactables = new Array<>();
    this.interactPressed = false;
    checkpointActivated = false;
    checkpointID = -1;
  }

  public void setPlayerController(PlayerController player) {
    this.player = player;
  }

  public void add(Interactable interactable) {
    this.interactables.add(interactable);
  }

  /** Checks if interact was pressed this frame. If so, interacts with all interactables in range */
  public void update() {
    this.checkpointActivated = false;
    for (Interactable interactable : interactables) {
      // Check if player is in range
      interactable.setPlayerInRange(interactable.isPlayerInRange(player.getShadowLocation()));

      if (interactable instanceof CheckpointModel) ((CheckpointModel) interactable).setActivated(false);
    }

    if (!interactPressed) {
      return;
    }

    // If interact pressed
    for (Interactable interactable : interactables) {
      // interact with healthpacks
      if (interactable instanceof HealthpackModel) {
        if (canUseHealthpack((HealthpackModel) interactable)) {
          if (BuildConfig.DEBUG) System.out.println("Health restored!");
          player.setHealth(5);
          ((HealthpackModel) interactable).setUsed(true);
        }

        // interact with checkpoints
      } else if (interactable instanceof CheckpointModel) {
        if (interactable.getPlayerInRange() && !checkpointActivated) {
           player.setHealth(5);
          ((CheckpointModel) interactable).setActivated(true);
          this.checkpointID = ((CheckpointModel) interactable).getCheckpointID();
          this.checkpointActivated = true;
        }
      }
    }
    interactPressed = false;
  }

  private boolean canUseHealthpack(HealthpackModel model) {
    return model.getPlayerInRange() && !model.isUsed() && player.getHealth() < 5;
  }

  public boolean isCheckpointActivated() {
    return checkpointActivated;
  }

  public int getCheckpointID() {
    return checkpointID;
  }

  @Override
  public void pressInteract() {
    interactPressed = true;
  }


  /*
  * ===============================
  * Unused stub methods
  * ===============================
  * */

  @Override
  public void moveHorizontal(float movement) {
    Controllable.super.moveHorizontal(movement);
  }

  @Override
  public void moveVertical(float movement) {
    Controllable.super.moveVertical(movement);
  }

  @Override
  public void pressPrimary() {
    Controllable.super.pressPrimary();
  }

  @Override
  public void pressSecondary() {
    Controllable.super.pressSecondary();
  }

  @Override
  public void toggleDashMode() {
    Controllable.super.toggleDashMode();
  }

  @Override
  public void updateDirection(Vector2 mousePos) {
    Controllable.super.updateDirection(mousePos);
  }

  @Override
  public void pressPause() {
    Controllable.super.pressPause();
  }

  @Override
  public Vector2 getLocation() {
    return null;
  }
}
