package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class HealthpackModel extends BoxModel implements Interactable, Renderable {

  private TextureRegion unused_texture;

  private TextureRegion used_texture;

  private FilmStrip arrow;

  private float WOLRD_SCALE;

  private boolean used;

  private boolean playerInRange;

  private int frameCounter;

  private int frameDelay;

  /** Range within which the player can interact with this healthpack */
  private final float INTERACT_RANGE = 5f;

  public HealthpackModel(LevelObject obs, float scale) {
    super(obs.x, obs.y, obs.width, obs.height);

    this.unused_texture = new TextureRegion(new Texture("levels/healthpack.png"));
    this.used_texture = new TextureRegion(new Texture("levels/healthpack_empty.png"));
    this.arrow = new FilmStrip(new Texture("levels/interactable_arrow.png"), 1, 20);
    this.used = false;
    this.WOLRD_SCALE = scale;
    this.playerInRange = false;
    this.frameCounter = 0;
    this.frameDelay = 4;

    setBodyType(BodyDef.BodyType.StaticBody);
  }

  /** Checks if the player is close enough to interact with this healthpack */
  public boolean isPlayerInRange(Vector2 playerPos) {
    return Math.abs(Vector2.dst(getX(), getY()+0.8f*getHeight(), playerPos.x, playerPos.y)) < INTERACT_RANGE;
  }

  public void setPlayerInRange(boolean inRange) {
    playerInRange = inRange;
  }

  @Override
  public boolean getPlayerInRange() {
    return playerInRange;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    float y_offset = (WOLRD_SCALE * used_texture.getRegionHeight() - getHeight())/2f;
    if (used) renderer.draw(used_texture, getX(), getY() + y_offset);
    else renderer.draw(unused_texture, getX(), getY() + y_offset);

    if (playerInRange && !used) {
      frameCounter += 1;
      if (frameCounter % frameDelay == 0) {
        progressFrame();
      }

      renderer.draw(arrow, getX(), getY() + y_offset + WOLRD_SCALE*used_texture.getRegionHeight());
    }

  }

  @Override
  public void progressFrame() {
    if (arrow.getFrame() < arrow.getSize()-1)  arrow.setFrame(arrow.getFrame()+1);
    else arrow.setFrame(0);
  }

  @Override
  public void alwaysUpdate() {

  }

  @Override
  public void neverUpdate() {

  }

  @Override
  public void setAlwaysAnimate(boolean animate) {

  }

  @Override
  public boolean alwaysAnimate() {
    return false;
  }
}
