package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.ui.DialogueBox;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TutorialModel extends BoxModel implements Interactable, Renderable {
  /** Whether the player is within range to automatically draw tutorial box */
  private boolean playerInRange;

  /** This tutorial's dialogue */
  private DialogueBox dialogue;

  /** Range within which the player can interact with this model */
  private final float INTERACT_RANGE = 7f;

  /**
   * Creates a new TutorialModel which consists of an invisible NPC which triggers a tutorial
   * dialogue box when the player is within range.
   *
   * @param tutorial the LevelObject representing the tutorial
   */
  public TutorialModel(LevelObject tutorial) {
    super(tutorial.x, tutorial.y, tutorial.width, tutorial.height);
    this.playerInRange = false;

    this.dialogue = new DialogueBox();
    dialogue.setTexts(parseTutorialDialogue(tutorial.tutID));
  }

  @Override
  public void draw(RenderingEngine renderer) {}

  @Override
  public void progressFrame() {}

  /**
   * Retrieves correct tutorial dialogue for specified box int modelNum the number dialogue box (for
   * 1st one player sees, 4 for last) *
   */
  private String parseTutorialDialogue(int modelNum) {
    Path path = Path.of("../assets/tutorial/tutorial" + modelNum + ".txt");
    try {
      String currentPath = new java.io.File(".").getCanonicalPath();
      return Files.readString(path, StandardCharsets.UTF_8);
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public DialogueBox getDialogueBox() {
    return dialogue;
  }

  @Override
  public void alwaysUpdate() {}

  @Override
  public void neverUpdate() {}

  @Override
  public void setAlwaysAnimate(boolean animate) {}

  @Override
  public boolean alwaysAnimate() {
    return false;
  }

  @Override
  public boolean isPlayerInRange(Vector2 playerPos) {
    return Math.abs(Vector2.dst(getX(), getY(), playerPos.x, playerPos.y)) < INTERACT_RANGE;
  }

  @Override
  public void setPlayerInRange(boolean inRange) {
    playerInRange = inRange;
  }

  @Override
  public boolean getPlayerInRange() {
    return playerInRange;
  }
}
