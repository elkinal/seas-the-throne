package edu.cornell.jade.seasthethrone.gamemodel.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/**
 * Model for the player spear. When the spear is extended, it will have an active hitbox that will
 * allow the spear to pierce through enemies.
 */
public class PlayerSpearModel extends BoxModel implements Renderable {
  /** Width of spear */
  private static float SPEAR_WIDTH = 1.5f;

  /** Length of spear */
  private static float SPEAR_LENGTH = 3.2f;

  /** Offset length of the spear center from the body center (when extended) */
  private static float SPEAR_OFFSET = 2.5f;

  /** Maximum number of fish that can be speared */
  private static int MAX_SPEAR_CAPACITY = 5;

  private PlayerBodyModel mainBody;

  /** If the spear is extended (during dash) */
  private boolean spearExtended;

  /** Number of fish currently speared */
  private int numSpeared;

  /** Cache for the direction the spear is facing */
  private Vector2 spearDirectionCache;

  /** Cache for the direction the of the shoot indicator */
  private Vector2 indicatorCache;

  private final TextureRegion SPEAR_TEXTURE_REGION;

  /** Amount of damage the spear inflicts */
  private int damage;

  /** If the spear should be set to inactive in the next iteration loop */
  private boolean flagInactive;

  /**
   * The size is expressed in physics units NOT pixels.
   *
   * @param x Initial x position of the spear center
   * @param y Initial y position of the spear center
   * @param width spear width in physics units
   * @param height spear width in physics units
   */
  public PlayerSpearModel(float x, float y, float width, float height, Texture texture) {
    super(x, y, width, height);
    spearExtended = false;
    spearDirectionCache = new Vector2();
    indicatorCache = new Vector2();
    SPEAR_TEXTURE_REGION = new TextureRegion(texture);
    damage = 5;
    flagInactive = false;
  }

  /** Create new player body at position (x,y) */
  public PlayerSpearModel(float x, float y, Texture texture, PlayerBodyModel mainBody) {
    this(x, y, SPEAR_WIDTH, SPEAR_LENGTH, texture);
    this.mainBody = mainBody;
  }

  /** Check if spear is extended */
  public boolean isSpearExtended() {
    return spearExtended;
  }

  /** Returns the spear damage number */
  public int getDamage() {
    return damage;
  }

  /** Returns main body attached to the spear */
  public PlayerBodyModel getMainBody() {
    return mainBody;
  }

  /**
   * Extend or retract spear, activating or deactivating the spear hitbox
   *
   * @param value If the spear should be extended
   */
  public void setSpear(boolean value) {
    setActive(value);
    spearExtended = value;
  }

  /**
   * Update the spear mode
   *
   * @param dashDirection Vector representing the direction of the dash
   */
  public void updateSpear(Vector2 dashDirection) {
    spearDirectionCache.set(dashDirection);
    setPosition(mainBody.getPosition().add(spearDirectionCache.scl(SPEAR_OFFSET)));
    setAngle(spearDirectionCache.angleRad() + (float) Math.PI / 2);
  }

  /**
   * Update the dash indicator vector
   *
   * @param dashDirection Vector representing the direction of the dash
   */
  public void updateDashIndicator(Vector2 dashDirection) {
    indicatorCache.set(dashDirection);
  }

  public int getNumSpeared() {
    return numSpeared;
  }

  public void setNumSpeared(int num) {numSpeared = num;}

  /**
   * If possible, increment spear counter
   */
  public void incrementSpear() {
    numSpeared = Math.min(numSpeared + 1, MAX_SPEAR_CAPACITY);
  }

  /**
   * Decrement spear counter.
   *
   * @pre spear counter is above 0
   */
  public void decrementSpear() {
    numSpeared -= 1;
  }
  /** Mark the spear as inactive in the next frame */
  public void markInactive() {
    flagInactive = true;
  }


  /** Overriding the current activatePhysics method to start the body off as inactive */
  @Override
  public boolean activatePhysics(World world) {
    // Make a body, if possible
    bodyinfo.active = false;
    body = world.createBody(bodyinfo);
    body.setUserData(this);

    // Only initialize if a body was created.
    if (body != null) {
      createFixtures();
      return true;
    }

    bodyinfo.active = false;
    return false;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    float angle = indicatorCache.angleRad();
    float mag = 3f + SPEAR_OFFSET;

    renderer.draw(
        SPEAR_TEXTURE_REGION,
        getMainBody().getX() + (float) (mag * Math.cos(angle)),
        getMainBody().getY() + (float) (mag * Math.sin(angle)));
  }

  /**
   * Updates the object's physics state (NOT GAME LOGIC).
   *
   * <p>Use this for cooldown checking/resetting.
   */
  @Override
  public void update(float delta) {
    if (flagInactive) {
      setSpear(false);
      flagInactive = false;
    }
  }

  @Override
  public FilmStrip progressFrame() {
    return null;
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

  public int getFrameNumber() {
    return 0;
  }

  public void setFrameNumber(int frameNumber) {}

  public FilmStrip getFilmStrip() {
    return null;
  }

  public int getFramesInAnimation() {
    return 0;
  }
}
