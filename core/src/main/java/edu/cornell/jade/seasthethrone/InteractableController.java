package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.CheckpointModel;
import edu.cornell.jade.seasthethrone.gamemodel.HealthpackModel;
import edu.cornell.jade.seasthethrone.gamemodel.Interactable;
import edu.cornell.jade.seasthethrone.input.Controllable;

public class InteractableController implements Controllable {
  private Array<Interactable> interactables;

  private boolean interactPressed;

  private int checkpointID;

  private boolean checkpointActivated;

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

  public void update() {
    this.checkpointActivated = false;

    if (!interactPressed) {
      return;
    }

    for (Interactable interactable : interactables) {
      // interact with healthpacks
      if (interactable instanceof HealthpackModel) {
        if (interactable.playerInRange(player.getShadowLocation())) {
          if (BuildConfig.DEBUG) System.out.println("Health restored!");
          player.setHealth(5);

          // Trying to remove healthpack after use is super buggy
//          ((HealthpackModel) interactable).markRemoved(true);
        }

        // interact with checkpoints
      } else if (interactable instanceof CheckpointModel) {
        if (interactable.playerInRange(player.getShadowLocation())) {
          if (BuildConfig.DEBUG) {
            System.out.println("Checkpoint "+((CheckpointModel) interactable).getCheckpointID()+" activated");
          }

          // player.setHealth(5);
          ((CheckpointModel) interactable).setActivated(true);
          this.checkpointID = ((CheckpointModel) interactable).getCheckpointID();
          this.checkpointActivated = true;

        }
      }
    }
    interactPressed = false;
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
