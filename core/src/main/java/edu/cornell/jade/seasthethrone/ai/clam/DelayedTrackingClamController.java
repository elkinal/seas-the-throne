package edu.cornell.jade.seasthethrone.ai.clam;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.patterns.DelayedTrackingArcAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public final class DelayedTrackingClamController extends ClamController {
  /** Bullet shot period */
  private static final int PERIOD = 30;
  private static final int DUPS = 4;

  private static final int DELAY = 120;

  private static final float CENTRAL_ANGLE = MathUtils.PI/3;



  public DelayedTrackingClamController(float angle, BossModel boss, PlayerModel player, BulletModel.Builder builder,
                                   PhysicsEngine physicsEngine) {
    super(boss, new DelayedTrackingArcAttack(PERIOD, DUPS, CENTRAL_ANGLE, angle, DELAY, boss, player,
            builder, physicsEngine ), player);
  }
}

