package edu.cornell.jade.seasthethrone.bpedit.patterns;

import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.BulletFamily;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import com.badlogic.gdx.math.MathUtils;

/**
 * Contains factory methods for some common and even some not so common
 * {@link SpawnerFactory}.
 */
public final class SpawnerFactory {
  /**
   * Constructs an arc pointed to the bottom of the screen.
   *
   * @param dups          number of bullets in the arc
   * @param centralAngle  size of the arc in radians
   * @param delay         delay inbetween firings
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingDownwardsFacingArc(int dups, float centralAngle, int delay, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(physicsEngine);
    BulletFamily f = new BulletFamily(0f, 0f, 0f, -2f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addEffect(new Arc(0, MathUtils.PI / 3f, 5));
    out.addFamily(f);
    return out;
  }
}
