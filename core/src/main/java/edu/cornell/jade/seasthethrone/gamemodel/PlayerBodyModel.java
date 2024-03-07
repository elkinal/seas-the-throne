package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.model.PolygonModel;

public class PlayerBodyModel extends PolygonModel {

    /** TODO: REMOVE THIS, DASH INFORMATION SHOULD NOT BE IN THE BODY */
    private boolean isDashing;
    public PlayerBodyModel(float[] vertices){
        super(vertices);
        isDashing = false;
    }
}
