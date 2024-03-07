package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.model.BoxModel;

/**
 * Model for the player spear. When the spear is extended,
 * it will have an active hitbox that will allow the spear to
 * pierce through enemies.
 */
public class PlayerSpearModel extends BoxModel {

    /** If the spear is extended (during dash) */
    private boolean spearExtended;

    /**
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the spear center
     * @param y  		Initial y position of the spear center
     * @param width		spear width in physics units
     * @param height	spear width in physics units
     */
    public PlayerSpearModel(float x, float y, float width, float height){
        super(x, y, width, height);
        spearExtended = false;
    }

    public boolean isSpearExtended() {
        return spearExtended;
    }
    public void setSpearExtended(boolean value){
        spearExtended = value;
    }
}

