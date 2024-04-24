package edu.cornell.jade.seasthethrone.ai;

import edu.cornell.jade.seasthethrone.bpedit.patterns.OscillatingRingAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public final class OscillatingRingClamController extends ClamController {
  public OscillatingRingClamController(BossModel boss, PlayerModel player, BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    super(boss, new OscillatingRingAttack(boss, player, builder, physicsEngine), player);
  }
}
