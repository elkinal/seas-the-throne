package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.input.Controllable;

public class DialogueBoxController implements Controllable {

  private DialogueBox dialogueBox;
  private boolean toggle;

  /** If the dialogue box is displayed and the controller should be reading input */
  private boolean active;

  public DialogueBoxController(DialogueBox dialogueBox) {
    this();
    this.dialogueBox = dialogueBox;
  }

  public DialogueBoxController() {
    toggle = false;
    active = false;
  }

  public void setActive(boolean active) {this.active = active;}

  public boolean isActive() {return active;}

  /** Sets the dialogue box */
  public void setDialogueBox(DialogueBox dialogueBox) {
    this.dialogueBox = dialogueBox;
  }

  /** Returns the dialogue box */
  public DialogueBox getDialogueBox() {
    return dialogueBox;
  }

  /** Hides the dialogue box */
  @Override
  public void pressPrimary() {
    if (!active) return;

    dialogueBox.hide();
  }

  @Override
  public void pressPause() {
    if (!active) return;

    dialogueBox.hide();
  }

  /** Selects between dialogue box pages */
  @Override
  public void moveHorizontal(float movement) {
    if (!active) return;

    if (movement > 0 && !toggle) {
      dialogueBox.cycleLeft();
      toggle = true;
    }
    if (movement < 0 && !toggle) {
      dialogueBox.cycleRight();
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
