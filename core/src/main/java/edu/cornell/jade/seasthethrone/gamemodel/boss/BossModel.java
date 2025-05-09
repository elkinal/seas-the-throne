package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import edu.cornell.jade.seasthethrone.ai.*;
import edu.cornell.jade.seasthethrone.ai.clam.*;
import edu.cornell.jade.seasthethrone.ai.jelly.AimedArcJellyBossController;
import edu.cornell.jade.seasthethrone.ai.jelly.AimedSingleBulletJellyBossController;
import edu.cornell.jade.seasthethrone.ai.jelly.ChasingJellyBossController;
import edu.cornell.jade.seasthethrone.ai.jelly.DelayedRotateRingJellyBossController;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

import java.util.EmptyStackException;

public class BossModel extends EnemyModel implements Renderable {

  /** Boss-unique move animation TODO: make left right up down filmstrips */
  protected FilmStrip moveAnimation;

  protected FilmStrip falloverAnimation;
  protected FilmStrip getHitAnimation;
  protected FilmStrip deathAnimation;
  protected FilmStrip attackAnimation;
  protected FilmStrip attackRightAnimation;
  protected FilmStrip attackUpAnimation;
  protected FilmStrip attackDownAnimation;
  protected FilmStrip shootAnimation;
  protected FilmStrip shootRightAnimation;
  protected FilmStrip shootUpAnimation;
  protected FilmStrip shootDownAnimation;
  protected FilmStrip getHitRightAnimation;
  protected FilmStrip getHitDownAnimation;
  protected FilmStrip getHitUpAnimation;

  /** The current filmstrip being used */
  public FilmStrip filmStrip;

  /** The number of frames since this boss was inititalized */
  protected int frameCounter;

  /** The number of frames between animation updates */
  protected int frameDelay;

  /** The current position in the filmstrip */
  protected int animationFrame;

  protected float scale;

  /** Flag for executing the boss */
  protected boolean isExecute;

  /** Flag for the boss attack */
  protected int attackCount;

  /** Array indicating polygon hitbox */
  protected float[] hitbox;

  /** Amount of knockback force applied to player on body collision */
  private float bodyKnockbackForce;

  /** Amount of knockback force applied to player on spear collision */
  private float spearKnockbackForce;

  /** Number of health points the boss has */
  protected int health;

  /** Hit animation countdown */
  protected int hitCount;

  /** Death animation countdown */
  protected int deathCount;

  /** Execute animation countdown */
  int executeCount;

  /** Whether the boss should continue being animated. */
  private boolean shouldUpdate;

  /** Whether the player should always be animated regardless of game state. */
  private boolean alwaysAnimate;

  /** Health threshold numbers */
  private int[] healthThresholds;

  /** Keeps track of currently tracked threshold */
  private int thresholdPointer;

  /** The ID number of the room this boss is associated with */
  private int roomId;
  /** Full amount of health */
  private int fullHealth;

  /** Tint of the boss */
  private Color color;

  /** If the player is in the room as the boss */
  private boolean inRoom;
  /** If the player finish executing the boss*/
  boolean finishExecute;

  /**
   * {@link BossModel} constructor using an x and y coordinate.
   *
   * @param builder builder for BossModel
   */
  public BossModel(Builder builder) {
    super(builder.hitbox, builder.x, builder.y, builder.type, builder.frameSize);

    moveAnimation = builder.moveAnimation;
    getHitAnimation = builder.getHitAnimation;
    falloverAnimation = builder.falloverAnimation;
    deathAnimation = builder.deathAnimation;
    attackAnimation = builder.attackAnimation;
    shootAnimation = builder.shootAnimation;
    idleAnimation = builder.idleAnimation;
    attackRightAnimation = builder.attackRightAnimation;
    attackUpAnimation = builder.attackUpAnimation;
    attackDownAnimation = builder.attackDownAnimation;
    getHitRightAnimation = builder.getHitRightAnimation;
    getHitUpAnimation = builder.getHitUpAnimation;
    getHitDownAnimation = builder.getHitDownAnimation;
    shootRightAnimation = builder.shootRightAnimation;
    shootUpAnimation = builder.shootUpAnimation;
    shootDownAnimation = builder.shootDownAnimation;

    this.filmStrip = shootAnimation;
    frameCounter = 1;
    frameDelay = builder.frameDelay;
    health = builder.health;
    fullHealth = builder.health;
    scale = builder.scale;
    deathCount = frameDelay * 16;
    attackCount = 0;
    hitCount = 0;
    shouldUpdate = true;
    alwaysAnimate = false;
    roomId = builder.roomId;
    bodyKnockbackForce = 50f;
    spearKnockbackForce = 80f;
    healthThresholds = builder.healthThresholds;
    thresholdPointer = 0;
    isExecute = false;
    color = Color.WHITE;
    inRoom = roomId == -1;
    finishExecute = false;

    //Hack so that the chasing jellies dont shove you past walls
    setBodyType(BodyDef.BodyType.DynamicBody);
    setMass(1000000f);
    setDensity(100000f);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    if (shouldUpdate) {
      progressFrame();
    }
    Vector2 pos = getPosition();
    renderer.draw(filmStrip, pos.x, pos.y, 0.16f*scale, color);
  }

