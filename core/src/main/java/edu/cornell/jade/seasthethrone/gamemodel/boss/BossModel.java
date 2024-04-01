package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.jade.seasthethrone.gamemodel.EnemyModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public abstract class BossModel extends EnemyModel implements Renderable {

  /** Number of frames in boss animation */
  private int frameSize;

  /** Boss-unique move animation TODO: make left right up down filmstrips */
  private FilmStrip moveAnimation;

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

  /**
   * {@link BossModel} constructor using an x and y coordinate.
   *
   * @param builder builder for BossModel
   */
  public BossModel(Builder builder) {
    super(builder, builder.type, builder.frameSize);
    frameSize = builder.frameSize;
    moveAnimation = builder.moveAnimation;
    this.filmStrip = super.shootAnimation;
    frameCounter = 1;
    frameDelay = builder.frameDelay;
    health = builder.health;

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

    if (frameCounter % frameDelay == 0) {
      setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
    }
    frameCounter += 1;
  }

  public float getBodyKnockbackForce() {
    return bodyKnockbackForce;
  }

  public float getSpearKnockbackForce() {
    return spearKnockbackForce;
  }

  /** Get remaining health points of the boss */
  public int getHealth() {
    return health;
  }

  /** Reduce boss HP by a specified amount If the boss dies, mark boss as removed */
  public void decrementHealth(int damage) {
    health -= damage;
    if (health <= 0) {
      markRemoved(true);
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

    /** Type of the boss (ie. crab, etc.). Must be the same string as the boss name in the json. */
    private String type;

    /** Number of frames in boss animation */
    private int frameSize;

    /** Boss-unique move animation */
    private FilmStrip moveAnimation;

    /** The number of frames between animation updates */
    private int frameDelay;

    /** Polygon indicating boss hitbox */
    private float[] hitbox;

    /** Number of health points the boss has */
    protected int health;

    public static Builder newInstance() {
      return new Builder();
    }

    private Builder() {}

    public float getX() {
      return x;
    }

    public float getY() {
      return y;
    }

    public float[] getHitbox() {
      return hitbox;
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

    public Builder setMoveAnimation(Texture texture) {
      int width = texture.getWidth();
      moveAnimation = new FilmStrip(texture, 1, width / frameSize);
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
