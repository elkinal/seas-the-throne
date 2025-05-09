package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.ui.DialogueBox;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class NpcModel extends BoxModel implements Interactable, Renderable {

  private boolean playerInRange;

  /** Texture for floating interact arrow */
  private FilmStrip arrow;

  /** Filmstrip for NPC */
  private FilmStrip filmStrip;

  private final float WORLD_SCALE;

  private int frameDelay;

  private int arrowFrameDelay;

  private int frameCounter;

  /** This NPC's dialogue */
  private DialogueBox dialogue;

  /** Range within which the player can interact with this checkpoint */
  private final float INTERACT_RANGE = 7f;

  public NpcModel(LevelObject obj, float scale) {
    super(obj.x, obj.y, obj.width, obj.height);
    this.WORLD_SCALE = scale;
    this.playerInRange = false;
    this.frameCounter = 0;
    this.frameDelay = 45;
    this.arrowFrameDelay = 4;

    this.filmStrip = new FilmStrip(obj.texture.getTexture(), 1, 2);
    this.arrow = new FilmStrip(new Texture("levels/interactable_arrow.png"), 1, 20);
    CollisionMask.setCategoryMaskBits(this);

    this.dialogue = new DialogueBox();
    dialogue.setTexts(parseDialogue(obj.dialogue));
  }

  @Override
  public void draw(RenderingEngine renderer) {
    float y_offset = WORLD_SCALE * filmStrip.getRegionHeight() / 2f - getHeight() / 2f;

    // draw character
    renderer.draw(filmStrip, getX(), getY() + y_offset);

    // draw arrow
    if (playerInRange) {
      renderer.draw(
          arrow, getX(), getY() + y_offset + WORLD_SCALE * filmStrip.getRegionHeight());
    }

    progressFrame();
    frameCounter += 1;
  }

  public void progressFrame() {
    if (frameCounter % arrowFrameDelay == 0 && playerInRange) {
      if (arrow.getFrame() < arrow.getSize() - 1) arrow.setFrame(arrow.getFrame() + 1);
      else arrow.setFrame(0);
    }

    if (frameCounter % frameDelay == 0) {
      if (filmStrip.getFrame() < filmStrip.getSize() - 1)
        filmStrip.setFrame(filmStrip.getFrame() + 1);
      else filmStrip.setFrame(0);
    }
  }

  /** Reads this characters dialogue string from the specified file */
  private String parseDialogue(String file) {
    Path path = Path.of("../app/assets/"+file);
    try {
      String currentPath = new java.io.File(".").getCanonicalPath();
      System.out.println("Current dir:" + currentPath);
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
