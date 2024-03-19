package edu.cornell.jade.seasthethrone.bpedit;

import java.util.Iterator;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import com.badlogic.gdx.math.MathUtils;

/**
 * Stores a bullet pattern. This is sourse of bullets which, at a given an
 * absolute time value, specifies the bullets which should be spawned as well as
 * their given locations. <code>BulletPattern</code>s implement {@link Iterator}
 * where <code>next</code> returns a bullet to be created as long as there
 * exists an uncreated bullet.
 *
 * Bullets are created by incrementing a bullet timer, an integer. At each time
 * step, a specified numbr of bullets should be attempted to be spawned.
 * Subsequent updates can be in the positive or negative direction, but updates
 * in the negative direction may be slower.
 *
 * By default, the <code>BulletPattern</code>'s internal timer is set to
 * <code>0</code>.
 */
public class BulletPattern {
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
      super(-timestamp);
      this.timestamp = timestamp;
      this.bx = bx;
      this.by = by;
      this.bvx = bvx;
      this.bvy = bvy;
      this.radius = radius;

      this.effect = new Queue<Effect>();
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

      res.effect = new Queue<Effect>();

      return res;
    }

    @Override
    public float getValue() {
      return -timestamp;
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
      return newFamily;
    }

    public BulletModel realizeBase(Pool<BulletModel> modelPool) {
      BulletModel m = BulletModel.construct(bx, by, radius, modelPool);
      m.setVX(bvx);
      m.setVY(bvy);
      return m;
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

  /** Cache array to apply effects */
  private Array<BulletFamily> bulletFamilyCache;

  /**
   * Constructs a <code>BulletPattern<code>.
   */
  public BulletPattern() {
    timer = 0;
    curBullets = new BinaryHeap<>();
    bulletFamilyCache = new Array<>();
    bulletFamilyPool = new ReflectionPool<>(BulletFamily.class);
    bulletBasePool = new ReflectionPool<>(BulletModel.class);
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
   * Updates the timer of the <code>BulletPattern</code>. This requires delta to
   * be positive and increments the timer by that delta.
   *
   * @param delta which time to set the internal timer
   */
  public void updateTime(int delta) {
    assert (delta > 0);
    timer += delta;
  }

  /**
   * Returns if the there is possibly another bullet which can be created
   *
   * @return if another bullet could possibly be created
   */
  public boolean doneCreating() {
    return curBullets.isEmpty();
  }

  public BulletModel next() {
    BulletFamily f = curBullets.pop();
    return f.realizeBase(bulletBasePool);
  }

  public boolean hasNext() {
    if (curBullets.isEmpty() || curBullets.peek().timestamp > timer)
      return false;
    BulletFamily p = curBullets.peek();
    while (!p.effect.isEmpty()) {
      BulletFamily clone = p.clone(bulletFamilyPool);
      clone.effect.removeFirst();
      bulletFamilyCache.add(clone);
      Effect e = p.effect.removeFirst();
      e.apply(bulletFamilyCache, bulletFamilyPool, bulletBasePool);
      for (BulletFamily np : bulletFamilyCache) {
        curBullets.add(np);
      }
      bulletFamilyCache.clear();

      if (curBullets.isEmpty() || curBullets.peek().timestamp > timer)
        return false;
      p = curBullets.peek();
    }

    return true;
  }
}
