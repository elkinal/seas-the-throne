package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class SpiralAttack extends AttackPattern {

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

  /** the model of the attacking boss */
  private final BossModel model;

  /**
   * Constructs the attack
   *
   * @param model         boss shooting the bullet
   * @param delay         the length of time between successive bullets
   * @param shots         the number of bullets in one circular rotation
   * @param unbreakable   if the bullets are unbreakable
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public SpiralAttack(BossModel model, int delay, int shots, boolean unbreakable,
                              BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    this.delay = delay;
    this.angle = MathUtils.PI * 2f / shots;

    this.stepsTaken = 0;
    this.spawner = SpawnerFactory.constructRepeatingStream(delay, builder, physicsEngine);
    if (unbreakable) this.spawner.setUnbreakable();
    this.model = model;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    this.spawner.moveSpawner(model.getX(), model.getY());
    if (stepsTaken == delay - 1) {
      spawner.rotates(angle);
      stepsTaken = 0;
    } else{
      stepsTaken++;
    }
  }
}
