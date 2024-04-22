package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class SpiralToHomingAttack extends AttackPattern {
  /*
   * -------------------------------
   * CONSTANTS RELATED TO THE ATTACK
   * -------------------------------
   */

  private final float NEW_SPEED = 20f;

  /*
   * -------------------------------
   * STATE RELATED TO THE ATTACK
   * -------------------------------
   */

  /** the length of time between successive bullets */
  private final int delay;

  /** the angle (in radians) between each bullet */
  private final float angle;

  /** the current number of animation steps taken mod the period */
  private int stepsTaken;

  /** the spawner actually creating the arc */
  private final Spawner spawner;

  /** The model being tracked */
  private Model toTrack;

  /**
   * Constructs the attack
   *
   * @param toTrack       the model to track (to change the spawn point)
   * @param delay         the length of time between successive bullets
   * @param homingDelay   delay in between initial firing and homing change
   * @param shots         the number of bullets in one circular rotation
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public SpiralToHomingAttack(Model toTrack, int delay, int homingDelay, int shots,
      BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    this.delay = delay;
    this.angle = MathUtils.PI * 2f / shots;

    this.stepsTaken = 0;
    this.spawner = SpawnerFactory.constructTrackingHomingRepeatingBullet(delay, homingDelay, NEW_SPEED, builder,
        physicsEngine);
    this.spawner.translate(toTrack.getX(), toTrack.getY());
    this.toTrack = toTrack;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    this.spawner.moveSpawner(toTrack.getX(), toTrack.getY());
    if (stepsTaken == delay - 1) {
      spawner.rotates(angle);
      stepsTaken = 0;
    } else {
      stepsTaken++;
    }
  }
}
