package edu.cornell.jade.seasthethrone.bpedit.patterns;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import com.badlogic.gdx.math.MathUtils;

public final class ArcsAcrossTheTopAttack extends AttackPattern {
  /*
   * -------------------------------
   * CONSTANTS RELATED TO THE ATTACK
   * -------------------------------
   */

  /** The angle of the arc of a shot */
  public static float CENTRAL_ANGLE = MathUtils.PI / 3;

  /** The number of bullets fired at once */
  public static int BULLETS = 6;

  /*
   * -------------------------------
   * STATE RELATED TO THE ATTACK
   * -------------------------------
   */
  /** length of the line of arcs */
  private final float length;

  /** the length of one repeition of the attack in ticks */
  private final int period;

  /** the current number of animation steps taken mod the period */
  private int stepsTaken;

  /** the spawner actually creating the arc */
  private final Spawner spawner;

  /**
   * Constructs the attack
   *
   * @param leftX         x coordinate of left endpoint of line of arcs
   * @param leftY         y coordinate of left endpoint of line of arcs
   * @param length        length of the line of arcs
   * @param period        the length of one repeition of the attack in ticks
   * @param shots         the number of arc shots fired in a single period
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public ArcsAcrossTheTopAttack(float leftX, float leftY, float length, int period, int shots,
      PhysicsEngine physicsEngine) {
    this.length = length;
    this.period = period;

    this.stepsTaken = 0;
    this.spawner = SpawnerFactory.constructRepeatingDownwardsFacingArc(BULLETS, CENTRAL_ANGLE, period / shots,
        physicsEngine);
    this.spawner.translate(leftX, leftY);

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    float step = length / period;
    if (stepsTaken == period) {
      spawner.translate(-length, 0);
      stepsTaken = 0;
    } else {
      spawner.translate(step, 0);
      stepsTaken++;
    }
  }
}
