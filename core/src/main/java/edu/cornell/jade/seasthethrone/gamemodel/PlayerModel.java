package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.render.PlayerRenderable;

/**
 * Model for the main player object of the game. This class extends
 * {@link ComplexModel} to support multiple joints and bodies for flexible
 * collision control and movement display.
 */
public class PlayerModel extends ComplexModel implements PlayerRenderable {
  /** FIXME: stop hardcoding this */
  private static int FRAMES_IN_FISH_ANIMATION = 6;
  /** current animation frame */
  private int animationFrame;

  /** Whether the player is dashing */
  private boolean isDashing;
  /**
   * Frame counter for dashing. Tracks how long the player has been dashing for
   * and how long until
   * they can dash again.
   */
  private int dashCounter;
  /** The time limit (in frames) between dashes */
  private int dashCooldownLimit;

  /** The number of frames a dash lasts */
  private int dashLength;

  /** Unique identifier for the point sensor; used in collision handling */
  private String pointSensorName;

  /** Scaling factor for player movement */
  private float moveSpeed;

  /**
   * {@link PlayerModel} constructor using an x and y coordinate.
   *
   * @param x The x-position for this player in world coordinates
   * @param y The y-position for this player in world coordinates
   */
  public PlayerModel(float x, float y) {
    super(x, y);

    // make a triangle for now
    float vertices[] = new float[6];
    vertices[0] = -0.5f;
    vertices[1] = -1;
    vertices[2] = 0.5f;
    vertices[3] = -1;
    vertices[4] = 0;
    vertices[5] = 1;

    // Set constants
    moveSpeed = 6f;
    dashCounter = 0;
    dashCooldownLimit = 25;
    dashLength = 20;
    isDashing = false;

    PolygonModel nose = new PolygonModel(vertices);
    nose.setName("nose");
    nose.setBodyType(BodyDef.BodyType.DynamicBody);
    bodies.add(nose);
  }

  /**
   * Returns player's move speed.
   *
   *
   * @return player's move speed.
   */
  public float getMoveSpeed() {
    return moveSpeed;
  }

  /**
   * Sets player's move speed.
   *
   * @param speed new value for move speed.
   */
  public void setMoveSpeed(float speed) {
    moveSpeed = speed;
  }

  /**
   * Returns true if the player is dashing.
   *
   * @return true if the player is dashing.
   */
  public boolean isDashing() {
    return isDashing;
  }

  /**
   * Sets if the player is dashing
   *
   * @param value is the player dashing
   */
  public void setDashing(boolean value) {
    isDashing = value;
  }

  /** Returns if the player can dash */
  public boolean canDash() {
    return !isDashing && dashCounter == 0;
  }

  /** Sets value for dash cooldown */
  public void setDashCounter(int value) {
    dashCounter = value;
  }

  /** Returns current value of dash cooldown */
  public int getDashCounter() {
    return dashCounter;
  }

  /** Returns dash limit */
  public int getDashCooldownLimit() {
    return dashCooldownLimit;
  }

  /** Decriments dash cooldown */
  public void decrementDashCounter() {
    dashCounter -= 1;
  }

  /** Returns length of dash in frames */
  public int getDashLength() {
    return dashLength;
  }

  /**
   * Returns the name of the nose point sensor
   *
   * This is used by ContactListener
   *
   * @return the name of the nose point sensor
   */
  public String getPointSensorName() {
    return pointSensorName;
  }

  /** Returns the model of the "nose", hard-coded in for now */
  public Model getPointModel() { return bodies.get(0); }

  // built from multiple polygonmodels?
  @Override
  protected boolean createJoints(World world) {

    pointSensorName = "NosePointSensor";

    // Create sensor on the points of the "nose," this should be factored to a diff
    // function later
    Vector2 sensorCenter = new Vector2(0, 1);
    FixtureDef sensorDef = new FixtureDef();
    sensorDef.isSensor = true;
    CircleShape sensorShape = new CircleShape();
    // sensor has 0 radius, maybe modify?
    sensorShape.setRadius(0.4f);
    sensorShape.setPosition(sensorCenter);
    sensorDef.shape = sensorShape;

    Fixture sensorFixture = getPointModel().getBody().createFixture(sensorDef);
    sensorFixture.setUserData(getPointSensorName());

    return true;
  }

  public boolean spearExtended() {
    return isDashing();
  }

  public int frameNumber() {
    return animationFrame;
  }

  public void updateAnimationFrame() {
    animationFrame = (animationFrame + 1) % FRAMES_IN_FISH_ANIMATION;
  }

  public int direction() {
    float vx = getVX();
    float vy = getVY();

    if (vx == 0 && vx == vy ) {
      return -1;
    }

    if (Math.abs(vx) > Math.abs(vy)) {
      if (vx > 0)
        // Right
        return 3;
      else
        // Left
        return 2;
    } else if (Math.abs(vy) > Math.abs(vx)){
      if (vy > 0)
        // Up
        return 0;
      else
        // Down
        return 1;
    } else {
      if (vx > 0) {
        if (vy > 0) {
          // North east
          return 4;
        } else {
          // South east
          return 5;
        }
      } else {
        if (vy > 0) {
          // North west
          return 7;
        } else {
          // South west
          return 6;
        }
      }
    }
  }

}
