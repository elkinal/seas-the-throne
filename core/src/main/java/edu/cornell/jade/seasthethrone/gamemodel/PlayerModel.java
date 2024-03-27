package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.render.PlayerRenderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/**
 * Model for the main player object of the game. This class extends
 * {@link ComplexModel} to support
 * multiple joints and bodies for flexible collision control and movement
 * display.
 */
public class PlayerModel extends ComplexModel implements PlayerRenderable {
  /** FIXME: stop hardcoding textures */
  /** Frame is player animation */
  private int framesInAnimation;
  private int framesInAnimationDash;

  /** Player texture when facing up */
  public Texture playerTextureUp;

  /** Player texture when facing down */
  public Texture playerTextureDown;

  /** Player texture when facing left */
  public Texture playerTextureLeft;

  /** Player texture when facing right */
  public Texture playerTextureRight;
  /** Player texture when dashing up */
  public Texture playerTextureUpDash;

  /** Player texture when dashing down */
  public Texture playerTextureDownDash;

  /** Player texture when dashing left */
  public Texture playerTextureLeftDash;

  /** Player texture when dashing right */
  public Texture playerTextureRightDash;

  /** Player texture for the das indicator */
  private Texture dashIndicatorTexture;
  /** Player texture for idle left */
  private Texture idleLeft;
  /** Player texture for idle right */
  private Texture idleRight;
  /** Player texture for idle up */
  private Texture idleUp;
  /** Player texture for idle down */
  private Texture idleDown;

  /** FilmStrip cache object */
  public FilmStrip filmStrip;
  /** FilmStrip cache object for dash up and down */
  public FilmStrip filmStripDashUD;
  /** FilmStrip cache object for dash left and right */

  public FilmStrip filmStripDashLR;
  /** FilmStrip cache object for idle */
  public FilmStrip filmStripIdle;
  /** current animation frame */
  private int animationFrame;

  /** The number of frames to skip before animating the next player frame */
  private int frameDelay;

  /**
   * Counter for the number of frames that have been drawn to the screen
   * This is separate from the position in the player filmstrip.
   */
  private int frameCounter;
  /**
   * Counter for the number of frames that have been drawn to the screen when
   * dashing
   * This is separate from the position in the player filmstrip.
   */
  private int dashFrameCounter;

  /** current direction the player is facing */
  private Direction faceDirection;

  /**
   * Frame counter for between dashing/shooting. Tracks how long until the player
   * can
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

  /**
   * {@link PlayerModel} constructor using an x and y coordinate.
   *
   * @param builder The builder for the PlayerModel
   */
  public PlayerModel(Builder builder) {
    super(builder.x, builder.y);

    cooldownCounter = 0;
    cooldownLimit = builder.cooldownLimit;

    framesInAnimation = builder.framesInAnimation;
    framesInAnimationDash = builder.framesInAnimationDash;
    moveSpeed = builder.moveSpeed;
    faceDirection = Direction.DOWN;
    dashCounter = 0;
    dashLength = builder.dashLength;
    isDashing = false;
    frameCounter = 1;
    dashFrameCounter = 1;
    initFrameDelay = builder.frameDelay;
    frameDelay = initFrameDelay;
    playerTextureUp = builder.playerTextureUp;
    playerTextureDown = builder.playerTextureDown;
    playerTextureLeft = builder.playerTextureLeft;
    playerTextureRight = builder.playerTextureRight;
    playerTextureUpDash = builder.playerTextureUpDash;
    playerTextureDownDash = builder.playerTextureDownDash;
    playerTextureLeftDash = builder.playerTextureLeftDash;
    playerTextureRightDash = builder.playerTextureRightDash;
    dashIndicatorTexture = builder.dashIndicatorTexture;
    idleLeft = builder.idleLeft;
    idleRight = builder.idleRight;
    idleUp = builder.idleUp;
    idleDown = builder.idleDown;

    shootCooldownLimit = builder.shootCooldownLimit;
    shootCounter = 0;
    isShooting = false;

    PlayerBodyModel playerBody = new PlayerBodyModel(builder.x, builder.y);
    bodies.add(playerBody);

    PlayerSpearModel playerSpear = new PlayerSpearModel(builder.x, builder.y, dashIndicatorTexture);
    bodies.add(playerSpear);

    PlayerShadowModel playerShadow = new PlayerShadowModel(builder.x, builder.y - 1.6f);
    bodies.add(playerShadow);

    filmStrip = new FilmStrip(playerTextureDown, 1, framesInAnimation);
    filmStripDashUD = new FilmStrip(playerTextureDownDash, 1, framesInAnimationDash);
    filmStripDashLR = new FilmStrip(playerTextureLeftDash, 1, framesInAnimationDash);
    filmStripIdle = new FilmStrip(idleDown, 1, 1);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    PlayerRenderable.super.draw(renderer);

    // Only move to next frame of animation every frameDelay number of frames
    if (isDashing) {
      if (dashFrameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
      }
      dashFrameCounter += 1;
    } else if (isIdle()){
      setFrameNumber(getFrameNumber());
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
    } else if (isIdle())
        return filmStripIdle;
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
      return framesInAnimationDash;
    else if (isIdle())
      return 1;
    else
      return framesInAnimation;
  }

