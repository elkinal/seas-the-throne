package edu.cornell.jade.seasthethrone.ai.jelly;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/**
 * A basic jelly that chases the player
 */
public class ChasingJellyBossController extends JellyBossController {

  /** The movement speed of the jelly */
  private static final float speed = 8f;

  public ChasingJellyBossController(BossModel boss, PlayerModel player, BulletModel.Builder builder,
                                    PhysicsEngine physicsEngine) {
    super(boss, player, null, builder, physicsEngine);
    boss.setColor(new Color(0.9f, 0.20f, .9f, 1));
    AGRO_DISTANCE = 35f;
  }

  @Override
  protected void nextState() {
    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE && boss.isInRoom()) {
          state = State.MOVE;
        }
        break;
      default:
        break;
    }
  }
    @Override
    protected void act() {
      switch (state) {
        case MOVE:
          boss.setLinearVelocity(boss.getPosition().sub(player.getPosition()).nor().scl(-speed));
          break;
        default:
          break;
      }
    }
  }
