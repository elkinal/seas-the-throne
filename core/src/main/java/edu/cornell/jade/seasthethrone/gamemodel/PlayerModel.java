package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.model.ComplexModel;

public class PlayerModel extends ComplexModel {

    // built from multiple polygonmodels?
    @Override
    protected boolean createJoints(World world) {
        // TODO:
        return false;
    }
}