  public Texture getTextureUp() {
    return playerTextureUp;
  }

  public Texture getTextureDown() {
    return playerTextureDown;
  }

  public Texture getTextureLeft() {
    return playerTextureLeft;
  }

  public Texture getTextureRight() {
    return playerTextureRight;
  }

  public Texture getTextureUpDash() {
    return playerTextureUpDash;
  }

  public Texture getTextureDownDash() {
    return playerTextureDownDash;
  }

  public Texture getTextureLeftDash() {
    return playerTextureLeftDash;
  }

  public Texture getTextureRightDash() {
    return playerTextureRightDash;
  }
  public Texture getIdleLeft() {return idleLeft; }
  public Texture getIdleRight() {return idleRight; }
  public Texture getIdleUp() {return idleUp; }
  public Texture getIdleDown() {return idleDown; }



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
  @Override
  public boolean isDashing() {
    return isDashing;
  }
  /**
   * Returns true if the player is idle.
   *
   * @return true if the player is idle.
   */
  @Override
  public boolean isIdle() {
    if (getVX() <= 0.1 && getVY() <= 0.1 && getVX()>=-0.1 && getVY()>=-0.1){
      frameCounter = 1;
      dashFrameCounter = 1;
      animationFrame = 0;
      return true;
    }
    return false;
  }

  /** Returns if the player can dash */
  public boolean canDash() {
    return !isDashing && !isShooting && !isInvincible() && cooldownCounter == 0;
  }

  /** Returns if the player can be set to shooting */
  public boolean canShoot() {
    return !isDashing && !isShooting && !isInvincible()
        && cooldownCounter == 0 && getSpearModel().getNumSpeared() > 0;
  }

  /** Returns if the player can shoot one bullet. */
  public boolean canShootBullet() {
    return isShooting() && shootCounter == 0 && getSpearModel().getNumSpeared() > 0;
  }

  /** Returns the number of current health points of the player. */
  public int getHealth() {
    return getBodyModel().getHealth();
  }

  /** Sets the player to dashing */
  public void startDashing() {
    isDashing = true;
    frameDelay = dashLength/ framesInAnimationDash;
    frameCounter = 1;
    getSpearModel().setSpear(true);
    animationFrame = 0;
    dashCounter = dashLength;
  }

