package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerBulletModel;
import edu.cornell.jade.seasthethrone.model.SimpleModel;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/**
 * Model for the game bullet objects. This class extends {@link SimpleModel} to be represented as a
 * single body (with circular shape). This is subject to change later as we design bullets with more
 * complex shapes.
 *
 * <p>TODO: make this implement Fish Renderable
 */
public class BulletModel extends SimpleModel implements Renderable, Poolable {
  /** Shape of the bullet, modeled as a circle */
  public CircleShape shape;

  /** Fixture to be attached to the body */
  private Fixture geometry;

  /** Bullet texture */
  public Texture fishTexture;

  /** FilmStrip cache object */
  public FilmStrip filmStrip;

  /** Amount of knockback force applied to player on collision */
  private float knockbackForce;

  /** If the bullet is unbreakable */
  private boolean isUnbreakable;

  /**
   * {@link BulletModel} constructor using an x and y coordinate & radius. NOTE: as of now, you must
   * call activatePhysics after constructing the BulletModel for it to be properly created.
   *
   * @param builder The builder for BulletModel
   */
  public BulletModel(Builder builder) {
    super(builder.x, builder.y);
    setVX(builder.vx);
    setVY(builder.vy);
    shape = new CircleShape();
    shape.setRadius(builder.radius);
    knockbackForce = 20f;
    setBodyType(BodyDef.BodyType.DynamicBody);
    fishTexture = builder.type == Builder.Type.UNBREAKABLE ? builder.UNBREAKABLE_TEXTURE : builder.BASE_TEXTURE;
    filmStrip = new FilmStrip(fishTexture, 1, 1);
    isUnbreakable = builder.type == Builder.Type.UNBREAKABLE;
  }

  /** Returns knockback force to apply to player on collision */
  public float getKnockbackForce() {
    return knockbackForce;
  }

  public boolean isUnbreakable() { return isUnbreakable; }

  /**
   * {@link BulletModel} constructor using no arguments for compatability with pooling. NOTE: as of
   * now, you must call activatePhysics after constructing the BulletModel for it to be properly
   * created.
   */
  public BulletModel() {
    super(0, 0);
  }

  public static BulletModel construct(Builder builder, Pool<BulletModel> pool) {
    BulletModel res = pool.obtain();
    res.setX(builder.x);
    res.setY(builder.y);
    res.setVX(builder.vx);
    res.setVY(builder.vy);
    res.shape.setRadius(builder.radius);
    res.setBodyType(BodyDef.BodyType.DynamicBody);
    res.knockbackForce = 20f;
    res.fishTexture = builder.type == Builder.Type.UNBREAKABLE
            ? builder.UNBREAKABLE_TEXTURE : builder.BASE_TEXTURE;
    res.filmStrip.setTexture(res.fishTexture);
    res.filmStrip.setRegion(0, 0, res.fishTexture.getWidth(), res.fishTexture.getHeight());
    res.isUnbreakable = builder.type == Builder.Type.UNBREAKABLE;
    res.markRemoved(false);
    return res;
  }

  /**
   * Create new fixtures for this body, defining the shape
   *
   * <p>This is the primary method to override for custom physics objects
   */
  public void createFixtures() {
    if (body == null) {
      return;
    }

    releaseFixtures();

    // Create the fixture
    fixture.shape = shape;
    geometry = body.createFixture(fixture);
    markDirty(false);
    // Making unbreakable bullets essentially unmovable
    if (isUnbreakable) {
      setMass(1000000f);
      setDensity(100000f);
    }
  }

  /**
   * Release the fixtures for this body, reseting the shape
   *
   * <p>This is the primary method to override for custom physics objects
   */
  public void releaseFixtures() {
    if (geometry != null) {
      body.destroyFixture(geometry);
      geometry = null;
    }
  }

  @Override
  public void draw(RenderingEngine renderer) {
    int frame = getFrameNumber();
    FilmStrip filmStrip = getFilmStrip();
    filmStrip.setFrame(frame);

    Vector2 pos = getPosition();

    renderer.draw(filmStrip, pos.x, pos.y, true, angle());
  }

  @Override
  public void progressFrame() {
  }

  @Override
  public void alwaysUpdate() {}

  @Override
  public void neverUpdate() {}

  @Override
  public void setAlwaysAnimate(boolean animate) {
    //    alwaysAnimate = animate;
  }

  @Override
  public boolean alwaysAnimate() {
    return false;
  }

  public int getFrameNumber() {
    return 0;
  }

  public void setFrameNumber(int frameNumber) {}

  public FilmStrip getFilmStrip() {
    return filmStrip;
  }

  public int getFramesInAnimation() {
    return 0;
  }

  public float angle() {
    float vx = getVX();
    float vy = getVY();
    if (vx == 0) {
      if (vy > 0) return (float) (0.5 * Math.PI);
      else return (float) (-0.5 * Math.PI);
    } else if (vx > 0) return (float) Math.atan(vy / vx);
    else return (float) Math.atan(vy / vx) + (float) (Math.PI);
  }

  @Override
  public void reset() {
  }

  public static class Builder {
    /** the type of bullet */
    public static enum Type{
      /** A normal, breakable enemy bullet */
      DEFAULT,
      /** A bullet shot by the player */
      PLAYER,
      /** An unbreakable enemy bullet */
      UNBREAKABLE
    }

    /** bullet x position */
    private float x;

    /** bullet y position */
    private float y;

    /** bullet velocity x component */
    private float vx;

    /** bullet velocity y component */
    private float vy;

    /** Texture for base bullet */
    private Texture BASE_TEXTURE;

    /** Texture for unbreakable bullet */
    private Texture UNBREAKABLE_TEXTURE;

    /** Radius of shape of bullet */
    private float radius;

    /** Whether the bullet has been shot by player */
    private boolean shotByPlayer;

    /** The type of the bullet */
    private Type type;

    public static Builder newInstance() {
      return new Builder();
    }

    private Builder() { this.type = Type.DEFAULT; }

    public Builder setBaseTexture(Texture texture) {
      BASE_TEXTURE = texture;
      return this;
    }

    public Builder setUnbreakableTexture(Texture texture) {
      UNBREAKABLE_TEXTURE = texture;
      return this;
    }

    public Builder setRadius(float radius) {
      this.radius = radius;
      return this;
    }

    public Builder setX(float x) {
      this.x = x;
      return this;
    }

    public Builder setY(float y) {
      this.y = y;
      return this;
    }

    public Builder setVX(float vx) {
      this.vx = vx;
      return this;
    }

    public Builder setVY(float vy) {
      this.vy = vy;
      return this;
    }

    public Builder setType(Type type) {
      this.type = type;
      return this;
    }

    public BulletModel build() {
      switch (type) {
        case PLAYER:
          return new PlayerBulletModel(this);
        default:
          return new BulletModel(this);
      }
    }
  }
}
