package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import edu.cornell.jade.seasthethrone.model.SimpleModel;
import com.badlogic.gdx.utils.Pool;
import edu.cornell.jade.seasthethrone.render.FishRenderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/**
 * Model for the game bullet objects. This class extends {@link SimpleModel} to be represented as a
 * single body (with circular shape). This is subject to change later as we design bullets with more
 * complex shapes.
 *
 * <p>TODO: make this implement Fish Renderable
 */
public class BulletModel extends SimpleModel implements FishRenderable {

  /** Shape of the bullet, modeled as a circle */
  public CircleShape shape;

  /** Fixture to be attached to the body */
  private Fixture geometry;

  /** Direction bullet is facing*/
  private Direction faceDirection;

  /** Bullet texture when facing north */
  public static final Texture FISH_TEXTURE_NORTH = new Texture("bullet/yellowfish_north.png");
  /** Bullet texture when facing northeast */
  public static final Texture FISH_TEXTURE_NE = new Texture("bullet/yellowfish_NE.png");
  /** Bullet texture when facing northwest */
  public static final Texture FISH_TEXTURE_NW = new Texture("bullet/yellowfish_NW.png");
  /** Bullet texture when facing east */
  public static final Texture FISH_TEXTURE_EAST = new Texture("bullet/yellowfish_east.png");
  /** Bullet texture when facing west */
  public static final Texture FISH_TEXTURE_WEST = new Texture("bullet/yellowfish_west.png");
  /** Bullet texture when facing southwest */
  public static final Texture FISH_TEXTURE_SW = new Texture("bullet/yellowfish_SW.png");
  /** Bullet texture when facing southeast */
  public static final Texture FISH_TEXTURE_SE = new Texture("bullet/yellowfish_SE.png");
  /** Bullet texture when facing south */
  public static final Texture FISH_TEXTURE_SOUTH = new Texture("bullet/yellowfish_south.png");
  /** FilmStrip cache object */
  public FilmStrip filmStrip;
  /** Amount of knockback force applied to player on collision */
  private float knockbackForce;

  /**
   * {@link BulletModel} constructor using an x and y coordinate & radius. NOTE: as of now, you must
   * call activatePhysics then createFixtures after constructing the BulletModel for it to be
   * properly created.
   *
   * @param x The x-position for this bullet in world coordinates
   * @param y The y-position for this bullet in world coordinates
   * @param radius The radius of this bullet
   */
  public BulletModel(float x, float y, float radius) {
    super(x, y);
    shape = new CircleShape();
    shape.setRadius(radius);
    knockbackForce = 15f;
    setBodyType(BodyDef.BodyType.KinematicBody);
    setName("bullet");
    faceDirection = Direction.DOWN;
    filmStrip = new FilmStrip(FISH_TEXTURE_SOUTH, 1, 1);
  }

  /** Returns knockback force to apply to player on collision */
  public float getKnockbackForce() {
    return knockbackForce;
  }

  /**
   * {@link BulletModel} constructor using no arguments for compatability with pooling. NOTE: as of now, you must
   * call activatePhysics then createFixtures after constructing the BulletModel for it to be
   * properly created.
   */
  public BulletModel() {
    super(0, 0);
    shape = new CircleShape();
    shape.setRadius(0);
    setBodyType(BodyDef.BodyType.DynamicBody);
    setName("bullet");
  }

  public static BulletModel construct(float x, float y, float radius, Pool<BulletModel> pool) {
    BulletModel res = pool.obtain();
    res.setX(x);
    res.setY(y);
    res.shape.setRadius(radius);
    res.setBodyType(BodyDef.BodyType.DynamicBody);
    res.setName("bullet");
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
    FishRenderable.super.draw(renderer);
  }

  @Override
  public int getFrameNumber() {
    return 0;
  }

  @Override
  public void setFrameNumber(int frameNumber) {

  }

  @Override
  public FilmStrip getFilmStrip() {
    return filmStrip;
  }

  @Override
  public int getFramesInAnimation() {
    return 0;
  }

  @Override
  public Texture getTextureNorth() {
    return FISH_TEXTURE_NORTH;
  }

  @Override
  public Texture getTextureNorthEast() {
    return FISH_TEXTURE_NE;
  }

  @Override
  public Texture getTextureNorthWest() {
    return FISH_TEXTURE_NW;
  }

  @Override
  public Texture getTextureWest() {
    return FISH_TEXTURE_WEST;
  }

  @Override
  public Texture getTextureEast() {
    return FISH_TEXTURE_EAST;
  }

  @Override
  public Texture getTextureSouthEast() {
    return FISH_TEXTURE_SE;
  }

  @Override
  public Texture getTextureSouthWest() {
    return FISH_TEXTURE_SW;
  }

  @Override
  public Texture getTextureSouth() {
    return FISH_TEXTURE_SOUTH;
  }

  @Override
  public Direction direction() {
    float vx = getVX();
    float vy = getVY();

    if (Math.abs(vx) > Math.abs(vy)) {
      boolean notDiagonalX = 0.52 * Math.abs(vx) > Math.abs(vy);
      if (vx > 0) {
        if (notDiagonalX)
          faceDirection = Direction.RIGHT;
        else if (vy>0)
          faceDirection = Direction.NE;
        else
          faceDirection = Direction.SE;
      }
      else {
        if (notDiagonalX)
          faceDirection = Direction.LEFT;
        else if (vy>0)
          faceDirection = Direction.NW;
        else
          faceDirection = Direction.SW;
      }
    } else if (Math.abs(vx) < Math.abs(vy)){
      boolean notDiagonalY = 0.52 * Math.abs(vy) > Math.abs(vx);
      if (vy > 0) {
        if (notDiagonalY)
          faceDirection = Direction.UP;
        else if (vx>0)
          faceDirection = Direction.NE;
        else
          faceDirection = Direction.NW;
      }
      else {
        if (notDiagonalY)
          faceDirection = Direction.DOWN;
        else if (vx>0)
          faceDirection = Direction.SE;
        else
          faceDirection = Direction.SW;
      }
    }
    return faceDirection;
  }

}
