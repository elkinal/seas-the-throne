package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;

public class PlayerShadowModel extends BoxModel {
    /** Width of shadow */
    private static float SHADOW_WIDTH = 1.0f;

    /** Length of shadow */
    private static float SHADOW_LENGTH = 0.3f;

    public PlayerShadowModel(float x, float y) { super(x, y, SHADOW_WIDTH, SHADOW_LENGTH); }
}
