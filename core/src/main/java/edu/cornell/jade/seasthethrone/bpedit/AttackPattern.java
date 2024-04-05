package edu.cornell.jade.seasthethrone.bpedit;

import edu.cornell.jade.seasthethrone.util.PooledList;

/**
 * Maintains a timer and collection of {@link Spawner}s. This animates the
 * <code>Spawner</code>s as needed to create fun attacks.
 *
 * A way to conceptulize this is a single "attack" an enemy does. A boss may
 * have 4-10 of these and a regular enemy may only have 1.
 */
public abstract class AttackPattern {
  /** Timer to keep track of what bullets are to be spawned */
  protected int bulletTimer;

  /** A list of the bullet patterns to spawn */
  protected PooledList<Spawner> spawners;

  /**
   * Constructs a BulletController
   *
   * @param physicsEngine physicsEngine to modify when adding bullets
   */
  protected AttackPattern() {
    spawners = new PooledList<>();
    bulletTimer = 0;
  }

  /**
   * Increments the bulletTimer and spawns any new bullets which would need be
   * spawned.
   *
   * @param px player x coordinate
   * @param py player y coordinate
   */
  public void update(float px, float py) {
    animateStep();
    bulletTimer++;
    for (Spawner p : spawners) {
      p.update(px, py);
    }
  }

  /**
   * Modifies any bullet patterns, called during update.
   */
  abstract protected void animateStep();

  /**
   * Adds a bullet pattern to the bullet controller so the patterns bullets will
   * spawn.
   *
   * @param spawner spawner to add
   */
  protected void addSpawner(Spawner spawner) {
    spawners.add(spawner);
  }
}
