package edu.cornell.jade.seasthethrone.ai.jelly;

import edu.cornell.jade.seasthethrone.bpedit.patterns.AimedArcAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.JellyBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/**
 * The basic jelly enemy with a single aimed bullet.
 */
public class AimedArcJellyBossController extends JellyBossController {

  /** Bullet shot period */
  private static final int PERIOD = 100;

  public AimedArcJellyBossController(JellyBossModel boss, PlayerModel player, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    super(boss, player, new AimedArcAttack(PERIOD, boss, player, builder, physicsEngine), builder, physicsEngine);
  }
}
