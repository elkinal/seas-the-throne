package edu.cornell.jade.seasthethrone.bpedit.patterns;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/** a ring pattern flowing in oscillating directions */
public final class OscillatingRingAttack extends AttackPattern {

  public static int DELAY = 15;
  public static int OSCILLATION_DELAY = 30;
  public static int PAUSE_TIME = 20;
  public static int SLEEP_TIME = 80;
  public static int DUPS = 11;
  public static float ROTATION_STEP = 0.3f;

  /** spawner creating bullets */
  private final Spawner spawner;

  /** the model of the attacking boss */
  private final BossModel model;

  private int timer;

  /**
   * Constructs the attack
   *
   * @param model         boss shooting the bullet
   * @param player        player to track
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public OscillatingRingAttack(BossModel model, PlayerModel player, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    this.spawner = SpawnerFactory.constructOscillatingRing(DUPS, DELAY, OSCILLATION_DELAY, PAUSE_TIME, SLEEP_TIME,
        builder, physicsEngine);
    this.model = model;
    this.timer = 0;

    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {
    timer++;
    if (timer < OSCILLATION_DELAY) {
      spawner.rotates(ROTATION_STEP);
    } else if (timer > OSCILLATION_DELAY + PAUSE_TIME) {
      spawner.rotates(-ROTATION_STEP);
    }
    timer %= (2 * OSCILLATION_DELAY + PAUSE_TIME + SLEEP_TIME);
    this.spawner.moveSpawner(model.getX(), model.getY());
  }
}
