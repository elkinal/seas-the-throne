package edu.cornell.jade.seasthethrone.bpedit.patterns;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class UnbreakableDelayedTrackingSpeedArcAttack extends AttackPattern {

  /** the spawner actually creating the arc */
  private final Spawner spawner;

  /** the model of the attacking boss */
  private final BossModel model;

  /**
   * Constructs the attack
   *
   * @param period        the length of one repeition of the attack in ticks
   * @param dups          the number of bullets in the arc
   * @param centralAngle  the range of angle to fire the arc (radians)
   * @param angle         the angle to shoot in (radians)
   * @param delay         the delay until homing on the player
   * @param model         boss shooting the bullet
   * @param player        player to track
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public UnbreakableDelayedTrackingSpeedArcAttack(int period, int dups, float centralAngle, float angle, int delay, BossModel model,
                                                  PlayerModel player, BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    this.spawner = SpawnerFactory.constructRepeatingDelayedTrackingSpeedArc(dups, centralAngle, angle, period, delay, 10, 20,
           model, builder, physicsEngine);
    this.spawner.setUnbreakable();
    this.model = model;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    this.spawner.moveSpawner(model.getX(), model.getY());
  }
}
