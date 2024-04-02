package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public abstract class BossModel extends PolygonModel implements Renderable {

  /** Number of frames in boss animation TODO: stop hardcoding animation */
  private int frameSize;

  private FilmStrip shootAnimation;
  private FilmStrip idleAnimation;
  private FilmStrip moveAnimation;
  private FilmStrip getHitAnimation;
  private FilmStrip dieAnimation;
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

  /**
   * {@link BossModel} constructor using an x and y coordinate.
   *
   * @param builder builder for BossModel
   */
  public BossModel(Builder builder) {
    super(builder.hitbox, builder.x, builder.y);
    frameSize = builder.frameSize;
    shootAnimation = builder.shootAnimation;
    idleAnimation = builder.idleAnimation;
    moveAnimation = builder.moveAnimation;
    getHitAnimation = builder.getHitAnimation;
    dieAnimation = builder.dieAnimation;
    this.filmStrip = shootAnimation;
    frameCounter = 1;
    frameDelay = builder.frameDelay;
    health = builder.health;
    deathCount = frameDelay * 16;

    bodyKnockbackForce = 70f;
    spearKnockbackForce = 130f;
    setBodyType(BodyDef.BodyType.StaticBody);
  }

  public void draw(RenderingEngine renderer) {
    int frame = getFrameNumber();
    FilmStrip filmStrip = getFilmStrip();
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

  /**
   * Reduce boss HP by a specified amount
   * If the boss dies, mark boss as removed
   */
  public void decrementHealth(int damage) {
    health -= damage;
    if (isDead()) {
      filmStrip = dieAnimation;
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

  public static class Builder {
    /** boss x position */
    private float x;
    /** boss y position */
    private float y;
    /** type of the boss (ie. crab, etc.) */
    private String type;

    /** Number of frames in boss animation */
    private int frameSize;

    private FilmStrip shootAnimation;
    private FilmStrip idleAnimation;
    private FilmStrip moveAnimation;
    private FilmStrip getHitAnimation;
    private FilmStrip dieAnimation;

    /** The number of frames between animation updates */
    private int frameDelay;

    /** Polygon indicating boss hitbox */
    private float[] hitbox;

    /** Number of health points the boss has */
    protected int health;

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

    public Builder setHitbox(float[] hitbox) {
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

    public Builder setDieAnimation(Texture texture) {
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

    public BossModel build() {
      switch (type) {
        case "crab":
          return new CrabBossModel(this);

        // Should not get here
        default:
          return new CrabBossModel(this);
      }
    }
  }
}