  /** Sets the color of this boss model */
  public void setColor(Color color) {
    this.color = color;
  }

  /** Sets if the boss is in the same room as the player */
  public void setInRoom(boolean value) {
    inRoom = value;
  }

  /** Returns whether the boss is in the same room as the player */
  public boolean isInRoom() { return inRoom; }

  /**
   * Returns whether the boss is finished playing its death animation.
   *
   * @return 0 if the death animation is done playing.
   */
  public int getDeathCount() {
    return deathCount;
  }

  boolean isHit() {
    if (hitCount > 0) {
      return true;
    } else {
      return false;
    }
  }

  boolean isIdle() {
    return (getVX()<=0.1 && getVY()<=0.1);
  }

  @Override
  public void progressFrame() {
    int frame = getFrameNumber();
    if (isDead()) {
      filmStrip = falloverAnimation;
    } else if (isHit()) {
      filmStrip = getHitAnimation;
    } else {
      if (isAttack()) filmStrip = attackAnimation;
      else if (isIdle()) filmStrip = idleAnimation;
      else filmStrip = shootAnimation;
    }
    filmStrip.setFrame(frame);

    if (isDead()) {
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
        deathCount -= 1;
      } else {
        setFrameNumber(getFrameNumber());
        deathCount -= 1;
      }
    } else if (isHit()) {
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
        hitCount -= 1;
      } else {
        setFrameNumber(getFrameNumber());
        hitCount -= 1;
      }
      if (hitCount == 0) {
        setFrameNumber(0);
      }
    } else {
      if (frameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
        if (isAttack()) {
          attackCount -= 1;
        }
      }
    }
    frameCounter += 1;
  }

  /** Returns full health */
  public int getFullHealth(){
    return fullHealth;
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

  public float getBodyKnockbackForce() {
    return bodyKnockbackForce;
  }

  public float getSpearKnockbackForce() {
    return spearKnockbackForce;
  }

  public boolean isDead() {
    return health <= 0;
  }

  public boolean isAttack() {
    return attackCount > 0;
  }

  /** Get remaining health points of the boss */
  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  /** Returns the room this boss is in */
  public int getRoomId() {
    return roomId;
  }

  /** Reduce boss HP by a specified amount If the boss dies, play the fallover animation */
  public void decrementHealth(int damage) {
    hitCount = frameDelay * getHitAnimation.getSize();
    setFrameNumber(0);
    health -= damage;
    if (isDead()) {
      filmStrip = falloverAnimation;
    }
  }

  public void setScale(float s) {
    scale = s;
  }

  public int getFrameNumber() {
    return animationFrame;
  }

  public void setFrameNumber(int frameNumber) {
    animationFrame = frameNumber;
  }

  public FilmStrip getFilmStrip() {
    return filmStrip;
  }

  public int getFramesInAnimation() {
    return filmStrip.getSize();
  }

  /**
   * Returns if the boss's health reached under a certain health threshold.
   *
   * @return True if the health threshold was reach, false otherwise
   */
  public boolean reachedHealthThreshold() {
    if (thresholdPointer < healthThresholds.length
        && health <= healthThresholds[thresholdPointer]) {
      thresholdPointer++;
      return true;
    } else {
      return false;
    }
  }

  /** Lets the player execute the boss */
  public void executeBoss() {
  }
  public boolean isFinishExecute(){
    return finishExecute;
  }
  public boolean isExecute(){
    return isExecute;
  }

  /** Lets the boss attack */
  public void bossAttack() {
    if (attackCount == 0) {
      setFrameNumber(0);
      attackCount = frameDelay * attackAnimation.getSize();
    }
  }

  @Override
  public void update(float delta) {
    if (isDead()) setActive(false);
  }


  public static class Builder {
    /** boss x position */
    private float x;

    /** boss y position */
    private float y;

    /** Type of the boss (ie. crab, etc.). Must be the same string as the boss name in the json. */
    private String type;

    /** Number of frames in boss animation */
    private int frameSize;

    /** Boss-unique move animation */
    private FilmStrip moveAnimation;

    private FilmStrip getHitAnimation;
    private FilmStrip falloverAnimation;
    private FilmStrip deathAnimation;
    private FilmStrip shootAnimation;
    private FilmStrip idleAnimation;
    private FilmStrip attackAnimation;
    private FilmStrip attackRightAnimation;
    private FilmStrip attackUpAnimation;
    private FilmStrip attackDownAnimation;
    private FilmStrip shootRightAnimation;
    private FilmStrip shootUpAnimation;
    private FilmStrip shootDownAnimation;
    private FilmStrip getHitRightAnimation;
    private FilmStrip getHitDownAnimation;
    FilmStrip getHitUpAnimation;
    FilmStrip transformAnimation;
    FilmStrip finalAttackAnimation;
    FilmStrip finalGetHitAnimation;
    FilmStrip finalShootAnimation;
    FilmStrip catchBreathAnimation;
    FilmStrip terminatedAnimation;
    FilmStrip idleUpAnimation;


    /** The number of frames between animation updates */
    private int frameDelay;

    /** Number of health points the boss has */
    protected int health;

    /** Health threshold numbers */
    private int[] healthThresholds;

    /** Boss hitbox */
    private float[] hitbox;

    /** ID for the room this boss is in */
    private int roomId;

    /** Scale of the boss size */
    private float scale;

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

    public Builder setType(String type) {
      this.type = type;
      return this;
    }

    public Builder setHealth(int health) {
      this.health = health;
      return this;
    }

    public Builder setScale(float scale) {
      this.scale = scale;
      return this;
    }

    public Builder setShootAnimation(Texture texture) {
      int width = texture.getWidth();
      shootAnimation = new FilmStrip(texture, 1, width / frameSize);
      ;
      return this;
    }

    public Builder setIdleAnimation(Texture texture) {
      int width = texture.getWidth();
      idleAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }

    public Builder setGetHitAnimation(Texture texture) {
      int width = texture.getWidth();
      getHitAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }

    public Builder setMoveAnimation(Texture texture) {
      int width = texture.getWidth();
      moveAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }

    public Builder setFalloverAnimation(Texture texture) {
      int width = texture.getWidth();
      falloverAnimation = new FilmStrip(texture, 1, width / frameSize);
      if (type.equals("crab")) {
        falloverAnimation = new FilmStrip(texture, 1, 16);
      }
      return this;
    }

    public Builder setDeathAnimation(Texture texture) {
      int width = texture.getWidth();
      deathAnimation = new FilmStrip(texture, 1, width / frameSize);
      if (type.equals("crab")) {
        deathAnimation = new FilmStrip(texture, 1, 18);
      }
      return this;
    }

    public Builder setAttackAnimation(Texture texture) {
      int width = texture.getWidth();
      attackAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setAttackRightAnimation(Texture texture) {
      int width = texture.getWidth();
      attackRightAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setAttackUpAnimation(Texture texture) {
      int width = texture.getWidth();
      attackUpAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setAttackDownAnimation(Texture texture) {
      int width = texture.getWidth();
      attackDownAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setGetHitRightAnimation(Texture texture) {
      int width = texture.getWidth();
      getHitRightAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setGetHitUpAnimation(Texture texture) {
      int width = texture.getWidth();
      getHitUpAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setGetHitDownAnimation(Texture texture) {
      int width = texture.getWidth();
      getHitDownAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setShootRightAnimation(Texture texture) {
      int width = texture.getWidth();
      shootRightAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setShootUpAnimation(Texture texture) {
      int width = texture.getWidth();
      shootUpAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setShootDownAnimation(Texture texture) {
      int width = texture.getWidth();
      shootDownAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setTransformAnimation (Texture texture){
      int width = texture.getWidth();
      transformAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setFinalAttackAnimation (Texture texture){
      int width = texture.getWidth();
      finalAttackAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setFinalGetHitAnimation (Texture texture){
      int width = texture.getWidth();
      finalGetHitAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setFinalShootAnimation (Texture texture){
      int width = texture.getWidth();
      finalShootAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setCatchBreathAnimation (Texture texture){
      int width = texture.getWidth();
      catchBreathAnimation = new FilmStrip(texture, 1, width / frameSize);
      return this;
    }
    public Builder setTerminatedAnimation (Texture texture){
      int width = texture.getWidth();
      terminatedAnimation = new FilmStrip(texture, 1, 1);
      return this;
    }
    public Builder setIdleUpAnimation(Texture texture){
      int width = texture.getWidth();
      idleUpAnimation = new FilmStrip(texture, 1, 1);
      return this;
    }

    public Builder setFrameDelay(int frameDelay) {
      this.frameDelay = frameDelay;
      return this;
    }

    public Builder setRoomId(int id) {
      this.roomId = id;
      return this;
    }

    public Builder setHealthThresholds(int[] thresholds) {
      this.healthThresholds = thresholds;
      return this;
    }

    public Builder setHitbox(float[] hitbox) {
      this.hitbox = hitbox;
      return this;
    }

    public Builder setFrameSize(int frameSize) {
      this.frameSize = frameSize;
      return this;
    }

    public BossModel build() {
      if (type.contains("swordfish")){
        return new SwordfishBossModel(this);
      } else if (type.contains("final")){
        return new FinalBossModel(this);
      } else if (type.contains("crab")) {
        return new CrabBossModel(this);
      } else if (type.contains("shark")) {
        return new SharkBossModel(this);
      } else {
        // Other bosses don't need specific functionality
        return new BossModel(this);
      }
    }

    public BossController buildController(
        BossModel model,
        PlayerModel player,
        BulletModel.Builder bulletBuilder,
        PhysicsEngine physicsEngine) {
      if (type.equals("crab")) {
        return new CrabBossController(model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("shark")) {
        return new SharkBossController(model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("head")) {
      return new HeadBossController(model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("swordfish")) {
        return new SwordfishBossController ((SwordfishBossModel) model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("final")){
        return new FinalBossController((FinalBossModel) model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("aimed_jelly")) {
        return new AimedSingleBulletJellyBossController(model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("arc_jelly")) {
        return new AimedArcJellyBossController(model, player, bulletBuilder, physicsEngine);
      }  else if (type.equals("chasing_jelly")) {
        return new ChasingJellyBossController(model, player, bulletBuilder, physicsEngine);
      } else if (type.contains("fixed_clam")) {
        // invarient, this is a float and won't fail
        float angle = MathUtils.degRad * Float.parseFloat(type.replaceAll("[^\\d.]", ""));
        return new FixedStreamClamController(angle, model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("oscillating_ring_clam")) {
        return new OscillatingRingClamController(model, player, bulletBuilder, physicsEngine);
      } else if (type.contains("random_stream_clam")) {
        float angle = MathUtils.degRad * Float.parseFloat(type.replaceAll("[^\\d.]", ""));
        return new RandomStreamClamController(angle, model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("unbreak_ring_clam")) {
        return new UnbreakableRingClamController(model, player, bulletBuilder, physicsEngine);
      } else if (type.contains("delay_track_clam")) {
        float angle = MathUtils.degRad * Float.parseFloat(type.replaceAll("[^\\d.]", ""));
        return new DelayedTrackingClamController(angle, model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("delay_rotate_jelly")) {
        return new DelayedRotateRingJellyBossController(model, player, bulletBuilder, physicsEngine);
      } else if (type.equals("delay_rotate_clam")) {
        return new DelayedRotateArcClamController(model, player, bulletBuilder, physicsEngine);
      } else if (type.contains("fixed_unbreak_clam")) {
        float angle = MathUtils.degRad * Float.parseFloat(type.replaceAll("[^\\d.]", ""));
        return new UnbreakableFixedStreamClamController(angle, model, player, bulletBuilder, physicsEngine);
      }
      return new EmptyBossController(model);
    }
  }
}
