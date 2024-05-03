package edu.cornell.jade.seasthethrone.ai;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.patterns.RandomStreamAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class RandomStreamClamController extends ClamController{
  /** Bullet shot period */
  private static final int PERIOD = 10;
  /** Bullet angle spray range */
  private static final float ANGLE_RANGE = MathUtils.PI/5;

  public RandomStreamClamController(float angle, BossModel boss, PlayerModel player, BulletModel.Builder builder,
                                   PhysicsEngine physicsEngine) {
    super(boss, new RandomStreamAttack(angle, ANGLE_RANGE, PERIOD, boss, player, builder, physicsEngine), player);
  }
}
