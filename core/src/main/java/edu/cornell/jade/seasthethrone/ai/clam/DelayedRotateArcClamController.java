package edu.cornell.jade.seasthethrone.ai.clam;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.patterns.DelayedRotateRingAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class DelayedRotateArcClamController extends ClamController {

  /** Bullet shot period */
  private static final int PERIOD = 40;

  /** Number of bullets in the arc */
  private static final int DUPS = 6;

  /** Delay until rotate */
  private static final int DELAY = 130;

  /** Central angle of the arc */
  private static final float CENTRAL_ANGLE = 4*MathUtils.PI/5;

  private static final float ROTATE_ANGLE = -MathUtils.PI/3;

  public DelayedRotateArcClamController(BossModel boss, PlayerModel player, BulletModel.Builder builder,
                                              PhysicsEngine physicsEngine) {
    super(boss, new DelayedRotateRingAttack(PERIOD, DUPS, DELAY, CENTRAL_ANGLE, ROTATE_ANGLE,
            boss, builder, physicsEngine), player);
  }
}
