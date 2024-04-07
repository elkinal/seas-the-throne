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

  /** Distance to render bullets from */
  private static float RENDER_DISTANCE = 100f;

  /** If the attack pattern has been cleaned up to be removed */
  private boolean cleanedUp;

  /** A list of the bullet patterns to spawn */
  protected PooledList<Spawner> spawners;

  /**
   * Constructs an AttackPattern
   */
  protected AttackPattern() {
    spawners = new PooledList<>();
    cleanedUp = false;
  }

  /**
   * Increments the bulletTimer and spawns any new bullets which would need be
   * spawned.
   *
   * @param px player x coordinate
   * @param py player y coordinate
   */
  public void update(float px, float py) {
    if (cleanedUp)
      return;

    animateStep();
    for (Spawner p : spawners) {
      p.update(px, py);
      p.removeFarFrom(px, py, RENDER_DISTANCE);
    }
  }

  /**
   * Cleanup the attack pattern before it is dropped. This should always have a
   * physics engine step between it an a subsequent update.
   */
  public void cleanup() {
    for (Spawner s : spawners) {
      s.removeAll();
    }
    cleanedUp = true;
  }

  /**
   * Frees any removed models. This should be called after a physics engine
   * update.
   */
  public void freeRemoved() {
    for (Spawner s : spawners) {
      s.freeRemovedBullets();
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
