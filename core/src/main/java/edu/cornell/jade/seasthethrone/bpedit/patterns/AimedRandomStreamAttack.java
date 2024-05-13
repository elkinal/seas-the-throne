package edu.cornell.jade.seasthethrone.bpedit.patterns;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/** An attack spawning an aimed random stream attack */
public final class AimedRandomStreamAttack extends AttackPattern {
  /** the spawner actually creating the stream */
  private final Spawner spawner;

  /** the model of the attacking boss */
  private final BossModel model;

  /**
   * Constructs the attack
   *
   * @param angleRange    the full angle the spawner can spray in (radians)
   * @param period        the length of one repeition of the attack in ticks
   * @param model         boss shooting the bullet
   * @param player        player to track
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public AimedRandomStreamAttack(float angleRange, int period, BossModel model, PlayerModel player,
                            BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    this.spawner = SpawnerFactory.constructRepeatingAimedRandomStream(angleRange, period, player,
            model, builder, physicsEngine);
    this.model = model;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() { this.spawner.moveSpawner(model.getX(), model.getY()); }
}

