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
  public static final Texture PLAYER_TEXTURE_UP = new Texture("player/playerspriterun_up_wspear.png");

  /** Player texture when facing down */
  public static final Texture PLAYER_TEXTURE_DOWN = new Texture("player/playerspriterun_down_wspear.png");

  /** Player texture when facing left */
  public static final Texture PLAYER_TEXTURE_LEFT = new Texture("player/playerspriterun_left_wspear.png");

  /** Player texture when facing right */
  public static final Texture PLAYER_TEXTURE_RIGHT = new Texture("player/playerspriterun_right_wspear.png");
  /** Player texture when dashing up */
  public static final Texture PLAYER_TEXTURE_UP_DASH = new Texture("player/playerspritedashfilmstrip_up.png");

  /** Player texture when dashing down */
  public static final Texture PLAYER_TEXTURE_DOWN_DASH = new Texture("player/playerspritedashfilmstrip_down.png");

  /** Player texture when dashing left */
  public static final Texture PLAYER_TEXTURE_LEFT_DASH = new Texture("player/playerspritedashfilmstrip_left.png");

  /** Player texture when dashing right */
  public static final Texture PLAYER_TEXTURE_RIGHT_DASH = new Texture("player/playerspritedashfilmstrip_right.png");


  /** FilmStrip cache object */
  public FilmStrip filmStrip;
  /** FilmStrip cache object for dash up and down */
  public FilmStrip filmStripDashUD;
  /** FilmStrip cache object for dash left and right */

  public FilmStrip filmStripDashLR;
  /** current animation frame */
  private int animationFrame;

  /** The number of frames to skip before animating the next player frame */
  private int frameDelay;

  /**
   * Counter for the number of frames that have been drawn to the screen
   * This is separate from the position in the player filmstrip.
   *  */
  private int frameCounter;
  /**
   * Counter for the number of frames that have been drawn to the screen when dashing
   * This is separate from the position in the player filmstrip.
   *  */
  private int dashFrameCounter;

  /** current direction the player is facing */
  private Direction faceDirection;

  /**
   * Frame counter for between dashing/shooting. Tracks how long until the player can
   * dash/shoot again.
   */
  private int cooldownCounter;

  /** The time limit (in frames) between dashes/shooting */
  private int cooldownLimit;


  /** Whether the player is dashing */
  private boolean isDashing;

  /**
   * Frame counter for dashing. Tracks how long the player has been dashing for.
   */
  private int dashCounter;

  /** The number of frames a dash lasts */
  private int dashLength;

  /** The angle direction of this dash in radians */
  private Vector2 dashDirection;

  /** Whether the player is shooting */
  private boolean isShooting;

  /** The time (in frames) between each bullet shot */
  private int shootCooldownLimit;

  /** Frame counter for shooting. Tracks when a subsequent bullet can be shot. */
  private int shootCounter;

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

    cooldownCounter = 0;
    cooldownLimit = 30;

    moveSpeed = 8f;
    faceDirection = Direction.DOWN;
    dashCounter = 0;
    dashLength = 20;
    isDashing = false;
    frameCounter = 1;
    dashFrameCounter = 1;
    frameDelay = 3;

    shootCooldownLimit = 20;
    shootCounter = 0;
    isShooting = false;

    PlayerBodyModel playerBody = new PlayerBodyModel(x, y);
    bodies.add(playerBody);

    PlayerSpearModel playerSpear = new PlayerSpearModel(x, y);
    bodies.add(playerSpear);

    filmStrip = new FilmStrip(PLAYER_TEXTURE_DOWN, 1, FRAMES_IN_ANIMATION);
    filmStripDashUD = new FilmStrip(PLAYER_TEXTURE_DOWN_DASH, 1, FRAMES_IN_ANIMATION_DASH);
    filmStripDashLR = new FilmStrip(PLAYER_TEXTURE_LEFT_DASH, 1, FRAMES_IN_ANIMATION_DASH);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    PlayerRenderable.super.draw(renderer);

    // Only move to next frame of animation every frameDelay number of frames
    if (isDashing){
      if (dashFrameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
      }
      dashFrameCounter += 1;
    }
    else {
      if (frameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
      }
      frameCounter += 1;
    }

    getSpearModel().draw(renderer);
  }

  public FilmStrip getFilmStrip() {
    if (isDashing) {
      if (faceDirection == Direction.DOWN || faceDirection == Direction.UP)

        return filmStripDashUD;
      else
        return filmStripDashLR;
    }
    else
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

  /** Returns if the player can dash */
  public boolean canDash(){
    return !isDashing && !isShooting && !isInvincible() && cooldownCounter == 0;
  }

  /** Returns if the player can be set to shooting */
  public boolean canShoot(){
    return !isDashing && !isShooting && !isInvincible()
            && cooldownCounter == 0 && getSpearModel().getNumSpeared() > 0 ;
  }

  /** Returns if the player can shoot one bullet. */
  public boolean canShootBullet(){
    return isShooting() && shootCounter == 0 && getSpearModel().getNumSpeared() > 0;
  }


  /** Returns the number of current health points of the player. */
  public int getHealth(){
    return getBodyModel().getHealth();
  }

  /** Sets the player to dashing */
  public void startDashing() {
    isDashing = true;
    frameDelay = dashLength/FRAMES_IN_ANIMATION_DASH;
    frameCounter = 1;
    getSpearModel().setSpear(true);
    animationFrame = 0;
    dashCounter = dashLength;
  }

  /** Set dashing to false */
  public void stopDashing(){
    isDashing = false;
    frameDelay = 3;
    dashFrameCounter = 1;
    animationFrame = 0;
    getSpearModel().setSpear(false);
  }


  /** Returns dash direction */
  public Vector2 getDashDirection() {
    return dashDirection;
  }

  /** Sets dash direction */
  public void setDashDirection(Vector2 dir) {
    dashDirection = dir;
  }

  /** Returns if the player is currently shooting */
  public boolean isShooting() {
    return isShooting;
  }

  /** Sets value for shoot counter to the cooldown limit */
  public void setShootCounter() {
    shootCounter = shootCooldownLimit;
  }

  /** Decrease fish counter. If the counter is sets to 0, stop shooting */
  public void decrementFishCount(){
    getSpearModel().decrementSpear();
    if(getSpearModel().getNumSpeared() <= 0){
      stopShooting();
    }
  }

  /** Sets the player to shooting */
  public void startShooting() {
    isShooting = true;
    shootCounter = 0;
  }

  /** Sets the player to not shooting */
  public void stopShooting(){
    isShooting = false;
    cooldownCounter = cooldownLimit;
  }

  /** Returns if the player is currently invincible */
  public boolean isInvincible() {
    return getBodyModel().isInvincible();
  }

  /** Returns if the player is stunned (during iframes) */
  public boolean isStunned(){
    return getBodyModel().isStunned();
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
   * Use this for cooldown checking/resetting.
   * */
  @Override
  public void update(float delta) {
    if (isDashing()) {
      dashCounter -= 1;
      if (dashCounter <= 0) {
        // exit dash
        stopDashing();
        cooldownCounter = cooldownLimit;
      }
    }
    else if (isShooting()){
      shootCounter = Math.max(0, shootCounter-1);
    }
    else {
      cooldownCounter = Math.max(0, cooldownCounter-1);
    }



    super.update(delta);
  }


  public boolean spearExtended() {
    return isDashing();
  }

  public int frameNumber() {
    return animationFrame;
  }

  public Direction direction() {
    // Don't update direction when stunned
    if(isStunned()) return faceDirection;

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
