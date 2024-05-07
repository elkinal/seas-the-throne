package edu.cornell.jade.seasthethrone.ai.jelly;

import edu.cornell.jade.seasthethrone.bpedit.patterns.SingleBulletAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.JellyBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/**
 * The basic jelly enemy with a single aimed bullet.
 */
public class AimedSingleBulletJellyBossController extends JellyBossController {

  /** Bullet shot period */
  private static final int PERIOD = 20;

  public AimedSingleBulletJellyBossController(JellyBossModel boss, PlayerModel player, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    super(boss, player, new SingleBulletAttack(PERIOD, boss, builder, physicsEngine), builder, physicsEngine);
  }
}
