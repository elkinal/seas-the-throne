package edu.cornell.jade.seasthethrone.gamemodel.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import edu.cornell.jade.seasthethrone.audio.SoundPlayer;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;
import com.badlogic.gdx.graphics.Color;

/**
 * Model for the main player object of the game. This class extends {@link ComplexModel} to support
 * multiple joints and bodies for flexible collision control and movement display.
 */
public class PlayerModel extends ComplexModel implements Renderable {
  /** FIXME: stop hardcoding textures */
  /** Frame is player animation */
  private int framesInAnimation;

  private int framesInAnimationDash;

  private int framesInAnimationDashDiagonal;
  private int framesInAnimationShoot;
  private int framesInAnimationDeath;

  /** Sound player for player sound effects */
  private SoundPlayer soundPlayer;

  /** Previous frame health */
  private int prevHealth;
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

  /** Player dashes when dashing diagonally */
  public Texture playerTextureNEDash;

  public Texture playerTextureNWDash;
  public Texture playerTextureSEDash;
  public Texture playerTextureSWDash;

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

  /** Player texture for shooting up */
  private Texture shootUp;

  /** Player texture for shooting down */
  private Texture shootDown;

  /** Player texture for shooting left */
  private Texture shootLeft;

  /** Player texture for shooting right */
  private Texture shootRight;

  /** Player texture for dying up */
  private Texture dieUp;

  /** Player texture for dying down */
  private Texture dieDown;

  /** Player texture for dying left */
  private Texture dieLeft;

  /** Player texture for dying right */
  private Texture dieRight;

  /** Empty texture */
  private Texture empty;
  /** FilmStrip cache object */
  public FilmStrip filmStrip;

  /** FilmStrip cache object for dash up and down */
  public FilmStrip filmStripDashUD;

  /** FilmStrip cache object for dash left and right */
  public FilmStrip filmStripDashLR;

  /** Filmstrip cache object for dash diagonal */
  public FilmStrip filmStripDashDiagonal;

  /** FilmStrip cache object for idle */
  public FilmStrip filmStripIdle;

  /** FilmStrip cache object for shoot */
  public FilmStrip filmStripShoot;

  /** FilmStrip cache object for dying */
  public FilmStrip filmStripDeath;
  /** Empty filmstrip for execution */
  public FilmStrip emptyFilmstrip;
  /** Current FilmStrip */
  public FilmStrip currentStrip;

  /** current animation frame */
  private int animationFrame;

  /** The number of frames to skip before animating the next player frame */
  private int frameDelay;

  /**
   * Counter for the number of frames that have been drawn to the screen This is separate from the
   * position in the player filmstrip.
   */
  private int frameCounter;

  /**
   * Counter for the number of frames that have been drawn to the screen when dashing This is
   * separate from the position in the player filmstrip.
   */
  private int dashFrameCounter;

  /** current direction the player is facing */
  private Direction faceDirection;

  /**
   * Frame counter for between dashing/shooting. Tracks how long until the player can dash/shoot
   * again.
   */
  private int cooldownCounter;

  /** The time limit (in frames) between dashes/shooting */
  private int cooldownLimit;

  /** Whether the player is dashing */
  private boolean isDashing;

  /** Frame counter for dashing. Tracks how long the player has been dashing for. */
  private int dashCounter;

  /** The number of frames a dash lasts */
  private int dashLength;

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

  /** Shoot timer for player */
  private int shootTime;

  /** Death animation countdown */
  private int deathCount;

  /** Whether the player should continue being animated. */
  private boolean shouldUpdate;

  /** Whether the player should always be animated regardless of game state. */
  private boolean alwaysAnimate;
  /** Whether the player has finished executing a boss */
  private boolean finishExecute;

