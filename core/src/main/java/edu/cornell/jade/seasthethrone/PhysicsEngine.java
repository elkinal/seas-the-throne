package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.model.*;
import edu.cornell.jade.util.*;

import java.util.Iterator;
// DO NOT IMPORT GameplayController

public class PhysicsEngine implements ContactListener {

  /** All the objects in the world. */
  protected PooledList<Model> objects = new PooledList<Model>();
  /** The Box2D world */
  private World world;
  /** The boundary of the world */
  private Rectangle bounds;
  /** The world scale */
  private Vector2 scale;
  /** The player */
  private PlayerModel player;

  /** Timer for spawning bullets */
  private int bulletTimer;

  /** The time since the last dash */

  public PhysicsEngine(Rectangle bounds) {

    // not sure if its gonna make sense to have some concept of gravity
    world = new World(new Vector2(0, 0), false);
    this.bounds = new Rectangle(bounds);
    this.scale = new Vector2(1, 1);
    this.bulletTimer = 0;
    world.setContactListener(this);
  }

  /**
   * Returns player model.
   *
   * @return the player model in the physics engine
   */
  public PlayerModel getPlayerModel() {
    return player;
  }

  /**
   * Resets the status of the game so that we can play again.
   *
   * This method disposes of the world and creates a new one.
   */
  public void reset() {
    Vector2 gravity = new Vector2(world.getGravity());
    objects.clear();
    world.dispose();

    world = new World(gravity, false);
    world.setContactListener(this);
    setupWorld();
  }

  private void setupWorld() {
    player = new PlayerModel(0, 0);
    addObject(player);

//    BulletModel bullet = new BulletModel(3, 3, 0.5f);
//    addObject(bullet);
//    bullet.createFixtures();
  }

  /**
   * @return the world of this physics engine.
   */
  public World getWorld() {
    return world;
  }

  /**
   * Move in given direction based on offset
   *
   * @param x a value from -1 to 1 representing the percentage of movement speed
   *          to be at in the given direction
   * @param y a value from -1 to 1 representing the percentage of movement speed
   *          to be at in the given direction
   */
  public void setVelPercentages(float x, float y) {
    float mag = (x * x + y * y) / (float) Math.sqrt(2);
    // TODO: Change this to compare with some epsilong probably
    // this does techinically work though
    if (mag == 0f) {
      mag = 1;
    }
    float moveSpeed = player.getMoveSpeed();
    if (player.isDashing())
      moveSpeed *= 3;

    player.getPointModel().setVX(x * moveSpeed / mag);
    player.getPointModel().setVY(y * moveSpeed / mag);
  }

  /** Orients the player model based on their primary direction of movement */
  public void orientPlayer() {
    int dir = player.direction();

    switch (dir) {
      case 0:
        player.setAngle(0f);
        break;
      case 1:
        player.setAngle(180f);
        break;
      case 2:
        player.setAngle(90f);
        break;
      case 3:
        player.setAngle(-90f);
        break;
    }
  }


  /**
   * Begin dashing if possible
   */
  public void beginDashing() {
    if (player.canDash()) {
      player.setDashing(true);
      player.setDashCounter(player.getDashLength());
    }
  }

  /**
   * Spawns bullets at a set interval in a circular pattern
   *
   * @param bulletTimer value of the timer, used to set angle
   * */
  public void spawnBulletPattern(int bulletTimer) {
    float speed = 2;
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

    // Handle dashing
    if (player.isDashing()) {
      player.decrementDashCounter();
      if (player.getDashCounter() <= 0) {
        // exit dash
        player.setDashing(false);
        player.setDashCounter(player.getDashCooldownLimit());
      }
    } else {
      player.setDashCounter(Math.max(0, player.getDashCounter() - 1));
    }

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

      // See if we have skewered a bullet.
      if (player.isDashing()) {
        if (player.getPointSensorName().equals(fd2) && bd1.getName().equals("bullet")) {
          bd1.markRemoved(true);
        }
        if (player.getPointSensorName().equals(fd1) && bd2.getName().equals("bullet")) {
          bd2.markRemoved(true);
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
