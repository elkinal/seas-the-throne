package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import edu.cornell.jade.seasthethrone.model.SimpleModel;
import com.badlogic.gdx.utils.Pool;

/**
 * Model for the game bullet objects. This class extends {@link SimpleModel} to be represented as a
 * single body (with circular shape). This is subject to change later as we design bullets with more
 * complex shapes.
 *
 * <p>TODO: make this implement Fish Renderable
 */
public class BulletModel extends SimpleModel {

  /** Shape of the bullet, modeled as a circle */
  public CircleShape shape;

  /** Fixture to be attached to the body */
  private Fixture geometry;

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
    setBodyType(BodyDef.BodyType.DynamicBody);
    setName("bullet");
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
}
