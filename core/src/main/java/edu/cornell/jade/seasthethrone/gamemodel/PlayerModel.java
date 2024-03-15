package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.render.PlayerRenderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/**
 * Model for the main player object of the game. This class extends {@link ComplexModel} to support
 * multiple joints and bodies for flexible collision control and movement display.
 */
public class PlayerModel extends ComplexModel implements PlayerRenderable {
  /** FIXME: stop hardcoding textures */
  /** Frame is player animation */
  private static int FRAMES_IN_ANIMATION = 12;
  private static int FRAMES_IN_ANIMATION_DASH = 5;

  /** Player texture when facing up */
  public Texture PLAYER_TEXTURE_UP = new Texture("playerspriterun_up_wspear.png");

  /** Player texture when facing down */
  public static final Texture PLAYER_TEXTURE_DOWN = new Texture("playerspriterun_down_wspear.png");

  /** Player texture when facing left */
  public static final Texture PLAYER_TEXTURE_LEFT = new Texture("playerspriterun_left_wspear.png");

  /** Player texture when facing right */
  public static final Texture PLAYER_TEXTURE_RIGHT = new Texture("playerspriterun_right_wspear.png");
  /** Player texture when dashing up */
  public Texture PLAYER_TEXTURE_UP_DASH = new Texture("playerspritedashfilmstrip_up.png");

  /** Player texture when dashing down */
  public static final Texture PLAYER_TEXTURE_DOWN_DASH = new Texture("playerspritedashfilmstrip_down.png");

  /** Player texture when dashing left */
  public static final Texture PLAYER_TEXTURE_LEFT_DASH = new Texture("playerspritedashfilmstrip_left.png");

  /** Player texture when dashing right */
  public static final Texture PLAYER_TEXTURE_RIGHT_DASH = new Texture("playerspritedashfilmstrip_right.png");


  /** FilmStrip cache object */
  public FilmStrip filmStrip;

  /** current animation frame */
  private int animationFrame;

  /** The number of frames to skip before animating the next player frame */
  private int frameDelay;

  /**
   * Counter for the number of frames that have been drawn to the screen
   * This is separate from the position in the player filmstrip.
   *  */
  private int frameCounter;

  /** current direction the player is facing */
  private Direction faceDirection;


  /** Whether the player is dashing */
  private boolean isDashing;

  /**
   * Frame counter for dashing. Tracks how long the player has been dashing for and how long until
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

    moveSpeed = 8f;
    faceDirection = Direction.DOWN;
    dashCounter = 0;
    dashCooldownLimit = 25;
    dashLength = 20;
    isDashing = false;
    frameCounter = 0;
    frameDelay = 3;

    PlayerBodyModel playerBody = new PlayerBodyModel(x, y);
    bodies.add(playerBody);

    PlayerSpearModel playerSpear = new PlayerSpearModel(x, y);
    bodies.add(playerSpear);

    filmStrip = new FilmStrip(PLAYER_TEXTURE_DOWN, 1, FRAMES_IN_ANIMATION);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    PlayerRenderable.super.draw(renderer);

    // Only move to next frame of animation every frameDelay number of frames
    if (frameCounter % frameDelay == 0) {
      setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
    }
    frameCounter += 1;
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
    if (isDashing)
      return FRAMES_IN_ANIMATION_DASH;
    else
      return FRAMES_IN_ANIMATION;
  }

  public Texture getTextureUp() {
    return PLAYER_TEXTURE_UP;
  }

  public Texture getTextureDown() {
    return PLAYER_TEXTURE_DOWN;
  }

  public Texture getTextureLeft() {
    return PLAYER_TEXTURE_LEFT;
  }

  public Texture getTextureRight() {
    return PLAYER_TEXTURE_RIGHT;
  }

  public Texture getTextureUpDash(){
    return PLAYER_TEXTURE_UP_DASH;
  }
  public Texture getTextureDownDash(){
    return PLAYER_TEXTURE_DOWN_DASH;
  }
  public Texture getTextureLeftDash(){
    return PLAYER_TEXTURE_LEFT_DASH;
  }
  public Texture getTextureRightDash(){
    return PLAYER_TEXTURE_RIGHT_DASH;
  }
  /**
   * Returns player's move speed.
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
   * Sets the player to dashing, if possible
   * If not possible, will return false.
   */

  public boolean checkAndSetDashing() {
    if(dashCounter == 0){
      isDashing = true;
      getSpearModel().setSpear(true);
      return true;
    } return false;
  }

  /** Set dashing to false */
  public void stopDashing(){
    isDashing = false;
    getSpearModel().setSpear(false);
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
   * <p>This is used by ContactListener
   *
   * @return the name of the nose point sensor
   */
  public String getPointSensorName() {
    return pointSensorName;
  }

  /** Returns the player body model */
  public PlayerBodyModel getBodyModel() {
    return (PlayerBodyModel) bodies.get(0);
  }

  /** Returns the player spear model */
  public PlayerSpearModel getSpearModel(){
    return (PlayerSpearModel) bodies.get(1);
  }

  /** Update the player's spear model when dashing */
  public void updateSpear(Vector2 dashDirection){
    getSpearModel().updateSpear(getPosition(), dashDirection);
  }

  @Override
  protected boolean createJoints(World world) {
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

    if (Math.abs(vx) > Math.abs(vy)) {
      if (vx > 0) faceDirection = Direction.RIGHT;
      else faceDirection = Direction.LEFT;
    } else if (Math.abs(vx) < Math.abs(vy)){
      if (vy > 0) faceDirection = Direction.UP;
      else faceDirection = Direction.DOWN;
    }
    return faceDirection;
  }
}
