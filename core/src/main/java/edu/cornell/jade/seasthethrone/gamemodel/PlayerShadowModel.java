package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;

public class PlayerShadowModel extends BoxModel {
    public PlayerShadowModel(float width, float height) {
        super(width, height);
    }

    public PlayerShadowModel(float x, float y, float width, float height) { super(x, y, width, height); }
}
