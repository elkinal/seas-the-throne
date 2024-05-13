package edu.cornell.jade.seasthethrone.ai.jelly;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.jade.seasthethrone.bpedit.patterns.DelayedRotateRingAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/** A jelly with delayed rotating rings */
public class DelayedRotateRingJellyBossController extends JellyBossController {

  /** Bullet shot period */
  private static final int PERIOD = 40;

  /** Number of bullets in the ring */
  private static final int DUPS = 8;

  /** Delay until rotate */
  private static final int DELAY = 80;

  public DelayedRotateRingJellyBossController(BossModel boss, PlayerModel player, BulletModel.Builder builder,
                                              PhysicsEngine physicsEngine) {
    super(boss, player, new DelayedRotateRingAttack(PERIOD, DUPS, DELAY, boss, builder, physicsEngine),
            builder, physicsEngine);
    boss.setColor(new Color(0.9f, 1f, 0.20f, 1));
  }
}
