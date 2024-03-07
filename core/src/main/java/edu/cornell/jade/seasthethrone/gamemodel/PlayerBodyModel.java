package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.PolygonModel;

/**
 * Model for the player body. This class extends
 * {@link PlayerModel} for a flexible frame. The PlayerBodyModel
 * accounts for the main hitbox of the player.
 */
public class PlayerBodyModel extends PolygonModel {

    /** TODO: REMOVE THIS */
    private boolean isDashing;
    public PlayerBodyModel(float[] vertices){
        super(vertices);
        isDashing = false;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public void setDashing(boolean dashing) {
        isDashing = dashing;
    }

    /** TODO: delete this */
    public String getPointSensorName(){
        return "NosePointSensor";
    }
}
