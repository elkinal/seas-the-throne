package edu.cornell.jade.seasthethrone.bpedit;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * Stores a bullet pattern. This is sourse of bullets which, at a given an
 * absolute time value, specifies the bullets which should be spawned as well as
 * their given locations. <code>Spawner</code>s implement {@link Iterator}
 * where <code>next</code> returns a bullet to be created as long as there
 * exists an uncreated bullet.
 *
 * Bullets are created by incrementing a bullet timer, an integer. At each time
 * step, a specified numbr of bullets should be attempted to be spawned.
 * Subsequent updates can be in the positive or negative direction, but updates
 * in the negative direction may be slower.
 *
 * By default, the <code>Spaner</code>'s internal timer is set to
 * <code>0</code>.
 */
public class Spawner {
  /**
   * An effect on a bullet model. This describes a mapping from one set of bullets
   * to another set of bullets.
   */
  public static interface Effect {
    /**
     * Modifies an array of bullets. This is how effects are actually applied. If an
     * effect adds more bullets, for example, it will add more bullets to this
     * bullets array.
     *
     * @param bullets    the set of bullets to be effected by the effect
     * @param familyPool an object pool to create new bullet families
     * @param basePool   an object pool to create new base bullets
     */
    public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool);
  }

  /**
   * Represents a change to a bullet at a given timestamp.
   */
  public static abstract class DelayedAction extends BinaryHeap.Node {
    /**
     * Set a delay to say the action will never stop being applied.
     */
    public static int NEVER_REMOVE = Integer.MIN_VALUE;

    /**
     * The timestamp at which the action should be applied, relative to the bullet
     * families timestamp.
     */
    protected int delay;

    /** The bullet model to modify */
    protected BulletModel model;

    /**
     * Sets the bullet model to modify.
     *
     * @param model the model to modify
     */
    public void setModel(BulletModel model) {
      this.model = model;
    }

    /**
     * Constructs a delayed action
     *
     * @param delay timestamp at which an action should be applied relative to the
     *              bullet family's timestamp
     */
    public DelayedAction(int delay) {
      super(0);
      this.delay = delay;
    }

    /*
     * Given a delayed action, returns a new identical object
     */
    public abstract DelayedAction clone();

    /**
     * Applies the given effect to the bullet model or does nothing if the bullet
     * model is <code>null</code>.
     *
     * @param px x coordinate of the player
     * @param px y coordinate of the player
     */
    public abstract void apply(float px, float py);
  }

  /** Rotates a bullet around its origin at a specified timestamp. */
  public static final class DelayedVelocityRotate extends DelayedAction {
    /** Radians to rotate the old velocity by */
    float theta;

    /**
     * Constructs a DelayedVelocityChange
     *
     * @param theta the amount to rotate the old velocity by in radians
     * @param delay timestamp at which an action should be applied relative to the
     *              bullet family's timestamp
     *
     */
    public DelayedVelocityRotate(float theta, int delay) {
      super(delay);
      this.theta = theta;
    }

    @Override
    public DelayedVelocityRotate clone() {
      return new DelayedVelocityRotate(theta, delay);
    }

    @Override
    public void apply(float px, float py) {
      if (model == null)
        return;

      float cos = MathUtils.cos(theta);
      float sin = MathUtils.sin(theta);
      float vx = cos * model.getVX() - sin * model.getVY();
      float vy = sin * model.getVX() + cos * model.getVY();
      model.setVX(vx);
      model.setVY(vy);
    }
  }

  /** Rotates a bullet around its origin at a specified timestamp. */
  public static final class DelayedSpeedChange extends DelayedAction {
    /** new speed should be */
    float speed;

    /**
     * Constructs a DelayedSpeedChange
     *
     * @param speed new speed after the change
     * @param delay timestamp at which an action should be applied relative to the
     *              bullet family's timestamp
     *
     */
    public DelayedSpeedChange(float speed, int delay) {
      super(delay);
      this.speed = speed;
    }

    @Override
    public DelayedSpeedChange clone() {
      return new DelayedSpeedChange(speed, delay);
    }

    @Override
    public void apply(float px, float py) {
      if (model == null)
        return;

      float ovx = model.getVX();
      float ovy = model.getVY();
      float mag = (float) Math.sqrt(ovx * ovx + ovy * ovy);
      if (mag == 0)
        return;
      float s = speed / mag;
      float vx = model.getVX() * s;
      float vy = model.getVY() * s;
      model.setVX(vx);
      model.setVY(vy);
    }
  }

  /** Sets a bullets velocity towards a target at a given timestamp. */
  public static final class DelayedTarget extends DelayedAction {
    /**
     * Constructs a DelayedVelocityTarget.
     *
     * @param delay timestamp at which an action should be applied relative to the
     *              bullet family's timestamp
     *
     */
    public DelayedTarget(int delay) {
      super(delay);
    }

    @Override
    public DelayedTarget clone() {
      return new DelayedTarget(delay);
    }

    @Override
    public void apply(float px, float py) {
      if (model == null)
        return;

      float dx = px - model.getX();
      float dy = py - model.getY();

      float ovx = model.getVX();
      float ovy = model.getVY();
      float mag = (float) Math.sqrt(ovx * ovx + ovy * ovy);
      if ((Math.abs(dx) < MathUtils.FLOAT_ROUNDING_ERROR && Math.abs(dy) < MathUtils.FLOAT_ROUNDING_ERROR)
          || mag < MathUtils.FLOAT_ROUNDING_ERROR) {
        return;
      }
      float normScale = (float) Math.sqrt(dx * dx + dy * dy);
      dx /= normScale;
      dy /= normScale;

      dx *= mag;
      dy *= mag;
      model.setVX(dx);
      model.setVY(dy);
    }
  }

  /** Rotates a bullet around a model's origin indefinitely. */
  public static final class DelayedIndefiniteRotate extends DelayedAction {

    /** Angle to rotate by each tick, in radians */
    final float dtheta;

    /** Time to make one full rotation */
    float period;

    /** The angle of the bullet w.r.t the origin */
    float angle;

    /** The distance from the origin */
    float radius;

    /** The model to rotate around */
    Model origin;

    /**
     * Constructs a DelayedIndefiniteRotate
     *
     * @param period  the time it takes to make one full rotation in frame ticks
     * @param origin  the model to rotate around
     *
     */
    public DelayedIndefiniteRotate(float period, Model origin) {
      super(NEVER_REMOVE);
      this.period = period;
      this.origin = origin;
      this.dtheta = MathUtils.PI*2/period;
    }

    @Override
    public void setModel(BulletModel model) {
      super.setModel(model);
      this.radius = model.getPosition().dst(origin.getPosition());
      this.angle = MathUtils.atan2(model.getY()-origin.getY(), model.getX()-origin.getX());
    }

    @Override
    public DelayedIndefiniteRotate clone() {
      return new DelayedIndefiniteRotate(period, origin);
    }

    @Override
    public void apply(float px, float py) {
      if (model == null)
        return;
      System.out.println(model + " " + model.getPosition().dst(origin.getPosition()) + " " + radius);

      angle = (angle + dtheta) % (MathUtils.PI*2);
      float cos = MathUtils.cos(angle);
      float sin = MathUtils.sin(angle);
      model.setX(radius * cos + origin.getX());
      model.setY(radius * sin + origin.getY());
    }
  }

  /**
   * A list of events and base bullet model pair.
   */
  public static class BulletFamily extends BinaryHeap.Node {
    /**
     * The <code>BulletFamily</code>'s timestamp. If the bullet pattern's timer is
     * after this timestamp, then this bullet should be spawned.
     */
    protected int timestamp;

    /** The base bullet model x coordinate */
    protected float bx;
    /** The base bullet model y coordinate */
    protected float by;
    /** The base bullet model velocity x component */
    protected float bvx;
    /** The base bullet model velocity y component */
    protected float bvy;
    /** The radius of the constructed bullet */
    protected float radius;

    /** An ordered list of effects to be applied to the base bullet. */
    protected Queue<Effect> effect;

    /** A map from timestamps to lists of delayed actions */
    protected Array<DelayedAction> delayedActions;

    /**
     * Constructs a <code>BulletFamily</code>
     *
     * @param bx        the x coordinate of the base bullet
     * @param by        the y coordinate of the base bullet
     * @param bvx       the x component of the velocity vector of the base bullet
     * @param bvy       the y component of the velocity vector of the base bullet
     * @param radius    the raidus of the constructed bullet
     * @param timestamp the time at which the base bullet should fire
     */
    public BulletFamily(float bx, float by, float bvx, float bvy, float radius, int timestamp) {
      super(timestamp);
      this.timestamp = timestamp;
      this.bx = bx;
      this.by = by;
      this.bvx = bvx;
      this.bvy = bvy;
      this.radius = radius;

      this.effect = new Queue<Effect>();
      this.delayedActions = new Array<>();
    }

    /** A zero argument constructor for use with a reflection rool */
    public BulletFamily() {
      super(0);
    }

    /**
     * Constructs a <code>BulletFamily</code> using a pool for allocation.
     *
     * @param bx        the x coordinate of the base bullet
     * @param by        the y coordinate of the base bullet
     * @param bvx       the x component of the velocity vector of the base bullet
     * @param bvy       the y component of the velocity vector of the base bullet
     * @param radius    the raidus of the constructed bullet
     * @param timestamp the time at which the base bullet should fire
     */
    public static BulletFamily construct(float bx, float by, float bvx, float bvy, float radius, int timestamp,
        Pool<BulletFamily> pool) {
      BulletFamily res = pool.obtain();
      res.timestamp = timestamp;
      res.bx = bx;
      res.by = by;
      res.bvx = bvx;
      res.bvy = bvy;
      res.radius = radius;

      // TODO: remove this dynamic allocation in the render loop
      res.effect = new Queue<Effect>();
      res.delayedActions = new Array<>();

      return res;
    }

    /**
     * Sets the timestamp of a given bullet, after how many ticks the bullet will
     * spawn.
     *
     * @param timestamp new timestamp
     */
    public void setTimestamp(int timestamp) {
      this.timestamp = timestamp;
    }

    /**
     * Adds a delayed action to the bullet family. The action's timestamp should
     * always be greater than the bullet family's timestamp.
     *
     * @param action action to add
     */
    public void addDelayedAction(DelayedAction action) {
      delayedActions.add(action);
    }

    /**
     * Sets the velocity of a bullet to a given constant.
     *
     * @param vx x component of the velocity of the bullet
     * @param vy y component of the velocity of the bullet
     */
    public void setVelocity(float vx, float vy) {
      this.bvx = vx;
      this.bvy = vy;
    }

    /**
     * Sets the position of the base bullet
     *
     * @param x x component of the position
     * @param y y component of the position
     */
    public void setPosition(float x, float y) {
      this.bx = x;
      this.by = y;
    }

    /**
     * Translates the base bullet pattern by some x and y
     *
     * @param x x amount to translate by
     * @param y y amount to translate by
     */
    public void translate(float x, float y) {
      this.bx += x;
      this.by += y;
    }

    /**
     * Gets direction of velocity of <code>BulletFamily</code>.
     *
     * @return direction of veclocity of base bullet
     */
    public float getDir() {
      return MathUtils.atan(bvy / bvx);
    }

    /**
     * Rotate a base bullet around a specified point counter clockwise.
     *
     * @param angle the angle in radians to rotate the point counter clockwise
     * @param x     x coordinate of point to rotate around
     * @param y     y coordinate of point to rotate around
     */
    public void rotate(float angle, float x, float y) {
      float ox = this.bx - x;
      float oy = this.by - y;
      float cos = MathUtils.cos(angle);
      float sin = MathUtils.sin(angle);
      this.bx = ox * cos - oy * sin + x;
      this.by = ox * sin + oy * cos + y;
      float ovx = this.bvx;
      float ovy = this.bvy;
      this.bvx = ovx * cos - ovy * sin;
      this.bvy = ovx * sin + ovy * cos;
    }

    /**
     * Gets the timestamp
     *
     * @return the family's timestamp
     */
    public int getTimestamp() {
      return timestamp;
    }

    /**
     * Adds an effect to be applied to the <code>base</code> at after previously
     * added effects have been applied.
     *
     * @param e the effect to be added
     */
    public void addEffect(Effect e) {
      effect.addLast(e);
    }

    /**
     * Adds an effect to be applied to the <code>base</code> at before previously
     * added effects have been applied.
     *
     * @param e the effect to be added
     */
    public void prependEffect(Effect e) {
      effect.addFirst(e);
    }

    /**
     * Clones the bullet family, with the base also a clone with some subset of its
     * properties the same as its parent
     *
     * @param familyPool the pool to obtain new bullet families from
     *
     * @return the new bullet family
     */
    public BulletFamily clone(Pool<BulletFamily> familyPool) {
      BulletFamily newFamily = BulletFamily.construct(bx, by, bvx, bvy, radius, timestamp, familyPool);
      for (Effect e : effect)
        newFamily.addEffect(e);
      for (DelayedAction a : delayedActions)
        newFamily.addDelayedAction(a.clone());
      return newFamily;
    }

    @Override
    public String toString() {
      return "BulletFamily [bvx=" + bvx + ", bvy=" + bvy + ", bx=" + bx + ", by=" + by + ", delayedActions="
          + delayedActions + ", effect=" + effect + ", radius=" + radius + ", timestamp=" + timestamp + "]";
    }
  }

  /** Object pool for allocating new bullet families */
  private Pool<BulletFamily> bulletFamilyPool;

  /** Object pool for allocating new bullet families */
  private Pool<BulletModel> bulletBasePool;

  /** The current time to create bullets. */
  private int timer;

  /** The list of bullets in the pattern currently not created. */
  private BinaryHeap<BulletFamily> curBullets;

  /** The heap of delayed actions yet to have been applied. */
  private BinaryHeap<DelayedAction> delayedActions;

  /**
   * a list of delayed actions yet to have been applied. Invariant: the lists
   * contents should be the same as the heap
   */
  private ObjectSet<DelayedAction> delayedActionsList;

  /** Cache array to apply effects */
  private Array<BulletFamily> bulletFamilyCache;

  /** The current spawner x position */
  private float x;

  /** The current spawner y position */
  private float y;

  // We keep a reference to the physics engine so all garbage collection can be
  // delt with by the spawner. It creates a new dependency but hopefully will make
  // us not have to think about any memory logic.
  /** The physics engine to add constructed bullets to */
  private PhysicsEngine physicsEngine;

  /** The set of bullet models added to the physics engine */
  private ObjectSet<BulletModel> added;

  /** Builder for bullets */
  private BulletModel.Builder bulletBuilder;

  /**
   * The current rotation of the spawner. Rotations are relative to the spawner's
   * origin and measured in radians and applied before any translation.
   */
  private float rotation;

  /** If bullets from this spawner should be unbreakable */
  private boolean unbreakable;

  private final Array<DelayedAction> delayedActionCache;

  /**
   * Constructs a <code>Spawner<code>.
   *
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   * @param bulletBuilder a builder to create bullet models
   */
  public Spawner(BulletModel.Builder bulletBuilder, PhysicsEngine physicsEngine) {
    timer = 0;
    curBullets = new BinaryHeap<>();
    bulletFamilyCache = new Array<>();
    bulletFamilyPool = new ReflectionPool<>(BulletFamily.class);
    bulletBasePool = new ReflectionPool<>(BulletModel.class);
    x = 0;
    y = 0;
    this.physicsEngine = physicsEngine;
    this.added = new ObjectSet<>();
    this.delayedActions = new BinaryHeap<>();
    this.delayedActionsList = new ObjectSet<>();
    this.bulletBuilder = bulletBuilder;
    this.delayedActionCache = new Array<>();
  }

  /**
   * Constructs a <code>Spawner<code> at a given position.
   *
   * @param x             x coordinate of spawner
   * @param y             y coordinate of spawner
   * @param bulletBuilder a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public Spawner(float x, float y, BulletModel.Builder bulletBuilder, PhysicsEngine physicsEngine) {
    this(bulletBuilder, physicsEngine);
    this.x = x;
    this.y = y;
  }

  /**
   * Adds a {@link BulletFamily} to list of uncreated bullets
   *
   * @param family bullet family to add
   */
  public void addFamily(BulletFamily family) {
    curBullets.add(family);
  }

  /**
   * Updates the timer of the <code>Spawner</code> and performs any required
   * actions at the new time.
   *
   * @param px player x position
   * @param py player y position
   */
  public void update(float px, float py) {
    timer++;
    addBullets();
    applyDelayedActions(px, py);
  }

  /**
   * Applies all outstanding delayed actions.
   *
   * @param px player x position
   * @param py player y position
   */
  private void applyDelayedActions(float px, float py) {
    while (!delayedActions.isEmpty() && delayedActions.peek().getValue() < timer) {
      var act = delayedActions.pop();
      act.apply(px, py);
      if (act.delay == DelayedAction.NEVER_REMOVE)
        delayedActionCache.add(act);
    }
    for (var act : delayedActionCache)
      delayedActions.add(act);
    delayedActionCache.clear();
  }

  /**
   * Translates the spawner. All bullets spawned will now be translated by this
   * given offset
   *
   * @param dx change in x coordinate
   * @param dy change in x coordinate
   */
  public void translate(float dx, float dy) {
    x += dx;
    y += dy;
  }

  /**
   * Moves the spawner. All bullets spawned will now be moved by this
   * given offset
   *
   * @param x new x coordinate
   * @param y new y coordinate
   */
  public void moveSpawner(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Rotates the spawner around its origin. All bullets spawned will now be
   * rotated by this given offset. This is applied before any translation.
   *
   * @param theta change in rotation in radians
   */
  public void rotates(float theta) {
    rotation += theta;
  }

  /**
   * Rotates the spawner, so it will be set in given direction
   *
   * @param theta new facing angle in radians
   */
  public void setAngle(float theta) {
    rotation = theta;
  }

  /**
   * Makes all bullets coming from the spawner unbreakable
   */
  public void setUnbreakable() {
    unbreakable = true;
  }

  /**
   * Returns if the there is possibly another bullet which can be created
   *
   * @return if another bullet could possibly be created
   */
  public boolean doneCreating() {
    return curBullets.isEmpty();
  }

  /**
   * Marks all bullets far from a given position for removal.
   *
   * @param x x coordinate of the point compared to
   * @param y y coordinate of the point compared to
   * @param d distance at which a bullet surpassing this will be marked for
   *          removal
   */
  public void removeFarFrom(float x, float y, float d) {
    for (BulletModel b : added) {
      if (b.getPosition().dst(x, y) > d) {
        b.markRemoved(true);
      }
    }
  }

  /**
   * Marks all bullets for removal from the physics engine.
   */
  public void removeAll() {
    for (BulletModel b : added) {
      b.markRemoved(true);
    }
  }

  /**
   * Frees all bullets marked as removed. This should NEVER be called after a call
   * to {@link removeFarFrom} or {@link removeAll} without a step to the physics
   * engine in between.
   */
  public void freeRemovedBullets() {
    for (BulletModel b : added) {
      if (b.isRemoved()) {
        added.remove(b);
        // TODO: hopefully there are few enough delayed actions this is fine but
        // consider optimizing
        for (DelayedAction a : delayedActionsList) {
          if (a.model == b) {
            delayedActionsList.remove(a);
          }
        }
        bulletBasePool.free(b);
      }
    }
  }

  /**
   * Adds bullets which have yet to be added to the physicsEngine.
   */
  private void addBullets() {
    while (hasNext()) {
      BulletModel b = next();
      assert !Float.isNaN(b.getX()) && !Float.isNaN(b.getY());
      physicsEngine.addObject(b);
      added.add(b);
    }
  }

  /**
   * Returns x coordinate of the <code>Spawner</code>
   *
   * @return x corrdinate of the <code>Spawner</code>
   */
  public float getX() {
    return x;
  }

  /**
   * Returns y coordinate of the <code>Spawner</code>
   *
   * @return y corrdinate of the <code>Spawner</code>
   */
  public float getY() {
    return y;
  }

  /**
   * Returns a bullet created by the bullet pattern. Requires a bullet to exist to
   * be obtained.
   *
   * @return a bullet in the current pattern if one exists at the current
   *         timestamp.
   */
  private BulletModel next() {
    assert hasNext();
    BulletFamily f = curBullets.pop();
    f.rotate(rotation, 0, 0);
    bulletBuilder
        .setX(f.bx + x)
        .setY(f.by + y)
        .setVX(f.bvx)
        .setVY(f.bvy)
        .setRadius(f.radius);
    if (unbreakable) bulletBuilder.setType(BulletModel.Builder.Type.UNBREAKABLE);
    BulletModel m = bulletBuilder.build();
    bulletBuilder.setType(BulletModel.Builder.Type.DEFAULT);
    //Disabled pooling for now
    //BulletModel m = BulletModel.construct(bulletBuilder, bulletBasePool);

    for (DelayedAction a : f.delayedActions) {
      a.setModel(m);
      delayedActions.add(a, a.delay + f.timestamp);
      delayedActionsList.add(a);
    }

    bulletFamilyPool.free(f);
    return m;
  }

  /**
   * Returns if there is a bullet to return at the current timestamp.
   *
   * @return if there is a bullet to return at the current timestamp.
   */
  private boolean hasNext() {
    if (curBullets.isEmpty() || curBullets.peek().timestamp > timer)
      return false;
    BulletFamily p = curBullets.peek();
    while (!p.effect.isEmpty()) {
      curBullets.pop();
      BulletFamily clone = p.clone(bulletFamilyPool);
      clone.effect.removeFirst();
      bulletFamilyCache.add(clone);
      Effect e = p.effect.removeFirst();
      e.apply(bulletFamilyCache, bulletFamilyPool, bulletBasePool);
      for (BulletFamily np : bulletFamilyCache) {
        curBullets.add(np, np.getTimestamp());
      }
      bulletFamilyCache.clear();

      if (curBullets.isEmpty() || curBullets.peek().getTimestamp() > timer)
        return false;
      p = curBullets.peek();
    }

    return true;
  }
}
