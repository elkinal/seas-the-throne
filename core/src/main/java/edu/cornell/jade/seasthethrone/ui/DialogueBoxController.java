package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.input.Controllable;

public class DialogueBoxController implements Controllable {

    private DialogueBox dialogueBox;

    private GameplayController gameplayController;

    public DialogueBoxController(DialogueBox dialogueBox) {
        this.dialogueBox = dialogueBox;
    }

    /** Sets the dialogue box */
    public void setDialogueBox(DialogueBox dialogueBox) {
        this.dialogueBox = dialogueBox;
    }

    /** Returns the dialogue box */
    public DialogueBox getDialogueBox() {
        return dialogueBox;
    }

    public void setGameplayController(GameplayController gameplayController) {
        this.gameplayController = gameplayController;
    }

    /** Hides the dialogue box */
    @Override
    public void pressInteract() {
        dialogueBox.setText("");
    }

    @Override
    public void pressPause() {}


    @Override
    public void moveVertical(float movement) {}

    @Override
    public Vector2 getLocation() {
        return null;
    }
}
