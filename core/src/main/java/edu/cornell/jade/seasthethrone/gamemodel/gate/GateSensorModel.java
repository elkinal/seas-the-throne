package edu.cornell.jade.seasthethrone.gamemodel.gate;

import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;

public class GateSensorModel extends BoxModel {
    public GateSensorModel(LevelObject obs) {
        super(obs.x, obs.y, obs.width, obs.height);
        System.out.println("f1");
    }
}
