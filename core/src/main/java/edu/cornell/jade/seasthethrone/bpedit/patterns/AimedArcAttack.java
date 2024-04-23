package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/** An attack which spawns an arc of bullets aimed at the player */
public final class AimedArcAttack extends AttackPattern {
  /*
   * -------------------------------
   * CONSTANTS RELATED TO THE ATTACK
   * -------------------------------
   */

  /** The angle of the arc of a shot */
  public static float CENTRAL_ANGLE = MathUtils.PI / 3;

  /** The number of bullets fired at once */
  public static int BULLETS = 6;

  /**
   * Constructs the attack
   *
   * @param period        the length of one repeition of the attack in ticks
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public AimedArcAttack(int period, BossModel model, BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    Spawner spawner = SpawnerFactory.constructRepeatingAimedArc(BULLETS, CENTRAL_ANGLE, period, model,
        builder, physicsEngine);

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
  }
}
