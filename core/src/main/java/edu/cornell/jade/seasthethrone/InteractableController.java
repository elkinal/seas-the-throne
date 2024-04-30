package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.Interactable;

public class InteractableController {
  private Array<Interactable> interactables;

  public InteractableController() {
    this.interactables = new Array<>();

  }

  public void add(Interactable interactable) {
    this.interactables.add(interactable);
  }

  public void update() {
    for (Interactable interactable : interactables) {
      if (interactable.isInteracted()) {
        interactable.interact();
      }
    }
  }

}
