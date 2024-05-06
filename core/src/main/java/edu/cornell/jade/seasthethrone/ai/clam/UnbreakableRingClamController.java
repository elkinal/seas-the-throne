package edu.cornell.jade.seasthethrone.ai.clam;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.patterns.RandomStreamAttack;
import edu.cornell.jade.seasthethrone.bpedit.patterns.RingAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class UnbreakableRingClamController extends ClamController{
  /** Bullet shot period */
  private static final int PERIOD = 150;
  /** Number of bullets to fire */
  private static final int DUPS = 9;

  public UnbreakableRingClamController(BossModel boss, PlayerModel player, BulletModel.Builder builder,
                                    PhysicsEngine physicsEngine) {
    super(boss, new RingAttack(boss, PERIOD, DUPS, true, builder, physicsEngine), player);
  }
}