  /**
   * {@link PlayerModel} constructor using an x and y coordinate.
   *
   * @param builder The builder for the PlayerModel
   */
  public PlayerModel(Builder builder) {
    super(builder.x, builder.y);

    this.soundPlayer = builder.soundPlayer;

    cooldownCounter = 0;
    cooldownLimit = builder.cooldownLimit;

    framesInAnimation = builder.framesInAnimation;
    framesInAnimationDash = builder.framesInAnimationDash;
    framesInAnimationDashDiagonal = builder.framesInAnimationDashDiagonal;
    framesInAnimationShoot = builder.framesInAnimationShoot;
    framesInAnimationDeath = builder.framesInAnimationDeath;
    moveSpeed = builder.moveSpeed;
    faceDirection = Direction.DOWN;
    dashCounter = 0;
    dashLength = builder.dashLength;
    isDashing = false;
    shouldUpdate = true;
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
    playerTextureNEDash = builder.playerTextureNEDash;
    playerTextureNWDash = builder.playerTextureNWDash;
    playerTextureSEDash = builder.playerTextureSEDash;
    playerTextureSWDash = builder.playerTextureSWDash;
    dashIndicatorTexture = builder.dashIndicatorTexture;
    idleLeft = builder.idleLeft;
    idleRight = builder.idleRight;
    idleUp = builder.idleUp;
    idleDown = builder.idleDown;
    shootUp = builder.shootUp;
    shootDown = builder.shootDown;
    shootLeft = builder.shootLeft;
    shootRight = builder.shootRight;
    dieUp = builder.dieUp;
    dieDown = builder.dieDown;
    dieLeft = builder.dieLeft;
    dieRight = builder.dieRight;
    empty = builder.empty;

    shootCooldownLimit = builder.shootCooldownLimit;
    shootCounter = 0;
    isShooting = false;
    shootTime = 0;
    deathCount = framesInAnimationDeath * initFrameDelay;

    PlayerBodyModel playerBody = new PlayerBodyModel(builder.x, builder.y);
    bodies.add(playerBody);

    PlayerSpearModel playerSpear =
        new PlayerSpearModel(builder.x, builder.y, dashIndicatorTexture, playerBody);
    bodies.add(playerSpear);

    PlayerShadowModel playerShadow = new PlayerShadowModel(builder.x, builder.y - 1.6f);
    bodies.add(playerShadow);

    filmStrip = new FilmStrip(playerTextureDown, 1, framesInAnimation);
    filmStripDashUD = new FilmStrip(playerTextureDownDash, 1, framesInAnimationDash);
    filmStripDashLR = new FilmStrip(playerTextureLeftDash, 1, framesInAnimationDash);
    filmStripDashDiagonal = new FilmStrip(playerTextureNEDash, 1, framesInAnimationDashDiagonal);
    filmStripIdle = new FilmStrip(idleDown, 1, 1);
    filmStripShoot = new FilmStrip(shootDown, 1, framesInAnimationShoot);
    filmStripDeath = new FilmStrip(dieDown, 1, framesInAnimationDeath);
    emptyFilmstrip = new FilmStrip(dieDown, 1, 1);
    prevHealth = playerBody.getHealth();
    finishExecute = false;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    if (shouldUpdate) {
      progressFrame();
    }
    Vector2 pos = getPosition();
    if (getBodyModel().isHit() && !isDead()) {
      renderer.draw(currentStrip, pos.x, pos.y, 0.12f, Color.RED);
    }
    else if (getBodyModel().isHeal() && !isDead()) {
      renderer.draw(currentStrip, pos.x, pos.y, 0.12f, Color.GREEN);
    }
    else renderer.draw(currentStrip, pos.x, pos.y, 0.12f);
    getSpearModel().draw(renderer);
  }

