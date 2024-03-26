package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import edu.cornell.jade.seasthethrone.model.SimpleModel;
import com.badlogic.gdx.utils.Pool;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
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


  /** Bullet texture */
  public static final Texture FISH_TEXTURE = new Texture("bullet/yellowfish_east.png");
  /** FilmStrip cache object */
  public FilmStrip filmStrip;
  /** Amount of knockback force applied to player on collision */
  private float knockbackForce;

  /** Amount of damage the player bullet inflicts (on bosses) */
  private int damage;

  /**
   * {@link BulletModel} constructor using an x and y coordinate & radius. NOTE: as of now, you must
   * call activatePhysics after constructing the BulletModel for it to be properly created.
   *
   * @param x The x-position for this bullet in world coordinates
   * @param y The y-position for this bullet in world coordinates
   * @param radius The radius of this bullet
   */
  public BulletModel(float x, float y, float radius) {
    super(x, y);
    shape = new CircleShape();
    shape.setRadius(radius);
    knockbackForce = 30f;
    setName("bullet");
    faceDirection = Direction.DOWN;
    filmStrip = new FilmStrip(FISH_TEXTURE, 1, 1);
    damage = 5;

    setBodyType(BodyDef.BodyType.DynamicBody);
  }

  /** Returns knockback force to apply to player on collision */
  public float getKnockbackForce() {
    return knockbackForce;
  }

  /** Returns amount of damage to inflict (on bosses) */
  public int getDamage() { return damage; }

  /**
   * {@link BulletModel} constructor using no arguments for compatability with pooling. NOTE: as of now, you must
   * call activatePhysics after constructing the BulletModel for it to be properly created.
   */
  public BulletModel() {
    super(0, 0);
    shape = new CircleShape();
    shape.setRadius(0);
    setName("bullet");
  }

  public static BulletModel construct(float x, float y, float radius, Pool<BulletModel> pool) {
    BulletModel res = pool.obtain();
    res.setX(x);
    res.setY(y);
    res.shape.setRadius(radius);
    res.setBodyType(BodyDef.BodyType.KinematicBody);
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

  public float angle(){
    float vx = getVX();
    float vy = getVY();
    if (vx == 0){
      if (vy>0)
        return (float) (0.5*Math.PI);
      else
        return (float) (-0.5*Math.PI);
    }
    else if (vx > 0)
      return (float) Math.atan(vy/vx);
    else
      return (float) Math.atan(vy/vx) + (float) (Math.PI);
  }

}
