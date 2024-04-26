package edu.cornell.jade.seasthethrone.bpedit.patterns;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

import java.util.Random;

/** An attack spawning random stream attack at a given angle */
public final class RandomStreamAttack extends AttackPattern {
  /** the spawner actually creating the stream */
  private final Spawner spawner;

  /** the model of the attacking boss */
  private final BossModel model;

  /** The random number generator */
  private final Random rand;

  /** the central angle of the attack */
  private final float angle;

  /** the full angle the spawner can spray in */
  private final float angleRange;

  /**
   * Constructs the attack
   *
   * @param angle         the angle (relative to hard right) the spawner points in (radians)
   * @param angleRange    the full angle the spawner can spray in (radians)
   * @param period        the length of one repeition of the attack in ticks
   * @param model         boss shooting the bullet
   * @param player        player to track
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public RandomStreamAttack(float angle, float angleRange, int period, BossModel model, PlayerModel player,
                                BulletModel.Builder builder, PhysicsEngine physicsEngine) {
    this.spawner = SpawnerFactory.constructRepeatingLeftFacingStream(period, builder, physicsEngine);
    this.model = model;
    this.rand = new Random();

    this.angle = angle;
    this.angleRange = angleRange;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    this.spawner.moveSpawner(model.getX(), model.getY());
    this.spawner.setAngle(rand.nextFloat(angle-angleRange/2, angle+angleRange/2));
  }
}

