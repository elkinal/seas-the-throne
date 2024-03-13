package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.render.PlayerRenderable;
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

  /** Player texture when facing up */
  public Texture PLAYER_TEXTURE_UP = new Texture("playerspriterunfilmstrip_up.png");

  /** Player texture when facing down */
  public static final Texture PLAYER_TEXTURE_DOWN = new Texture("playerspriterun_down_clean.png");

  /** Player texture when facing horizontally FIXME: actually add this texture */
  public static final Texture PLAYER_TEXTURE_HORI = null;

  /** FilmStrip cache object */
  public FilmStrip filmStrip;

  /** current animation frame */
  private int animationFrame;

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
    faceDirection = Direction.RIGHT;
    moveSpeed = 6f;
    dashCounter = 0;
    dashCooldownLimit = 25;
    dashLength = 20;
    isDashing = false;

    PlayerBodyModel playerBody = new PlayerBodyModel(x, y);
    bodies.add(playerBody);

    PlayerSpearModel playerSpear = new PlayerSpearModel(x, y);
    bodies.add(playerSpear);

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


  /** Returns the number of current health points of the player. */
  public int getHealth(){
    return getBodyModel().getHealth();
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

  /** Returns length of dash in frames */
  public int getDashLength() {
    return dashLength;
  }

  /** Returns the max cooldown time of dash */
  public int getDashCooldownLimit(){
    return dashCooldownLimit;
  }


  /** Returns dash direction */
  public Vector2 getDashDirection() {
    return dashDirection;
  }

  /** Sets dash direction */
  public void setDashDirection(Vector2 dir) {
    dashDirection = dir;
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

  /** Updates the object's physics state (NOT GAME LOGIC).
   *
   * Use this for dash cooldown checking/resetting.
   * */
  @Override
  public void update(float delta) {
    if (isDashing()) {
      dashCounter -= 1;
      if (dashCounter <= 0) {
        // exit dash
        stopDashing();
        dashCounter = dashCooldownLimit;
      }
    } else {
      dashCounter = Math.max(0, dashCounter - 1);
    }
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
