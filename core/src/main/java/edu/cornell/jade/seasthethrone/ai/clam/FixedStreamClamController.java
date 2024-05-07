package edu.cornell.jade.seasthethrone.ai.clam;

import edu.cornell.jade.seasthethrone.bpedit.patterns.FixedAngleStreamAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public final class FixedStreamClamController extends ClamController {
  /** Bullet shot period */
  private static final int PERIOD = 10;

  public FixedStreamClamController(float angle, BossModel boss, PlayerModel player, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    super(boss, new FixedAngleStreamAttack(angle, PERIOD, boss, player, builder, physicsEngine), player);
  }
}
