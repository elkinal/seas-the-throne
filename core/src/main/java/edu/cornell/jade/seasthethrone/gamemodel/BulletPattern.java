package edu.cornell.jade.seasthethrone.gamemodel;

import java.util.Iterator;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

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
     * @param bullets the set of bullets to be effected by the effect
     * @param pool    an object pool to create new bullet families
     */
    public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> pool);
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
     * Adds an effect to be applied to the <code>base</code> at after previously
     * added effects have been applied.
     *
     * @param e the effect to be added
     */
    public void addEffect(Effect e) {
      effect.addLast(e);
    }
  }

  /** Object pool for allocating new bullet families */
  private Pool<BulletFamily> bulletFamilyPool;

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
      e.apply(bulletFamilyCache, bulletFamilyPool);
      for (BulletFamily np : bulletFamilyCache)
        curBullets.add(np);

      if (curBullets.isEmpty() || curBullets.peek().timestamp > timer)
        return false;
      p = curBullets.peek();
    }

    return true;
  }
}
