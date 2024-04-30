package edu.cornell.jade.seasthethrone.gamemodel;

import edu.cornell.jade.seasthethrone.InteractableController;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;

public class HealthpackModel extends BoxModel implements Interactable {


  public HealthpackModel(LevelObject obs) {
    super(obs.x, obs.y, obs.width, obs.height);

  }

  @Override
  public void interact() {

  }

  @Override
  public boolean isInteracted() {
    return false;
  }
}
