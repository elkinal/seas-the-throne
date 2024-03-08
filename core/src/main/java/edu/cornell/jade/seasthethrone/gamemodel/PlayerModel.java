package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.render.PlayerRenderable;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.jade.seasthethrone.util.FilmStrip;
import edu.cornell.jade.seasthethrone.util.Direction;

import java.util.Vector;

/**
 * Model for the main player object of the game. This class extends
 * {@link ComplexModel} to support multiple joints and bodies for flexible
 * collision control and movement display.
 */
public class PlayerModel extends ComplexModel implements PlayerRenderable {
  /** FIXME: stop hardcoding textures */
  /** Frame is player animation */
  private static int FRAMES_IN_ANIMATION = 12;

  /** Player texture when facing up */
  public Texture PLAYER_TEXTURE_UP = new Texture("playerspriterunfilmstrip_up.png");

  /** Player texture when facing down */
  public static final Texture PLAYER_TEXTURE_DOWN = new Texture("playerspriterun_down_clean.png");

  /**
   * Player texture when facing horizontally
   * FIXME: actually add this texture
   */
  public static final Texture PLAYER_TEXTURE_HORI = null;

  /** FilmStrip cache object */
  public FilmStrip filmStrip;

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

  /** The angle direction of this dash in radians */
  private Vector2 dashDirection;

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

    // Set constants
    moveSpeed = 6f;
    dashCounter = 0;
    dashCooldownLimit = 25;
    dashLength = 20;
    isDashing = false;

    // make a triangle for now
    float vertices[] = new float[6];
    vertices[0] = -0.5f;
    vertices[1] = -1;
    vertices[2] = 0.5f;
    vertices[3] = -1;
    vertices[4] = 0;
    vertices[5] = 1;

    PlayerBodyModel playerBody = new PlayerBodyModel(vertices);
    bodies.add(playerBody);

    filmStrip = new FilmStrip(PLAYER_TEXTURE_DOWN, 1, FRAMES_IN_ANIMATION);
  }

  public FilmStrip getFilmStrip() {
    return filmStrip;
  }

  public int getFrameNumber() {
    return animationFrame;
  }

  public void setFrameNumber(int animationFrame) {
    this.animationFrame = animationFrame;
  }

  public int getFramesInAnimation() {
    return FRAMES_IN_ANIMATION;
  }

  public Texture getTextureUp() {
    return PLAYER_TEXTURE_UP;
  }

  public Texture getTextureDown() {
    return PLAYER_TEXTURE_DOWN;
  }

  public Texture getTextureHori() {
    return PLAYER_TEXTURE_HORI;
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
    getBodyModel().setDashing(value);
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

  /** Returns dash direction */
  public Vector2 getDashDirection() {
    return dashDirection;
  }

  /** Sets dash direction */
  public void setDashDirection(Vector2 dir) {
    dashDirection = dir;
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
  /** Returns the player body model */
  public PlayerBodyModel getBodyModel() { return (PlayerBodyModel) bodies.get(0); }

  // built from multiple polygonmodels?
  @Override
  protected boolean createJoints(World world) {

    pointSensorName = "NosePointSensor";

    // Create sensor on the points of the "nose," this should be factored to a diff
    // function later
    Vector2 sensorCenter = new Vector2(0, 1.6f);
    FixtureDef sensorDef = new FixtureDef();
    sensorDef.isSensor = true;
    PolygonShape sensorShape = new PolygonShape();
    sensorShape.setAsBox(0.3f, 1.6f, sensorCenter, 0f);
    sensorDef.shape = sensorShape;

    Fixture sensorFixture = getBodyModel().getBody().createFixture(sensorDef);
    sensorFixture.setUserData(getPointSensorName());

    return true;
  }

  public boolean spearExtended() {
    return isDashing();
  }

  public int frameNumber() {
    return animationFrame;
  }

  public Direction direction() {
    float vx = getVX();
    float vy = getVY();

    // Default to facing right
    if (vx == 0 && vx == vy) {
      return Direction.RIGHT;
    }

    if (Math.abs(vx) > Math.abs(vy)) {
      if (vx > 0)
        return Direction.RIGHT;
      else
        return Direction.LEFT;
    } else {
      if (vy > 0)
        return Direction.UP;
      else
        return Direction.DOWN;
    }
  }

}
