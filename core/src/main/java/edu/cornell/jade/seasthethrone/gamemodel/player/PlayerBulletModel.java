package edu.cornell.jade.seasthethrone.gamemodel.player;

import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;

public class PlayerBulletModel extends BulletModel {

    public PlayerBulletModel(float x, float y, float radius) {
        super(x, y, radius);
    }
}
