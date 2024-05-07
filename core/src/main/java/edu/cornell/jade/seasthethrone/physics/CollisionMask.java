package edu.cornell.jade.seasthethrone.physics;

import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerBodyModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerBulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerShadowModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerSpearModel;
import edu.cornell.jade.seasthethrone.gamemodel.CheckpointModel;
import edu.cornell.jade.seasthethrone.model.Model;

/**
 * In charge of specifying/setting collision bitmasks
 * for easy collision filtering.
 */
public class CollisionMask {
  /**
   * Category bitmask for wall (also default is 0x0001)
   */
  private static final short CATEGORY_WALL = 0x0001;

  /**
   * Category bitmask for player body
   */
  private static final short CATEGORY_PLAYER = 0x0002;

  /**
   * Category bitmask for player spear
   */
  private static final short CATEGORY_PLAYER_SPEAR = 0x0004;

  /**
   * Category bitmask for player shadow (should only touch walls)
   */
  private static final short CATEGORY_PLAYER_SHADOW = 0x0008;

  /**
   * Category bitmask for enemy bullet
   */
  private static final short CATEGORY_ENEMY_BULLET = 0x0010;

  /**
   * Category bitmask for boss
   */
  private static final short CATEGORY_BOSS = 0x0020;

  /**
   * Category bitmask for checkpoint
   */
  private static final short CATEGORY_CHECKPOINT = 0x0040;

  /**
   * Category bitmask for obstacle
   */
  private static final short CATEGORY_OBSTACLE = 0x0080;


  /**
   * Set the category and mask bits for a given model
   */
  public static void setCategoryMaskBits(Model model) {
    // If player
    if (model instanceof PlayerBodyModel) {
      model.setCategoryBits(CATEGORY_PLAYER);
      model.setMaskBits((short) (CATEGORY_ENEMY_BULLET | CATEGORY_BOSS));
    }
    if (model instanceof PlayerSpearModel) {
      model.setCategoryBits(CATEGORY_PLAYER_SPEAR);
      model.setMaskBits((short) (CATEGORY_ENEMY_BULLET | CATEGORY_BOSS));
    }
    // If player shadow
    else if (model instanceof PlayerShadowModel) {
      model.setCategoryBits(CATEGORY_PLAYER_SHADOW);
      model.setMaskBits((short) (CATEGORY_WALL | CATEGORY_OBSTACLE | CATEGORY_CHECKPOINT));
    }
    // If player bullet
    else if (model instanceof PlayerBulletModel) {
      model.setCategoryBits(CATEGORY_PLAYER);
      model.setMaskBits((short) (CATEGORY_BOSS | CATEGORY_OBSTACLE));
    }
    // If enemy unbreakable bullet
    else if (model instanceof UnbreakableBulletModel) {
      model.setCategoryBits(CATEGORY_ENEMY_BULLET);
      model.setMaskBits((short) (CATEGORY_PLAYER | CATEGORY_OBSTACLE));
    }
    // If enemy bullet
    else if (model instanceof BulletModel) {
      model.setCategoryBits(CATEGORY_ENEMY_BULLET);
      model.setMaskBits((short) (CATEGORY_PLAYER | CATEGORY_PLAYER_SPEAR | CATEGORY_OBSTACLE));
    }
    // If boss
    else if (model instanceof BossModel) {
      model.setCategoryBits(CATEGORY_BOSS);
      model.setMaskBits((short) (CATEGORY_PLAYER | CATEGORY_PLAYER_SPEAR));
    }
    // If portal
    else if (model instanceof PortalModel) {
      model.setCategoryBits(CATEGORY_WALL);
      model.setMaskBits(CATEGORY_PLAYER_SHADOW);
    }
    // If checkpoint
    else if (model instanceof CheckpointModel) {
      model.setCategoryBits(CATEGORY_CHECKPOINT);
      model.setMaskBits(CATEGORY_PLAYER_SHADOW);
    }
    // If obstacle
    else if (model instanceof ObstacleModel) {
      model.setCategoryBits(CATEGORY_OBSTACLE);
      model.setMaskBits((short) (CATEGORY_PLAYER_SHADOW | CATEGORY_PLAYER | CATEGORY_ENEMY_BULLET));
    }
  }
}
