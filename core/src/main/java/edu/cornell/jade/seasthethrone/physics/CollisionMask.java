package edu.cornell.jade.seasthethrone.physics;

import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerBodyModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerShadowModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerSpearModel;
import edu.cornell.jade.seasthethrone.model.Model;

/**
 * In charge of specifying/setting collision bitmasks
 * for easy collision filtering.
 */
public class CollisionMask {
    /** Category bitmask for wall (also default is 0x0001) */
    private static final short CATEGORY_OBSTACLE = 0x0001;

    /** Category bitmask for player */
    private static final short CATEGORY_PLAYER = 0x0002;

    /** Category bitmask for player shadow (should only touch walls) */
    private static final short CATEGORY_PLAYER_SHADOW = 0x0004;

    /** Category bitmask for enemy bullet */
    private static final short CATEGORY_ENEMY_BULLET = 0x0008;

    /** Category bitmask for player bullet */
    private static final short CATEGORY_PLAYER_BULLET = 0x0010;

    /** Category bitmask for boss  */
    private static final short CATEGORY_BOSS = 0x0020;


    /** Set the category and mask bits for a given model */
    public static void setCategoryMaskBits(Model model, boolean shotByPlayer){
        // If player
        if (model instanceof PlayerBodyModel || model instanceof PlayerSpearModel){
            model.setCategoryBits(CATEGORY_PLAYER);
            model.setMaskBits((short) (CATEGORY_ENEMY_BULLET | CATEGORY_BOSS));
        }
        // If player shadow
        else if (model instanceof PlayerShadowModel){
            model.setCategoryBits(CATEGORY_PLAYER_SHADOW);
            model.setMaskBits(CATEGORY_OBSTACLE);
        }
        // If player bullet
        else if (model instanceof BulletModel && shotByPlayer) {
            model.setCategoryBits(CATEGORY_PLAYER_BULLET);
            model.setMaskBits(CATEGORY_BOSS);
        }
        // If enemy bullet
        else if (model instanceof BulletModel){
            model.setCategoryBits(CATEGORY_ENEMY_BULLET);
            model.setMaskBits(CATEGORY_PLAYER);
        }
        // If boss
        else if (model instanceof BossModel) {
            model.setCategoryBits(CATEGORY_BOSS);
            model.setMaskBits((short) (CATEGORY_PLAYER | CATEGORY_PLAYER_BULLET));
        }
    }

    /** Set the category and mask bits for a given model */
    public static void setCategoryMaskBits(Model model){
        setCategoryMaskBits(model, false);
    }
}
