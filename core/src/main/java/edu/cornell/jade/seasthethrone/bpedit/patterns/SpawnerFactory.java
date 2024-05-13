package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;

import edu.cornell.jade.seasthethrone.bpedit.BulletFamilyPool;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.BulletFamily;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModelPool;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.DelayedTarget;

/**
 * Contains factory methods for some common and even some not so common
 * {@link SpawnerFactory}.
 */
public final class SpawnerFactory {

  // FIXME: this is pretty evil just having static global pools here
  // really we should make these all non-static and then instantiate in gameplay
  // controller and pass stuff down
  public static BulletModelPool bulletModelPool = new BulletModelPool(100, Integer.MAX_VALUE);
  public static BulletFamilyPool bulletFamilyPool = new BulletFamilyPool(100, Integer.MAX_VALUE);

  /**
   * Constructs a single repeatedly shooting bullet whose origin
   * changes based on the tracked model.
   *
   * @param dups          number of bullets in the arc
   * @param delay         delay in between firings
   * @param vel           velocity of the bullet
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingRing(int dups, int delay, float vel,  BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(1f, 0f, vel, 0f, 0.5f, 0);
    f.addEffect(new Arc(0, MathUtils.PI * 2, dups));
    f.addEffect(new Periodic(delay));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a single repeatedly shooting bullet whose origin
   * changes based on the tracked model. It will for a time look to rotate
   * in one direction and then change direction.
   *
   * @param dups            number of bullets in the arc
   * @param delay           delay in between firings (should be less than
   *                        oscillation delay)
   * @param oscilationDelay delay in between changing firing directions
   * @param pauseTime       time inbetween last fired short of one direction and
   *                        first fired shot of other direction
   * @param sleepTime       time when no bullets are fired
   * @param builder         a builder to create bullet models
   * @param physicsEngine   {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructOscillatingRing(int dups, int delay, int oscilationDelay, int pauseTime, int sleepTime,
      BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    int ringsPerPeriod = oscilationDelay / dups;
    for (int i = 0; i <= ringsPerPeriod; i++) {
      BulletFamily f = new BulletFamily(1f, 0f, 5f, 5f, 0.5f, i * delay);
      f.addEffect(new Arc(0, MathUtils.PI * 2, dups));
      f.addEffect(new Periodic(2 * oscilationDelay + pauseTime + sleepTime));
      out.addFamily(f);
    }
    for (int i = 0; i <= ringsPerPeriod; i++) {
      BulletFamily f = new BulletFamily(1f, 0f, 5f, 5f, 0.5f, oscilationDelay + i * delay + pauseTime);
      f.addEffect(new Arc(0, MathUtils.PI * 2, dups));
      f.addEffect(new Periodic(2 * oscilationDelay + pauseTime + sleepTime));
      out.addFamily(f);
    }
    return out;
  }

  /**
   * Constructs a single repeatingly sohoting bullet always going in a certain
   * direction
   *
   * @param delay         delay inbetween firing
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingLeftFacingStream(int delay, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 14f, 0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a repeating stream going in both directions
   *
   * @param delay         delay in between firings
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingStream(int delay, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
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
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingAimedBullet(int delay, BossModel model, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 14f, -0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addEffect(new PlaysAttackAnimation(model));
    f.addDelayedAction(new DelayedTarget(0));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs an arc pointed to the bottom of the screen.
   *
   * @param dups          number of bullets in the arc
   * @param centralAngle  size of the arc in radians
   * @param delay         delay inbetween firings
   * @param model         model used to play animations
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingDownwardsFacingArc(int dups, float centralAngle, int delay, BossModel model,
      BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 0f, -8f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addEffect(new PlaysAttackAnimation(model));
    f.addEffect(new Arc(-MathUtils.PI / 6f, MathUtils.PI / 3f, 5));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs an arc pointed to the player
   *
   * @param dups          number of bullets in the arc
   * @param centralAngle  size of the arc in radians
   * @param delay         delay inbetween firings
   * @param model         model used to play animations
   * @param player        player to track
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingAimedArc(int dups, float centralAngle, int delay, BossModel model,
      PlayerModel player, BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 8f, 0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addEffect(new TargetsModel(out, player));
    f.addEffect(new PlaysAttackAnimation(model));
    f.addEffect(new Arc(-MathUtils.PI / 6f, centralAngle, dups));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a randomly spraying bullet stream of bullet aimed in a certain
   * direction
   *
   * @param angleRange    the full angle the spawner can spray in (radians)
   * @param delay         delay inbetween firing
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingRandomStream(float angleRange, int delay, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 14f, 0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addEffect(new RandomSpray(angleRange));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a ring of bullets that slowly spin around a moving origin
   *
   * @param dups          number of bullets in the ring
   * @param delay         the time it takes to make a full rotation
   * @param model         model to track
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructSpinningRing(int dups, int delay, BossModel model,
      BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(4.5f, 0f, 0f, 0f, 0.5f, 0);
    f.addEffect(new Arc(0f, MathUtils.PI * 2, dups));
    f.addDelayedAction(new Spawner.DelayedIndefiniteRotate(delay, model));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a randomly spraying bullet stream of bullet aimed in a certain
   * direction
   *
   * @param angleRange    the full angle the spawner can spray in (radians)
   * @param delay         delay inbetween firing
   * @param player        the player to track
   * @param model         the boss used to play animations
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingAimedRandomStream(float angleRange, int delay, PlayerModel player,
                                  BossModel model, BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 12f, 0f, 0.5f, 0);
    f.addEffect(new Periodic(delay));
    f.addEffect(new PlaysAttackAnimation(model));
    f.addEffect(new TargetsModel(out, player));
    f.addEffect(new RandomSpray(angleRange));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs an arc pointed to the player
   *
   * @param dups          number of bullets in the arc
   * @param centralAngle  size of the arc in radians
   * @param period        delay inbetween firings
   * @param delay         delay until homing on the player
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingDelayedTrackingArc(int dups, float centralAngle, float angle,
                                 int period, int delay, BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 10f, 0f, 0.5f, 0);
    f.addEffect(new Periodic(period));
    f.addEffect(new Arc(angle-centralAngle/2, centralAngle, dups));
    f.addDelayedAction(new DelayedTarget(delay));
    out.addFamily(f);
    return out;
  }

  /**
   * Constructs a ring with a delayed rotate
   *
   * @param dups          number of bullets in the arc
   * @param period        delay inbetween firings
   * @param delay         delay until rotating
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public static Spawner constructRepeatingDelayedRotateRing(int dups, int period, int delay,
                                                            BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner out = new Spawner(builder, physicsEngine, bulletFamilyPool, bulletModelPool);
    BulletFamily f = new BulletFamily(0f, 0f, 10f, 0f, 0.5f, 0);
    f.addEffect(new Periodic(period));
    f.addEffect(new Arc(0, MathUtils.PI*2, dups));
    f.addDelayedAction(new Spawner.DelayedVelocityRotate(MathUtils.PI/2, delay));
    out.addFamily(f);
    return out;
  }
}
