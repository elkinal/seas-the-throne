package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.jade.seasthethrone.gamemodel.EnemyModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

import java.util.Arrays;
import java.util.Iterator;

public abstract class BossModel extends EnemyModel implements Renderable {

  /** Number of frames in boss animation */
  private int frameSize;

  /** Boss-unique move animation TODO: make left right up down filmstrips */
  private FilmStrip moveAnimation;

  private FilmStrip falloverAnimation;
  private FilmStrip getHitAnimation;

  /** The current filmstrip being used */
  public FilmStrip filmStrip;

  /** The number of frames since this boss was inititalized */
  protected int frameCounter;

  /** The number of frames between animation updates */
  protected int frameDelay;

  /** The current position in the filmstrip */
  protected int animationFrame;

  protected float scale;

  /** Amount of knockback force applied to player on body collision */
  private float bodyKnockbackForce;

  /** Amount of knockback force applied to player on spear collision */
  private float spearKnockbackForce;

  /** Number of health points the boss has */
  protected int health;

  /** Death animation countdown */
  private int deathCount;

  /** Health threshold numbers */
  private int[] healthThresholds;

  /** Keeps track of currently tracked threshold */
  private int thresholdPointer;

  /** The ID number of the room this boss is associated with */
  private int roomId;

  /**
   * {@link BossModel} constructor using an x and y coordinate.
   *
   * @param builder builder for BossModel
   */
  public BossModel(Builder builder) {
    super(builder.x, builder.y, builder.hitbox, builder.type, builder.frameSize);
    frameSize = builder.frameSize;
    moveAnimation = builder.moveAnimation;
    getHitAnimation = builder.getHitAnimation;
    falloverAnimation = builder.dieAnimation;
    this.filmStrip = shootAnimation;
    frameCounter = 1;
    frameDelay = builder.frameDelay;
    health = builder.health;
    deathCount = frameDelay * 16;
    roomId = builder.roomId;

    bodyKnockbackForce = 70f;
    spearKnockbackForce = 130f;
    healthThresholds = builder.healthThresholds;
    thresholdPointer = 0;
    setBodyType(BodyDef.BodyType.KinematicBody);
  }

  public void draw(RenderingEngine renderer) {
    int frame = getFrameNumber();
    if (isDead()) {
      filmStrip = falloverAnimation;
    } else {
      filmStrip = shootAnimation;
    }
    filmStrip.setFrame(frame);
    Vector2 pos = getPosition();
    renderer.draw(filmStrip, pos.x, pos.y, 0.16f);
    if (isDead()) {
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
        deathCount -= 1;
      } else {
        setFrameNumber(getFrameNumber());
        deathCount -= 1;
      }
    } else {
      if (frameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
      }
    }
    frameCounter += 1;
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

  public boolean isTerminated() {
    return deathCount <= 0;
  }

  /** Get remaining health points of the boss */
  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {this.health = health;}

  /** Returns the room this boss is in */
  public int getRoomId() { return roomId; }
  /**
   * Reduce boss HP by a specified amount
   * If the boss dies, mark boss as removed
   */
  public void decrementHealth(int damage) {
    health -= damage;
    if (isDead()) {
      filmStrip = falloverAnimation;
      //TODO: kinda hard-coded in right now, find a way to make body inactive
      setVX(0);
      setVY(0);
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
   * Returns if the boss's health reached under a certain
   * health threshold.
   *
   * @return True if the health threshold was reach, false otherwise
   */
  public boolean reachedHealthThreshold(){
    if (thresholdPointer < healthThresholds.length
            && health <= healthThresholds[thresholdPointer]) {
      thresholdPointer++;
      return true;
    } else {
      return false;
    }
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
    private FilmStrip dieAnimation;
    private FilmStrip shootAnimation;
    private FilmStrip idleAnimation;

    /** The number of frames between animation updates */
    private int frameDelay;

    /** Polygon indicating boss hitbox */
    private float[] hitbox;

    /** Number of health points the boss has */
    protected int health;

    /** Health threshold numbers */
    private int[] healthThresholds;

    /** ID for the room this boss is in */
    private int roomId;

    public static Builder newInstance() {
      return new Builder();
    }

    private Builder() {
    }

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

    public Builder setHitbox(String type) {
      float[] hitbox;
      switch (type) {
        case "crab":
          hitbox = new float[]{-4, -7, -4, 7, 4, 7, 4, -7};
          break;
        case "jelly":
          hitbox = new float[]{-3, -3, -3, 3,3, 3, 3, -3};
          break;
        default:
          hitbox = new float[]{-4, -7, -4, 7, 4, 7, 4, -7};
      }
      this.hitbox = hitbox;
      return this;
    }

    public Builder setHealth(int health) {
      this.health = health;
      return this;
    }

    public Builder setFrameSize(int frameSize) {
      this.frameSize = frameSize;
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
      ;
      return this;
    }

    public Builder setGetHitAnimation(Texture texture) {
      int width = texture.getWidth();
      getHitAnimation = new FilmStrip(texture, 1, width / frameSize);
      ;
      return this;
    }

    public Builder setMoveAnimation(Texture texture) {
      int width = texture.getWidth();
      moveAnimation = new FilmStrip(texture, 1, width / frameSize);
      ;
      return this;
    }

    public Builder setFalloverAnimation(Texture texture) {
      int width = texture.getWidth();
      // TODO: Stop hardcoding columns
      dieAnimation = new FilmStrip(texture, 1, 16);
      ;
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

    public BossModel build() {
      switch (type) {
        case "crab":
          return new CrabBossModel(this);
        case "jelly":
          return new JellyBossModel(this);
        // Should not get here
        default:
          return new CrabBossModel(this);
      }
    }
  }
}
