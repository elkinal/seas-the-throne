package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class AlternatingRingAttack extends AttackPattern {

  /*
   * -------------------------------
   * STATE RELATED TO THE ATTACK
   * -------------------------------
   */

  /** the spawner actually creating the arc */
  private final Spawner spawner;

  /** the model of the attacking boss */
  private final BossModel model;

  private int timer;

  private int delay;

  private int shots;

  /**
   * Constructs the attack
   *
   * @param delay         the length of time between successive bullets
   * @param shots         the number of bullets in one ring
   * @param vel           the velocity of the bullets
   * @param unbreakable   if the bullets are unbreakable
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public AlternatingRingAttack(BossModel model, int delay, int shots, float vel, boolean unbreakable,
                               BulletModel.Builder builder, PhysicsEngine physicsEngine) {

    this.spawner = SpawnerFactory.constructRepeatingRing(shots, delay, vel, builder, physicsEngine);
    if (unbreakable) this.spawner.setUnbreakable();

    this.model = model;
    this.timer = delay;
    this.delay = delay;
    this.shots = shots;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    this.spawner.moveSpawner(model.getX(), model.getY());
    timer -= 1;
    if (timer <= 0){
      timer = delay;
      float angle = MathUtils.PI*2/(shots-1);
      this.spawner.rotates(angle/2);
    }
  }
}
