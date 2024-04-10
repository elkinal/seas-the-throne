package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class RingAttack extends AttackPattern {

  /*
   * -------------------------------
   * STATE RELATED TO THE ATTACK
   * -------------------------------
   */

  /** the spawner actually creating the arc */
  private final Spawner spawner;

  /**
   * Constructs the attack
   *
   * @param ox            x coordinate of the attack origin
   * @param oy            y coordinate of the attack origin
   * @param delay         the length of time between successive bullets
   * @param shots         the number of bullets in one ring
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public RingAttack(float ox, float oy, int delay, int shots,
                    BulletModel.Builder builder, PhysicsEngine physicsEngine) {

    this.spawner = SpawnerFactory.constructRepeatingRing(shots, delay, builder, physicsEngine);
    this.spawner.translate(ox, oy);

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {}
}
