package edu.cornell.jade.seasthethrone.bpedit;

import java.util.Iterator;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;

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
public class BulletPattern implements Iterator<BulletModel> {
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
    int timestamp;

    /** The base bullet model before any effects are applied. */
    BulletModel base;

    /** An ordered list of effects to be applied to the base bullet. */
    Queue<Effect> effect;

    /**
     * Constructs a <code>BulletFamily</code>
     *
     * @param base      the base bullet the family is constructed from
     * @param timestamp the time at which the base bullet should fire
     */
    public BulletFamily(BulletModel base, int timestamp) {
      super(-timestamp);
      this.timestamp = timestamp;
      this.base = base;
      this.effect = new Queue<Effect>();
    }

    /**
     * Sets the timestamp
     *
     * @param timestamp the time to set
     */
    public void setTimestamp(int timestamp) {
      this.timestamp = timestamp;
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
     * Sets the base
     *
     * @param base the base to set
     */
    public void setBase(BulletModel base) {
      this.base = base;
    }

    /**
     * Gets the base
     *
     * @return the base's timestamp
     */
    public BulletModel getBase() {
      return base;
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
     * @param basePool   the pool to obtain new bullet models from
     *
     * @return the new bullet family
     */

    public BulletFamily mostlyClone(Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
      BulletFamily newFamily = familyPool.obtain();
      for (Effect e : effect)
        newFamily.addEffect(e);
      newFamily.setTimestamp(timestamp);

      BulletModel newBase = basePool.obtain();
      newBase.setX(base.getX());
      newBase.setY(base.getY());
      newBase.setVX(base.getVX());
      newBase.setVY(base.getVY());
      newFamily.setBase(newBase);

      return newFamily;
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
    return curBullets.pop().base;
  }

  public boolean hasNext() {
    if (curBullets.isEmpty() || curBullets.peek().timestamp > timer)
      return false;
    BulletFamily p = curBullets.peek();
    while (!p.effect.isEmpty()) {
      curBullets.pop();

      bulletFamilyCache.add(p);
      Effect e = p.effect.removeFirst();
      e.apply(bulletFamilyCache, bulletFamilyPool, bulletBasePool);
      for (BulletFamily np : bulletFamilyCache)
        curBullets.add(np);

      if (curBullets.isEmpty() || curBullets.peek().timestamp > timer)
        return false;
      p = curBullets.peek();
    }

    return true;
  }
}
