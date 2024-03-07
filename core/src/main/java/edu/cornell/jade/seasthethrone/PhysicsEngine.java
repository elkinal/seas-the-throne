package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.util.PooledList;

import java.net.DatagramSocket;
import java.util.Iterator;
public class PhysicsEngine implements ContactListener {

  /** All the objects in the world. */
  protected PooledList<Model> objects = new PooledList<Model>();
  /** The Box2D world */
  private World world;
  /** The boundary of the world */
  private Rectangle bounds;
  /** Timer for spawning bullets */
  private int bulletTimer;

  public PhysicsEngine(Rectangle bounds, World world, PlayerModel player) {
    this.world = world;
    this.bounds = new Rectangle(bounds);
    this.bulletTimer = 0;
    world.setContactListener(this);
    addObject(player);
  }

  public PooledList<Model> getObjects(){
    return objects;
  }

  public void dispose(){
    objects.clear();
    world.dispose();
  }

  /**
   * @return the world of this physics engine.
   */
  public World getWorld() {
    return world;
  }

  /**
   * Spawns bullets at a set interval in a circular pattern
   *
   * @param bulletTimer value of the timer, used to set angle
   * */
  public void spawnBulletPattern(int bulletTimer) {
    float speed = 4;
    BulletModel bullet = new BulletModel(3,3,0.5f);
    float theta = bulletTimer * 2;
    Vector2 v_i = new Vector2((float)Math.cos(theta), (float)Math.sin(theta));
    bullet.setVX(speed * v_i.x);
    bullet.setVY(speed * v_i.y);
    addObject(bullet);
    bullet.createFixtures();
  }

  /**
   * The core gameplay loop of this world.
   *
   * This method is called after input is read, but before collisions are
   * resolved.
   * The very last thing that it should do is apply forces to the appropriate
   * objects.
   *
   * @param delta Number of seconds since last animation frame
   */
  public void update(float delta) {
    if (bulletTimer % 60 == 0) {
      spawnBulletPattern(bulletTimer);
    }
    bulletTimer += 1;

    // turn the physics engine crank
    world.step(delta, 8, 4);

    // Garbage collect the deleted objects.
    // Note how we use the linked list nodes to delete O(1) in place.
    // This is O(n) without copying.
    Iterator<PooledList<Model>.Entry> iterator = objects.entryIterator();
    while (iterator.hasNext()) {
      PooledList<Model>.Entry entry = iterator.next();
      Model obj = entry.getValue();
      if (obj.isRemoved()) {
        obj.deactivatePhysics(world);
        entry.remove();
      } else {
        obj.update(delta);
      }
    }
  }

  /**
   * Immediately adds the object to the physics world
   *
   * @param obj The object to add
   */
  protected void addObject(Model obj) {
    assert inBounds(obj) : "Object is not in bounds";
    objects.add(obj);
    obj.activatePhysics(world);
  }

  /**
   * Returns true if the object is in bounds.
   *
   * This assertion is useful for debugging the physics.
   *
   * @param obj The object to check.
   *
   * @return true if the object is in bounds.
   */
  public boolean inBounds(Model obj) {
    boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x + bounds.width);
    boolean vert = (bounds.y <= obj.getY() && obj.getY() <= bounds.y + bounds.height);
    return horiz && vert;
  }

  /**
   * Callback method for the start of a collision
   *
   * This method is called when we first get a collision between two objects.  We use
   * this method to test if it is the "right" kind of collision.
   *
   * @param contact The two bodies that collided
   */
  @Override
  public void beginContact(Contact contact) {
    Fixture fix1 = contact.getFixtureA();
    Fixture fix2 = contact.getFixtureB();

    Body body1 = fix1.getBody();
    Body body2 = fix2.getBody();

    Object fd1 = fix1.getUserData();
    Object fd2 = fix2.getUserData();

    try {
      Model bd1 = (Model) body1.getUserData();
      Model bd2 = (Model) body2.getUserData();

      if(bd1 instanceof PlayerBodyModel && bd2 instanceof BulletModel){
        PlayerBodyModel pm1 = (PlayerBodyModel) bd1;
        if(pm1.isDashing() && pm1.getPointSensorName().equals(fd1)){
          bd2.markRemoved(true);
        } else{
          bd1.markRemoved(true);
          contact.setEnabled(false); // Disable the collision response
        }
      }
      else if(bd2 instanceof PlayerBodyModel && bd1 instanceof BulletModel) {
        PlayerBodyModel pm2 = (PlayerBodyModel) bd2;
        if (pm2.isDashing() && pm2.getPointSensorName().equals(fd2)) {
          bd1.markRemoved(true);
        } else {
          bd2.markRemoved(true);
          contact.setEnabled(false); // Disable the collision response
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void endContact(Contact contact) {

  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {

  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {

  }

}
