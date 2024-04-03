package edu.cornell.jade.seasthethrone.bpedit;

import edu.cornell.jade.seasthethrone.util.PooledList;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;

/**
 * Maintains a timer and adds bullets to the physics engine based on a series of
 * bullet patterns and the timer.
 */
public class BulletController {
  /** Timer to keep track of what bullets are to be spawned */
  int bulletTimer;

  /** A list of the bullet patterns to spawn */
  PooledList<BulletPattern> patterns;

  /** The physics engine which to add models to */
  PhysicsEngine physicsEngine;

  /**
   * Constructs a BulletController
   *
   * @param physicsEngine physicsEngine to modify when adding bullets
   */
  public BulletController(PhysicsEngine physicsEngine) {
    patterns = new PooledList<>();
    bulletTimer = 0;
    this.physicsEngine = physicsEngine;
  }

  /**
   * Increments the bulletTimer and spawns any new bullets which would need be
   * spawned.
   */
  public void update(int delta) {
    bulletTimer++;
    for (BulletPattern p : patterns) {
      p.updateTime(delta);
    }
    for (BulletPattern p : patterns) {
      while (p.hasNext()) {
        BulletModel b = p.next();
        CollisionMask.setCategoryMaskBits(b);
        physicsEngine.addObject(b);
        b.createFixtures();
      }
      // p will never spawn another bullet so remove it
      if (p.doneCreating()) {
        patterns.remove(p);
      }
    }
  }

  /**
   * Adds a bullet pattern to the bullet controller so the patterns bullets will
   * spawn.
   *
   * @param bulletPattern bullet pattern to add
   */
  public void addPattern(BulletPattern bulletPattern) {
    patterns.add(bulletPattern);
  }
}
