package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.PortalModel;

public class PortalController {
  private Array<PortalModel> portals;

  public PortalController() {
    this.portals = new Array<>();
  }

  public void addPortal(PortalModel portal) {
    this.portals.add(portal);
  }

  public void dispose() {
    portals.clear();
  }

  public void update(StateController state) {
    for (PortalModel portal : portals) {
      if (state.getCheckpoint() >= portal.getRequiredCheckpoint()) {
        portal.setActivated(true);
      }
    }
  }
}