  /** Set dashing to false */
  public void stopDashing() {
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
  public void decrementFishCount() {
    getSpearModel().decrementSpear();
    if (getSpearModel().getNumSpeared() <= 0) {
      stopShooting();
    }
  }

  /** Sets the player to shooting */
  public void startShooting() {
    isShooting = true;
    shootCounter = 0;
  }

  /** Sets the player to not shooting */
  public void stopShooting() {
    isShooting = false;
    cooldownCounter = cooldownLimit;
  }

  /** Returns if the player is currently invincible */
  public boolean isInvincible() {
    return getBodyModel().isInvincible();
  }

  /** Returns if the player is knocked back (during iframes) */
  public boolean isKnockedBack() {
    return getBodyModel().isKnockedBack();
  }

  /** Returns the player body model */
  public PlayerBodyModel getBodyModel() {
    return (PlayerBodyModel) bodies.get(0);
  }

  /** Returns the player spear model */
  public PlayerSpearModel getSpearModel() {
    return (PlayerSpearModel) bodies.get(1);
  }

  /** Returns the player shadow model */
  public PlayerShadowModel getShadowModel() {
    return (PlayerShadowModel) bodies.get(2);
  }

  /** Update the player's spear model when dashing */
  public void updateSpear(Vector2 dashDirection) {
    getSpearModel().updateSpear(getPosition(), dashDirection);
  }

  @Override
  protected boolean createJoints(World world) {
    RevoluteJointDef jointDef = new RevoluteJointDef();
    Body bodyA = getBodyModel().getBody();
    Body bodyB = getShadowModel().getBody();
    jointDef.initialize(bodyA, bodyB, bodyA.getWorldCenter());
    jointDef.collideConnected = false;

    Joint joint = world.createJoint(jointDef);
    joints.add(joint);

    return true;
  }

  /**
   * Updates the object's physics state (NOT GAME LOGIC).
   *
   * Use this for cooldown checking/resetting.
   */
  @Override
  public void update(float delta) {
    if (isDashing()) {
      dashCounter -= 1;
      if (dashCounter <= 0) {
        // exit dash
        stopDashing();
        cooldownCounter = cooldownLimit;
      }
    } else if (isShooting()) {
      shootCounter = Math.max(0, shootCounter - 1);
    } else {
      cooldownCounter = Math.max(0, cooldownCounter - 1);
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
    return faceDirection;
  }

  public void setDirection(Vector2 moveDirection) {
    float vx = moveDirection.x;
    float vy = moveDirection.y;

    if (Math.abs(vx) > Math.abs(vy)) {
      if (vx > 0)
        faceDirection = Direction.RIGHT;
      else
        faceDirection = Direction.LEFT;
    } else if (Math.abs(vx) < Math.abs(vy)) {
      if (vy > 0)
        faceDirection = Direction.UP;
      else
        faceDirection = Direction.DOWN;
    }
  }
  public static class Builder {
    /**player x position */
    private float x;
    /**player y position */
    private float y;
    /** Frame is player animation */
    private int framesInAnimation;
    private int framesInAnimationDash;

    /** Player texture when facing up */
    private Texture playerTextureUp;

    /** Player texture when facing down */
    private Texture playerTextureDown;

    /** Player texture when facing left */
    private Texture playerTextureLeft;

    /** Player texture when facing right */
    private Texture playerTextureRight;
    /** Player texture when dashing up */
    private Texture playerTextureUpDash;

    /** Player texture when dashing down */
    private Texture playerTextureDownDash;

    /** Player texture when dashing left */
    private Texture playerTextureLeftDash;

    /** Player texture when dashing right */
    private Texture playerTextureRightDash;
    private Texture dashIndicatorTexture;
    /** player texture for idle left */
    private Texture idleLeft;
    /** Player texture for idle right */
    private Texture idleRight;
    /** Player texture for idle up */
    private Texture idleUp;
    /** Player texture for idle down */
    private Texture idleDown;

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
      framesInAnimation = frames;
      return this;
    }
    public Builder setFramesInAnimationDash(int frames){
      framesInAnimationDash = frames;
      return this;
    }
    public Builder setTextureUp(Texture texture){
      playerTextureUp = texture;
      return this;
    }
    public Builder setTextureDown(Texture texture){
      playerTextureDown = texture;
      return this;
    }
    public Builder setTextureLeft(Texture texture){
      playerTextureLeft = texture;
      return this;
    }
    public Builder setTextureRight(Texture texture){
      playerTextureRight = texture;
      return this;
    }
    public Builder setTextureUpDash(Texture texture){
      playerTextureUpDash = texture;
      return this;
    }
    public Builder setTextureDownDash(Texture texture){
      playerTextureDownDash = texture;
      return this;
    }
    public Builder setTextureLeftDash(Texture texture){
      playerTextureLeftDash = texture;
      return this;
    }
    public Builder setTextureRightDash(Texture texture){
      playerTextureRightDash = texture;
      return this;
    }
    public Builder setDashIndicatorTexture(Texture texture){
      dashIndicatorTexture = texture;
      return this;
    }
    public Builder setIdleLeft(Texture texture){
      idleLeft = texture;
      return this;
    }
    public Builder setIdleRight(Texture texture){
      idleRight = texture;
      return this;
    }
    public Builder setIdleUp(Texture texture){
      idleUp = texture;
      return this;
    }
    public Builder setIdleDown(Texture texture){
      idleDown = texture;
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
