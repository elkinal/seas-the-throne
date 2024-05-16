package edu.cornell.jade.seasthethrone.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.EnemyModel;
import edu.cornell.jade.seasthethrone.gamemodel.gate.GateSensorModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.*;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.util.PooledList;
import edu.cornell.jade.seasthethrone.BuildConfig;

import java.util.Iterator;
import java.util.Optional;

public class PhysicsEngine implements ContactListener {

  /** All the objects in the world. */
  protected PooledList<Model> objects = new PooledList<Model>();

  /** The Box2D world */
  private World world;

  /** The boundary of the world */
  private Rectangle bounds;

  private Array<EnemyModel> enemies = new Array<>();

  /**
   * Filepath to the JSON of the level to switch to. Should be null unless the player is on a portal
   */
  private String target;

  /** The location to spawn in the player when the level is loaded */
  private Vector2 spawnPoint;

  /** Flag to check if spear has made contact with boss in this frame
   * (due to a strange bug where collisions were counted twice in one frame?)
   */
  private boolean hasSpeared;

  /** To keep track of the continuous player-boss collision */
  private Optional<Contact> playerBossCollision;

  public PhysicsEngine(Rectangle bounds, World world) {
    this.world = world;
    this.bounds = new Rectangle(bounds);
    world.setContactListener(this);
    playerBossCollision = Optional.empty();
  }

  public PooledList<Model> getObjects() {
    return objects;
  }

  public Array<EnemyModel> getEnemies() {
    return enemies;
  }

  public void dispose() {
//    Iterator<PooledList<Model>.Entry> iterator = objects.entryIterator();
//    while (iterator.hasNext()) {
//      PooledList<Model>.Entry entry = iterator.next();
//      Model obj = entry.getValue();
//      obj.markRemoved(true);
//    }
    objects.clear();
    //    world.dispose();
  }

  /**
   * @return the world of this physics engine.
   */
  public World getWorld() {
    return world;
  }

  /**
   * Spawns a single bullet given a position and velocity (only used for the player right now)
   *
   * @param pos starting position of bullet
   * @param vel velocity of bullet
   * @param speed speed of bullet
   * @param builder the bullet model builder
   */
  public void spawnBullet(Vector2 pos, Vector2 vel, float speed, BulletModel.Builder builder) {
    BulletModel bullet = builder.setX(pos.x).setY(pos.y).setRadius(0.5f).build();
    bullet.setVX(speed * vel.x);
    bullet.setVY(speed * vel.y);
    addObject(bullet);
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
    // turn the physics engine crank
    world.step(delta, 8, 4);
    hasSpeared = false;
    // Garbage collect the deleted objects.
    // Note how we use the linked list nodes to delete O(1) in place.
    // This is O(n) without copying.
    Iterator<PooledList<Model>.Entry> iterator = objects.entryIterator();
    while (iterator.hasNext()) {
      PooledList<Model>.Entry entry = iterator.next();
      Model obj = entry.getValue();
      if (obj.isRemoved()) {
        obj.deactivatePhysics(world);
        if (obj instanceof EnemyModel) {
          enemies.removeValue((EnemyModel) obj, true);
        }
        entry.remove();
      } else {
        if (obj instanceof PlayerModel) {
          // Resolve knockback flag
          PlayerBodyModel body = ((PlayerModel) obj).getBodyModel();
          if (body.isJustKnocked()) {
            applyKnockback(body, body.getKnockingBodyPos(), body.getKnockbackForce());
            body.setJustKnocked(false);
          }
        }
        obj.update(delta);
      }
    }

    // Try to collide with the boss again (if player is not invincible)
    // I'm not a fan of this workaround but I couldn't figure anything else out
    if (!playerBossCollision.isEmpty() && playerBossCollision.get().isTouching()) {
      beginContact(playerBossCollision.get());
    }
  }

