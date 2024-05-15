package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class DelayedRotateRingAttack extends AttackPattern {

  /** the spawner actually creating the ring */
  private final Spawner spawner;

  /** the model of the attacking boss */
  private final BossModel model;

  /**
   * Constructs the attack
   *
   * @param period        the length of one repeition of the attack in ticks
   * @param dups          the number of bullets in the arc
   * @param delay         the delay until the rotate
   * @param centralAngle  the central angle in radians
   * @param rotateAngle   the angle to rotate after a delay
   * @param model         boss shooting the bullet
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public DelayedRotateRingAttack(int period, int dups,  int delay, float centralAngle, float rotateAngle, BossModel model,
                                 BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    this.spawner = SpawnerFactory.constructRepeatingDelayedRotateArc(dups, centralAngle, period,
            delay, rotateAngle, model,  builder, physicsEngine);
    this.model = model;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    this.spawner.moveSpawner(model.getX(), model.getY());
  }
}