  @Override
  public void progressFrame() {
    currentStrip = getFilmStrip();
    if (isExecuting()){
      currentStrip.setTexture(empty);
      setFrameNumber(0);
    }
    else if (!isDashing()){
      switch (direction()) {
        case UP:
          if (isDead())
            currentStrip.setTexture(getDieUp());
          else if (isShootingAnimated()) {
            currentStrip.setTexture(getShootUp());
          } else if (isIdle())
            currentStrip.setTexture(getIdleUp());
          else
            currentStrip.setTexture(getTextureUp());
          break;
        case DOWN:
          if (isDead())
            currentStrip.setTexture(getDieDown());
          else if (isShootingAnimated())
            currentStrip.setTexture(getShootDown());
          else if (isIdle())
            currentStrip.setTexture(getIdleDown());
          else
            currentStrip.setTexture(getTextureDown());
          break;
        case LEFT:
          if (isDead())
            currentStrip.setTexture(getDieLeft());
          else if (isShootingAnimated())
            currentStrip.setTexture(getShootLeft());
          else if (isIdle())
            currentStrip.setTexture(getIdleLeft());
          else
            currentStrip.setTexture(getTextureLeft());
          break;
        case RIGHT:
          if (isDead())
            currentStrip.setTexture(getDieRight());
          else if (isShootingAnimated())
            currentStrip.setTexture(getShootRight());
          else if (isIdle())
            currentStrip.setTexture(getIdleRight());
          else
            currentStrip.setTexture(getTextureRight());
          break;
      }
    }
    else {
      if (isDead()) {
        switch (direction()) {
          case UP -> currentStrip.setTexture(getDieUp());
          case DOWN -> currentStrip.setTexture(getDieDown());
          case LEFT -> currentStrip.setTexture(getDieLeft());
          case RIGHT -> currentStrip.setTexture(getDieRight());
        }
      } else{
        switch (direction()) {
          case UP -> currentStrip.setTexture(getTextureUpDash());
          case DOWN -> currentStrip.setTexture(getTextureDownDash());
          case LEFT -> currentStrip.setTexture(getTextureLeftDash());
          case RIGHT -> currentStrip.setTexture(getTextureRightDash());
          case NE -> currentStrip.setTexture(getTextureNEDash());
          case NW -> currentStrip.setTexture(getTextureNWDash());
          case SE -> currentStrip.setTexture(getTextureSEDash());
          case SW -> currentStrip.setTexture(getTextureSWDash());
        }
      }
    }
    int frame = getFrameNumber();
    currentStrip.setFrame(frame);

    // Only move to next frame of animation every frameDelay number of frames
    if (isExecuting()){
      setFrameNumber(0);
    }
    else if (isDead()) {
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
      } else setFrameNumber(getFrameNumber());
      frameCounter += 1;
    } else if (isDashing) {
      if (dashFrameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
      }
      dashFrameCounter += 1;
    } else if (isShootingAnimated()) {
      if (frameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
      }
      frameCounter += 1;
    } else if (isIdle()) {
      setFrameNumber(getFrameNumber());
    } else {
      if (frameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
      }
      frameCounter += 1;
    }
  }

  @Override
  public void alwaysUpdate() {
    shouldUpdate = true;
  }

  @Override
  public void neverUpdate() {
    shouldUpdate = false;
  }

  @Override
  public void setAlwaysAnimate(boolean animate) {
    alwaysAnimate = animate;
  }

  @Override
  public boolean alwaysAnimate() {
    return alwaysAnimate;
  }
  public boolean isExecuting(){return getBodyModel().isExecute();}
  public void setFinishExecute(boolean execute){
    finishExecute = execute;
  }
  public boolean isFinishExecute(){
    return finishExecute;
  }

  public FilmStrip getFilmStrip() {
    if(isExecuting()){
      return emptyFilmstrip;
    }
    if (isDead()) return filmStripDeath;
    else if (isDashing) {
      if (faceDirection == Direction.DOWN || faceDirection == Direction.UP) return filmStripDashUD;
      else if (faceDirection == Direction.RIGHT || faceDirection == Direction.LEFT)
        return filmStripDashLR;
      else return filmStripDashDiagonal;
    } else if (isShootingAnimated()) {
      return filmStripShoot;
    } else if (isIdle()) return filmStripIdle;
    else return filmStrip;
  }

  public int getFrameNumber() {
    return animationFrame;
  }

  public void setFrameNumber(int animationFrame) {
    this.animationFrame = animationFrame;
  }

  public int getFramesInAnimation() {
    if (isDead()) return framesInAnimationDeath;
    else if (isDashing) return framesInAnimationDash;
    else if (isShootingAnimated()) return framesInAnimationShoot;
    else if (isIdle()) return 1;
    else return framesInAnimation;
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

  public Texture getTextureNEDash() {
    return playerTextureNEDash;
  }

  public Texture getTextureNWDash() {
    return playerTextureNWDash;
  }

  public Texture getTextureSWDash() {
    return playerTextureSWDash;
  }

  public Texture getTextureSEDash() {
    return playerTextureSEDash;
  }

  public Texture getIdleLeft() {
    return idleLeft;
  }

  public Texture getIdleRight() {
    return idleRight;
  }

  public Texture getIdleUp() {
    return idleUp;
  }

  public Texture getIdleDown() {
    return idleDown;
  }

  public Texture getShootUp() {
    return shootUp;
  }

  public Texture getShootDown() {
    return shootDown;
  }

  public Texture getShootRight() {
    return shootRight;
  }

  public Texture getShootLeft() {
    return shootLeft;
  }

  public Texture getDieUp() {
    return dieUp;
  }

  public Texture getDieDown() {
    return dieDown;
  }

  public Texture getDieLeft() {
    return dieLeft;
  }

  public Texture getDieRight() {
    return dieRight;
  }
  public Texture getEmpty() {
    return empty;
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
   * Returns true if the player is idle.
   *
   * @return true if the player is idle.
   */
  public boolean isIdle() {
    return getVX() <= 0.1 && getVY() <= 0.1 && getVX() >= -0.1 && getVY() >= -0.1;
  }

  public boolean isShootingAnimated() {
    return shootCounter > 0;
  }

  public boolean isDead() {
    if (getHealth() <= 0) {
      getBodyModel().markRemoved(true);
    }
    return getHealth() <= 0;
  }

  /** Returns if the player can dash */
  public boolean canDash() {
    return !isDashing
      && !isShooting
      && !isExecuting()
      && cooldownCounter == 0;
  }

  /** Returns if the player can be set to shooting */
  public boolean canShoot() {
    return !isDashing
      && !isShooting
      && cooldownCounter == 0
      && getSpearModel().getNumSpeared() > 0;
  }

  /** Returns the number of current health points of the player. */
  public int getHealth() {
    return getBodyModel().getHealth();
  }

  public void setHealth(int hp) {
    getBodyModel().setHealth(hp);
  }

  /** Sets the player to dashing */
  public void startDashing() {
    isDashing = true;
    frameDelay = dashLength / framesInAnimationDash;
    frameCounter = 1;
    getSpearModel().setSpear(true);
    animationFrame = 0;
    dashCounter = dashLength;
    getBodyModel().setInvincible(dashLength);

    soundPlayer.playSoundEffect("dash");
  }

  /** Set dashing to false */
  public void stopDashing() {
    isDashing = false;
    frameDelay = initFrameDelay;
    dashFrameCounter = 1;
    animationFrame = 0;
    cooldownCounter = cooldownLimit;
    dashCounter = 0;
    getSpearModel().setSpear(false);
  }

  /** Returns if the player is currently shooting */
  public boolean isShooting() {
    return isShooting;
  }

  /** Decrease fish counter. */
  public void decrementFishCount() {
    getSpearModel().decrementSpear();
  }

  /** Sets the player to shooting */
  public void startShooting() {
    soundPlayer.playSoundEffect("shoot-bullet");
    isShooting = true;
    shootCounter = shootCooldownLimit;
    animationFrame = 0;
    frameCounter = 1;
    frameDelay = shootCooldownLimit / framesInAnimationShoot;
  }

  /** Sets the player to not shooting */
  public void stopShooting() {
    isShooting = false;
    animationFrame = 0;
    frameCounter = 1;
    frameDelay = initFrameDelay;
  }

  /** Initialize dying process */
  public void startDying() {
    animationFrame = 0;
    frameCounter = 1;
    dashFrameCounter = 1;
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

  /** Update the player's spear model for dashing */
  public void updateSpear(Vector2 dashDirection) {
    getSpearModel().updateSpear(dashDirection);
  }

  /** Update the player's dash indicator (this is purely for rendering) */
  public void updateDashIndicator(Vector2 dashDirection) {
    getSpearModel().updateDashIndicator(dashDirection);
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
   * <p>Use this for cooldown checking/resetting.
   */
  @Override
  public void update(float delta) {
    if (getBodyModel().shouldStopDashing()){
      if (isDashing()) stopDashing();
      getBodyModel().setStopDashing(false);
    }

    if (isDead()) {
      if (deathCount == framesInAnimationDeath * initFrameDelay) startDying();
      deathCount = Math.max(0, deathCount - 1);
    } else if (isDashing()) {
      dashCounter -= 1;
      if (dashCounter <= 0) {
        // exit dash
        stopDashing();
      }
    } else if (isShooting()) {
      shootCounter -= 1;
      if (shootCounter <= 0){
        stopShooting();
      }
    } else if (isIdle()) {
      animationFrame = 0;
      frameCounter = 1;
      dashFrameCounter = 1;
      cooldownCounter = Math.max(0, cooldownCounter - 1);
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

    if (isDashing()) {
      if (isDead()) {
        if (Math.abs(vx) > Math.abs(vy)) {
          if (vx > 0) faceDirection = Direction.RIGHT;
          else faceDirection = Direction.LEFT;
        } else if (Math.abs(vx) < Math.abs(vy)) {
          if (vy > 0) faceDirection = Direction.UP;
          else faceDirection = Direction.DOWN;
        }
      }
      else{
          if (Math.abs(vx) > Math.abs(vy)) {
          final boolean nonDiagonalX = 0.414 * Math.abs(vx) > Math.abs(vy);
          if (vx > 0) {
            if (nonDiagonalX) faceDirection = Direction.RIGHT;
            else if (vy > 0) faceDirection = Direction.NE;
            else faceDirection = Direction.SE;
          } else {
            if (nonDiagonalX) faceDirection = Direction.LEFT;
            else if (vy > 0) faceDirection = Direction.NW;
            else faceDirection = Direction.SW;
          }
        } else if (Math.abs(vx) < Math.abs(vy)) {
          final boolean nonDiagonalY = 0.414 * Math.abs(vy) > Math.abs(vx);
          if (vy > 0) {
            if (nonDiagonalY) faceDirection = Direction.UP;
            else if (vx > 0) faceDirection = Direction.NE;
            else faceDirection = Direction.NW;
          } else {
            if (nonDiagonalY) faceDirection = Direction.DOWN;
            else if (vx > 0) faceDirection = Direction.SE;
            else faceDirection = Direction.SW;
          }
        }
      }
    }
    else{
      if (Math.abs(vx) > Math.abs(vy)) {
        if (vx > 0) faceDirection = Direction.RIGHT;
        else faceDirection = Direction.LEFT;
      } else if (Math.abs(vx) < Math.abs(vy)) {
        if (vy > 0) faceDirection = Direction.UP;
        else faceDirection = Direction.DOWN;
      } else if (Math.abs(vx) == Math.abs(vy) && Math.abs(vx) > 0) {
        if (vx > 0) faceDirection = Direction.RIGHT;
        else faceDirection = Direction.LEFT;
      }
    }
  }

  public static class Builder {
    /** player x position */
    private float x;

    /** player y position */
    private float y;

    /** Frame is player animation */
    private int framesInAnimation;

    private int framesInAnimationDash;
    private int framesInAnimationDashDiagonal;
    private int framesInAnimationShoot;
    private int framesInAnimationDeath;

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

    /** Player dashes when dashing diagonally */
    public Texture playerTextureNEDash;

    public Texture playerTextureNWDash;
    public Texture playerTextureSEDash;
    public Texture playerTextureSWDash;
    private Texture dashIndicatorTexture;

    /** player texture for idle left */
    private Texture idleLeft;

    /** Player texture for idle right */
    private Texture idleRight;

    /** Player texture for idle up */
    private Texture idleUp;

    /** Player texture for idle down */
    private Texture idleDown;

    /** Player texture for shooting up */
    private Texture shootUp;

    /** Player texture for shooting down */
    private Texture shootDown;

    /** Player texture for shooting left */
    private Texture shootLeft;

    /** Player texture for shooting right */
    private Texture shootRight;

    /** Player texture for dying up */
    private Texture dieUp;

    /** Player texture for dying down */
    private Texture dieDown;

    /** Player texture for dying left */
    private Texture dieLeft;

    /** Player texture for dying right */
    private Texture dieRight;
    /** Empty Texture */
    private Texture empty;

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

    private SoundPlayer soundPlayer;

    public static Builder newInstance() {
      return new Builder();
    }

    private Builder() {}

    public Builder setX(float x) {
      this.x = x;
      return this;
    }

    public Builder setY(float y) {
      this.y = y;
      return this;
    }

    public Builder setFramesInAnimation(int frames) {
      framesInAnimation = frames;
      return this;
    }

    public Builder setFramesInAnimationDash(int frames) {
      framesInAnimationDash = frames;
      return this;
    }

    public Builder setFramesInAnimationDashDiagonal(int frames) {
      framesInAnimationDashDiagonal = frames;
      return this;
    }

    public Builder setFramesInAnimationShoot(int frames) {
      framesInAnimationShoot = frames;
      return this;
    }

    public Builder setFramesInAnimationDeath(int frames) {
      framesInAnimationDeath = frames;
      return this;
    }

    public Builder setTextureUp(Texture texture) {
      playerTextureUp = texture;
      return this;
    }

    public Builder setTextureDown(Texture texture) {
      playerTextureDown = texture;
      return this;
    }

    public Builder setTextureLeft(Texture texture) {
      playerTextureLeft = texture;
      return this;
    }

    public Builder setTextureRight(Texture texture) {
      playerTextureRight = texture;
      return this;
    }

    public Builder setTextureUpDash(Texture texture) {
      playerTextureUpDash = texture;
      return this;
    }

    public Builder setTextureDownDash(Texture texture) {
      playerTextureDownDash = texture;
      return this;
    }

    public Builder setTextureLeftDash(Texture texture) {
      playerTextureLeftDash = texture;
      return this;
    }

    public Builder setTextureRightDash(Texture texture) {
      playerTextureRightDash = texture;
      return this;
    }

    public Builder setTextureNEDash(Texture texture) {
      playerTextureNEDash = texture;
      return this;
    }

    public Builder setTextureNWDash(Texture texture) {
      playerTextureNWDash = texture;
      return this;
    }

    public Builder setTextureSEDash(Texture texture) {
      playerTextureSEDash = texture;
      return this;
    }

    public Builder setTextureSWDash(Texture texture) {
      playerTextureSWDash = texture;
      return this;
    }

    public Builder setDashIndicatorTexture(Texture texture) {
      dashIndicatorTexture = texture;
      return this;
    }

    public Builder setIdleLeft(Texture texture) {
      idleLeft = texture;
      return this;
    }

    public Builder setIdleRight(Texture texture) {
      idleRight = texture;
      return this;
    }

    public Builder setIdleUp(Texture texture) {
      idleUp = texture;
      return this;
    }

    public Builder setIdleDown(Texture texture) {
      idleDown = texture;
      return this;
    }

    public Builder setShootUp(Texture texture) {
      shootUp = texture;
      return this;
    }

    public Builder setShootDown(Texture texture) {
      shootDown = texture;
      return this;
    }

    public Builder setShootLeft(Texture texture) {
      shootLeft = texture;
      return this;
    }

    public Builder setShootRight(Texture texture) {
      shootRight = texture;
      return this;
    }

    public Builder setDeathLeft(Texture texture) {
      dieLeft = texture;
      return this;
    }

    public Builder setDeathRight(Texture texture) {
      dieRight = texture;
      return this;
    }

    public Builder setDeathUp(Texture texture) {
      dieUp = texture;
      return this;
    }

    public Builder setDeathDown(Texture texture) {
      dieDown = texture;
      return this;
    }
    public Builder setEmpty(Texture texture){
      empty = texture;
      return this;
    }

    public Builder setFrameDelay(int frameDelay) {
      this.frameDelay = frameDelay;
      return this;
    }

    public Builder setCooldownLimit(int cooldownLimit) {
      this.cooldownLimit = cooldownLimit;
      return this;
    }

    public Builder setDashLength(int dashLength) {
      this.dashLength = dashLength;
      return this;
    }

    public Builder setShootCooldownLimit(int shootCooldownLimit) {
      this.shootCooldownLimit = shootCooldownLimit;
      return this;
    }

    public Builder setMoveSpeed(float moveSpeed) {
      this.moveSpeed = moveSpeed;
      return this;
    }

    public Builder setSoundPlayer(SoundPlayer soundPlayer) {
      this.soundPlayer = soundPlayer;
      return this;
    }

    public PlayerModel build() {
      return new PlayerModel(this);
    }
  }
}
