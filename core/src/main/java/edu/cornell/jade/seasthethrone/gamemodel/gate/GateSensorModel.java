package edu.cornell.jade.seasthethrone.gamemodel.gate;

import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;

public class GateSensorModel extends BoxModel {

    /** Set to true when the player contacts this sensor */
    private boolean raised;

    public GateSensorModel(LevelObject obs) {
        super(obs.x, obs.y, obs.width, obs.height);
        this.raised = false;
        setSensor(true);
    }

    /** Returns if this sensor has been activated */
    public boolean isRaised() { return raised; }

    /** Flags this gate for raising */
    public void setRaised(boolean raised) { this.raised = raised; }

}