  /** Removes any models currently marked for removal */
  public void removeMarked() {
    // Garbage collect the deleted objects.
    // Note how we use the linked list nodes to delete O(1) in place.
    // This is O(n) without copying.
    Iterator<PooledList<Model>.Entry> iterator = objects.entryIterator();
    while (iterator.hasNext()) {
      PooledList<Model>.Entry entry = iterator.next();
      Model obj = entry.getValue();
      if (obj.isRemoved()) {
        obj.deactivatePhysics(world);
        if (obj instanceof EnemyModel) {
          enemies.removeValue((EnemyModel) obj, true);
        }
        entry.remove();
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
    if (obj instanceof EnemyModel) {
      enemies.add((EnemyModel) obj);
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

    if (fix1 == null || fix2 == null) {
      return;
    }

    Body body1 = fix1.getBody();
    Body body2 = fix2.getBody();

    Object fd1 = fix1.getUserData();
    Object fd2 = fix2.getUserData();

    try {
      Model bd1 = (Model) body1.getUserData();
      Model bd2 = (Model) body2.getUserData();

      if (bd1 instanceof PlayerBodyModel && bd2 instanceof BulletModel) {
        if (BuildConfig.DEBUG) System.out.println("player hit");

        handleCollision((PlayerBodyModel) bd1, (BulletModel) bd2, contact);
      } else if (bd2 instanceof PlayerBodyModel && bd1 instanceof BulletModel) {
        if (BuildConfig.DEBUG) System.out.println("player hit");

        handleCollision((PlayerBodyModel) bd2, (BulletModel) bd1, contact);
      } else if (bd1 instanceof PlayerSpearModel
          && bd2 instanceof BulletModel
          && !((BulletModel) bd2).isUnbreakable()) {
        handleCollision((PlayerSpearModel) bd1, (BulletModel) bd2);
      } else if (bd2 instanceof PlayerSpearModel
          && bd1 instanceof BulletModel
          && !((BulletModel) bd1).isUnbreakable()) {
        handleCollision((PlayerSpearModel) bd2, (BulletModel) bd1);
      } else if (bd1 instanceof PlayerSpearModel && bd2 instanceof BossModel) {
        handleCollision((PlayerSpearModel) bd1, (BossModel) bd2);
      } else if (bd2 instanceof PlayerSpearModel && bd1 instanceof BossModel) {
        handleCollision((PlayerSpearModel) bd2, (BossModel) bd1);
      } else if (bd1 instanceof PlayerBodyModel && bd2 instanceof BossModel) {
        handleCollision((PlayerBodyModel) bd1, (BossModel) bd2, contact);
      } else if (bd2 instanceof PlayerBodyModel && bd1 instanceof BossModel) {
        handleCollision((PlayerBodyModel) bd2, (BossModel) bd1, contact);
      } else if (bd1 instanceof PlayerBulletModel && bd2 instanceof BossModel) {
        handleCollision((PlayerBulletModel) bd1, (BossModel) bd2);
      } else if (bd2 instanceof PlayerBulletModel && bd1 instanceof BossModel) {
        handleCollision((PlayerBulletModel) bd2, (BossModel) bd1);
      }
      // Handle obstacles
      else if (bd1 instanceof BulletModel
          && bd2 instanceof ObstacleModel
          && !((BulletModel) bd1).isUnbreakable()) {
        bd1.markRemoved(true);
      } else if (bd2 instanceof BulletModel
          && bd1 instanceof ObstacleModel
          && !((BulletModel) bd2).isUnbreakable()) {
        bd2.markRemoved(true);
      }
      // Handle portal sensors
      else if (bd1 instanceof PortalModel && bd2 instanceof PlayerShadowModel) {
        if (BuildConfig.DEBUG)
          System.out.println("portal detected: " + ((PortalModel) bd1).requiredCheckpoint);

        if (((PortalModel) bd1).isActivated()) {
          setTarget(((PortalModel) bd1).getTarget());
          setSpawnPoint(((PortalModel) bd1).getPlayerLoc());
        }
      } else if (bd2 instanceof PortalModel && bd1 instanceof PlayerShadowModel) {
        if (BuildConfig.DEBUG)
          System.out.println("portal detected: " + ((PortalModel) bd2).requiredCheckpoint);

        if (((PortalModel) bd2).isActivated()) {
          setTarget(((PortalModel) bd2).getTarget());
          setSpawnPoint(((PortalModel) bd2).getPlayerLoc());
        }
      }
      // Handle gate sensors
      else if (bd1 instanceof PlayerShadowModel && bd2 instanceof GateSensorModel) {
        if (BuildConfig.DEBUG) System.out.println("gate detected");

        ((GateSensorModel) bd2).setRaised(true);
      } else if (bd2 instanceof PlayerShadowModel && bd1 instanceof GateSensorModel) {
        if (BuildConfig.DEBUG) System.out.println("gate detected");

        ((GateSensorModel) bd1).setRaised(true);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Sets the target for screen transition */
  public void setTarget(String target) {
    this.target = target;
  }

  public String getTarget() {
    return target;
  }

  public void setSpawnPoint(Vector2 spawn) {
    this.spawnPoint = spawn;
  }

  public Vector2 getSpawnPoint() {
    return spawnPoint;
  }

  public boolean hasTarget() {
    return this.target != null;
  }

  /** Helper function to apply a knockback on the player body. */
  public void applyKnockback(PlayerBodyModel pb, Vector2 bd2Pos, float knockbackForce) {

    // Calculate knockback direction
    Vector2 knockbackDir = pb.getPosition().sub(bd2Pos).nor();
    // Apply knockback force
    pb.getBody().setLinearVelocity(0, 0);
    pb.getBody().applyLinearImpulse(knockbackDir.scl(knockbackForce), pb.getCentroid(), false);
  }

  /** Handle collision between player body and bullet */
  public void handleCollision(PlayerBodyModel pb, BulletModel b, Contact c) {
    if (!(b.isUnbreakable())) {
      b.markRemoved(true);
      if (pb.isInvincible()) {
        c.setEnabled(false);
        return;
      }
    }
    pb.setKnockedBack(b.getPosition(), b.getKnockbackForce(), 7);
    if (pb.isInvincible() && pb.isHit() && b.isUnbreakable()) return;
    pb.decrementHealth();
    pb.setInvincible(pb.getHitIFrames());
    pb.setHit(pb.getHitIFrames());
    pb.setStopDashing(true);
  }

  /** Handle collision between player spear and bullet */
  public void handleCollision(PlayerSpearModel ps, BulletModel b) {
    ps.incrementSpear();
    b.markRemoved(true);
  }

  /** Handle collision between player body and boss */
  public void handleCollision(PlayerBodyModel pb, BossModel b, Contact c) {
    if (BuildConfig.DEBUG) System.out.println("player invincible: " + pb.isInvincible());

    if (!pb.isInvincible() && !b.isDead()) {
      pb.decrementHealth();
      pb.setInvincible(pb.getHitIFrames());
      pb.setHit(pb.getHitIFrames());
      pb.setKnockedBack(b.getPosition(), b.getBodyKnockbackForce(), 7);
      pb.setStopDashing(true);
      playerBossCollision = Optional.empty();
    } else {
      if (playerBossCollision.isEmpty() && pb.getHealth() > 0) {
        playerBossCollision = Optional.of(c);
      }
    }
  }

  /** Handle collision between player spear and boss */
  public void handleCollision(PlayerSpearModel ps, BossModel b) {
    if(!b.isDead() && !hasSpeared){
      hasSpeared = true;

      b.decrementHealth(ps.getDamage());
      ps.getMainBody().setKnockedBack(b.getPosition(), b.getSpearKnockbackForce(), 15);

      // Disable spear collision again so it doesn't double count
      ps.markInactive();
    }
  }

  /** Handle collision between player bullet and boss */
  public void handleCollision(PlayerBulletModel pb, BossModel b) {
    b.decrementHealth(pb.getDamage());
    pb.markRemoved(true);
  }

  @Override
  public void endContact(Contact contact) {}

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {}

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {}
}
