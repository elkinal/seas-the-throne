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
public class PlayerModel extends ComplexModel implements PlayerRenderable{
  /** FIXME: stop hardcoding textures */
  /** Frame is player animation */
  private static int FRAMES_IN_ANIMATION;
  private static int FRAMES_IN_ANIMATION_DASH;

  /** Player texture when facing up */
  public static Texture PLAYER_TEXTURE_UP;

  /** Player texture when facing down */
  public static Texture PLAYER_TEXTURE_DOWN;

  /** Player texture when facing left */
  public static Texture PLAYER_TEXTURE_LEFT;

  /** Player texture when facing right */
  public static Texture PLAYER_TEXTURE_RIGHT;
  /** Player texture when dashing up */
  public static Texture PLAYER_TEXTURE_UP_DASH;

  /** Player texture when dashing down */
  public static Texture PLAYER_TEXTURE_DOWN_DASH;

  /** Player texture when dashing left */
  public static Texture PLAYER_TEXTURE_LEFT_DASH;

  /** Player texture when dashing right */
  public static Texture PLAYER_TEXTURE_RIGHT_DASH;

  public Texture dashIndicatorTexture;


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
  /** Initial frame delay */
  private int initFrameDelay;
  /** Used for calculating health differences */
  private int healthCache;


  /**
   * {@link PlayerModel} constructor using an x and y coordinate.
   *
   * @param builder the builder for PlayerModel
   */
  public PlayerModel(Builder builder) {
    super(builder.x, builder.y);
    cooldownCounter = 0;
    cooldownLimit = builder.cooldownLimit;

    FRAMES_IN_ANIMATION = builder.FRAMES_IN_ANIMATION;
    FRAMES_IN_ANIMATION_DASH = builder.FRAMES_IN_ANIMATION_DASH;
    moveSpeed = builder.moveSpeed;
    faceDirection = Direction.DOWN;
    dashCounter = 0;
    dashLength = builder.dashLength;
    isDashing = false;
    frameCounter = 1;
    dashFrameCounter = 1;
    initFrameDelay = builder.frameDelay;
    frameDelay = initFrameDelay;
    PLAYER_TEXTURE_UP = builder.PLAYER_TEXTURE_UP;
    PLAYER_TEXTURE_DOWN = builder.PLAYER_TEXTURE_DOWN;
    PLAYER_TEXTURE_LEFT = builder.PLAYER_TEXTURE_LEFT;
    PLAYER_TEXTURE_RIGHT = builder.PLAYER_TEXTURE_RIGHT;
    PLAYER_TEXTURE_UP_DASH = builder.PLAYER_TEXTURE_UP_DASH;
    PLAYER_TEXTURE_DOWN_DASH = builder.PLAYER_TEXTURE_DOWN_DASH;
    PLAYER_TEXTURE_LEFT_DASH = builder.PLAYER_TEXTURE_LEFT_DASH;
    PLAYER_TEXTURE_RIGHT_DASH = builder.PLAYER_TEXTURE_RIGHT_DASH;
    dashIndicatorTexture = builder.dashIndicatorTexture;

    shootCooldownLimit = builder.shootCooldownLimit;
    shootCounter = 0;
    isShooting = false;

    PlayerBodyModel playerBody = new PlayerBodyModel(builder.x, builder.y);
    bodies.add(playerBody);
    healthCache = playerBody.getHealth();

    PlayerSpearModel playerSpear = new PlayerSpearModel(builder.x, builder.y, dashIndicatorTexture);
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
  }

