package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;

import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.BulletFamily;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.DelayedTarget;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.DelayedSpeedChange;

/**
 * Contains factory methods for some common and even some not so common
 * {@link SpawnerFactory}.
 */
public final class SpawnerFactory {

  /**
   * Constructs a single repeatedly shooting bullet whose origin
   * changes based on the tracked model.
   *
   * @param dups          number of bullets in the arc
   * @param delay         delay in between firings
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingRing(int dups, int delay, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine);
    BulletFamily f = new BulletFamily(1f, 0f, 8f, 0f, 0.5f, 0);
    f.addEffect(new Arc(0, MathUtils.PI * 2, dups));
    f.addEffect(new Periodic(delay));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a single repeatedly shooting bullet whose origin
   * changes based on the tracked model.
   *
   * @param delay         delay in between firings
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructTrackingRepeatingBullet(int delay, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine);
    BulletFamily f = new BulletFamily(-1f, 0f, -8f, -0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    out.addFamily(f);

    f = new BulletFamily(1f, 0f, 8f, -0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a single repeatedly shooting bullet whose origin changes based on
   * the tracked model which will reangle to home in on the player after a given
   * amount of time.
   *
   * @param delay         delay in between firings
   * @param homingDelay   delay in between initial firing and homing change
   * @param newSpeed      speed after homing occurs
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructTrackingHomingRepeatingBullet(int delay, int homingDelay, float newSpeed,
      BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine);
    BulletFamily f = new BulletFamily(-1f, 0f, -8f, -0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addDelayedAction(new DelayedTarget(homingDelay));
    f.addDelayedAction(new DelayedSpeedChange(newSpeed, homingDelay));
    out.addFamily(f);

    f = new BulletFamily(1f, 0f, 8f, -0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addDelayedAction(new DelayedTarget(homingDelay));
    f.addDelayedAction(new DelayedSpeedChange(newSpeed, homingDelay));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs an arc pointed to the bottom of the screen.
   *
   * @param dups          number of bullets in the arc
   * @param centralAngle  size of the arc in radians
   * @param delay         delay inbetween firings
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingDownwardsFacingArc(int dups, float centralAngle, int delay,
      BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine);
    BulletFamily f = new BulletFamily(0f, 0f, 0f, -8f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addEffect(new Arc(-MathUtils.PI / 6f, MathUtils.PI / 3f, 5));
    out.addFamily(f);
    return out;
  }
}
