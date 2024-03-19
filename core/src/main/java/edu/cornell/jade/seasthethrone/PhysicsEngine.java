package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.util.PooledList;
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

  private Array<BossModel> bosses = new Array<>();

  public PhysicsEngine(Rectangle bounds, World world, PlayerModel player) {
    this.world = world;
    this.bounds = new Rectangle(bounds);
    world.setContactListener(this);
    addObject(player);
  }

  public PooledList<Model> getObjects() {
    return objects;
  }

  public void dispose() {
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
   */
  public void spawnBulletPattern(int bulletTimer) {
    for (BossModel boss : bosses) {
      float speed = 6;
      Vector2 bossPos = boss.getPosition();
      float theta = bulletTimer * 0.01f;
      Vector2 v_i = new Vector2((float) Math.cos(theta), (float) Math.sin(theta));

      BulletModel bullet1 = new BulletModel(bossPos.x, bossPos.y + 2, 0.5f);
      bullet1.setBodyType(BodyDef.BodyType.KinematicBody);
      bullet1.setVX(speed * v_i.x);
      bullet1.setVY(speed * v_i.y);
      addObject(bullet1);
      bullet1.createFixtures();

      BulletModel bullet2 = new BulletModel(bossPos.x, bossPos.y + 2, 0.5f);
      bullet2.setBodyType(BodyDef.BodyType.KinematicBody);
      bullet2.setVX(-speed * v_i.x);
      bullet2.setVY(-speed * v_i.y);
      addObject(bullet2);
      bullet2.createFixtures();
    }
  }

  /**
   * The core gameplay loop of this world.
   *
   * <p>This method is called after input is read, but before collisions are resolved. The very last
   * thing that it should do is apply forces to the appropriate objects.
   *
   * @param delta Number of seconds since last animation frame
   */
  public void update(float delta) {
    if (bulletTimer % 20 == 0) {
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
        if (obj instanceof BossModel) {
          bosses.removeValue((BossModel)obj, true);
        }
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
  public void addObject(Model obj) {
    assert inBounds(obj) : "Object is not in bounds";
    objects.add(obj);
    obj.activatePhysics(world);
    if (obj instanceof BossModel) {
      bosses.add((BossModel)obj);
    }
  }

  /**
   * Returns true if the object is in bounds.
   *
   * <p>This assertion is useful for debugging the physics.
   *
   * @param obj The object to check.
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
   * <p>This method is called when we first get a collision between two objects. We use this method
   * to test if it is the "right" kind of collision.
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

      if (bd1 instanceof PlayerBodyModel && bd2 instanceof BulletModel) {
        System.out.println("player hit");
        handleCollision((PlayerBodyModel) bd1, (BulletModel) bd2);
      } else if (bd2 instanceof PlayerBodyModel && bd1 instanceof BulletModel) {
        System.out.println("player hit");
        handleCollision((PlayerBodyModel) bd2, (BulletModel) bd1);
      } else if (bd1 instanceof PlayerSpearModel && bd2 instanceof BulletModel){
        bd2.markRemoved(true);
      } else if (bd2 instanceof PlayerSpearModel && bd1 instanceof BulletModel){
        bd1.markRemoved(true);
      }
      else if (bd1 instanceof PlayerShadowModel && bd2 instanceof BulletModel){}
      else if (bd2 instanceof PlayerShadowModel && bd1 instanceof BulletModel){}

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Handle collision between player body and bullet */
  public void handleCollision(PlayerBodyModel pb, BulletModel b){
    b.markRemoved(true);
    if(!pb.isInvincible()){
      pb.setHit(true);
      pb.setInvincible();
      // Calculate knockback direction
//      Vector2 knockbackDir = new Vector2(pb.getPosition()).sub(b.getPosition()).nor();
      // Apply knockback force
//      pb.getBody().applyLinearImpulse(knockbackDir.scl(b.getKnockbackForce()), pb.getCentroid(), false);
    }
  }

  public void handleCollision(PlayerShadowModel pb, BulletModel b) {
  }

  @Override
  public void endContact(Contact contact) {}

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {}

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {}
}