  public void setFramesInAnimation(int frames){
    FRAMES_IN_ANIMATION = frames;
  }
  public void setFramesInAnimationDash(int frames){

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
  /** Returns whether the player is hit*/
  @Override
  public boolean healthReduced(){
    if (getHealth() == healthCache)
      return false;
    else {
      healthCache = getHealth();
      return true;
    }
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
    frameDelay = initFrameDelay;
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
  public static class Builder {
    /**player x position */
    private float x;
    /**player y position */
    private float y;
    /** Frame is player animation */
    private int FRAMES_IN_ANIMATION;
    private int FRAMES_IN_ANIMATION_DASH;

    /** Player texture when facing up */
    private Texture PLAYER_TEXTURE_UP;

    /** Player texture when facing down */
    private Texture PLAYER_TEXTURE_DOWN;

    /** Player texture when facing left */
    private Texture PLAYER_TEXTURE_LEFT;

    /** Player texture when facing right */
    private Texture PLAYER_TEXTURE_RIGHT;
    /** Player texture when dashing up */
    private Texture PLAYER_TEXTURE_UP_DASH;

    /** Player texture when dashing down */
    private Texture PLAYER_TEXTURE_DOWN_DASH;

    /** Player texture when dashing left */
    private Texture PLAYER_TEXTURE_LEFT_DASH;

    /** Player texture when dashing right */
    private Texture PLAYER_TEXTURE_RIGHT_DASH;
    private Texture dashIndicatorTexture;

    /** The number of frames to skip before animating the next player frame */
    private int frameDelay;

    /** The time limit (in frames) between dashes/shooting */
    private int cooldownLimit;

    /** The number of frames a dash lasts */
    private int dashLength;

    /** The time (in frames) between each bullet shot */
    private int shootCooldownLimit;

    /** Scaling factor for player movement */
    private float moveSpeed;
    public static Builder newInstance()
    {
      return new Builder();
    }

    private Builder() {}
    public Builder setX(float x){
      this.x = x;
      return this;
    }
    public Builder setY(float y){
      this.y = y;
      return this;
    }
    public Builder setFramesInAnimation(int frames){
      FRAMES_IN_ANIMATION = frames;
      return this;
    }
    public Builder setFramesInAnimationDash(int frames){
      FRAMES_IN_ANIMATION_DASH = frames;
      return this;
    }
    public Builder setTextureUp(Texture texture){
      PLAYER_TEXTURE_UP = texture;
      return this;
    }
    public Builder setTextureDown(Texture texture){
      PLAYER_TEXTURE_DOWN = texture;
      return this;
    }
    public Builder setTextureLeft(Texture texture){
      PLAYER_TEXTURE_LEFT = texture;
      return this;
    }
    public Builder setTextureRight(Texture texture){
      PLAYER_TEXTURE_RIGHT = texture;
      return this;
    }
    public Builder setTextureUpDash(Texture texture){
      PLAYER_TEXTURE_UP_DASH = texture;
      return this;
    }
    public Builder setTextureDownDash(Texture texture){
      PLAYER_TEXTURE_DOWN_DASH = texture;
      return this;
    }
    public Builder setTextureLeftDash(Texture texture){
      PLAYER_TEXTURE_LEFT_DASH = texture;
      return this;
    }
    public Builder setTextureRightDash(Texture texture){
      PLAYER_TEXTURE_RIGHT_DASH = texture;
      return this;
    }
    public Builder setDashIndicatorTexture(Texture texture){
      dashIndicatorTexture = texture;
      return this;
    }
    public Builder setFrameDelay(int frameDelay){
      this.frameDelay = frameDelay;
      return this;
    }
    public Builder setCooldownLimit (int cooldownLimit){
      this.cooldownLimit = cooldownLimit;
      return this;
    }
    public Builder setDashLength (int dashLength){
      this.dashLength = dashLength;
      return this;
    }
    public Builder setShootCooldownLimit (int shootCooldownLimit){
      this.shootCooldownLimit = shootCooldownLimit;
      return this;
    }
    public Builder setMoveSpeed (float moveSpeed){
      this.moveSpeed = moveSpeed;
      return this;
    }
    public PlayerModel build(){
      return new PlayerModel(this);
    }
  }
}
