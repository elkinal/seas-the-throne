package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class UIModel implements Renderable {
  /** UI renderable objects */
  private Array<Renderable> uiElements;

  /** Constructs a new UIModel. It initially has no renderable UI elements */
  public UIModel() {
    uiElements = new Array<>();

    // add health bar and ammo to render list
    uiElements.add(new HealthBar());
    uiElements.add(new AmmoBar());
  }

  /** Adds UI element to the model */
  public void add(Renderable r) {
    uiElements.add(r);
  }

  /**
   * Gets the HealthBar model for the player from the list of models
   *
   * @return Renderable that is the HealthBar
   */
  private HealthBar getHealthBar() {
    for (Renderable r : uiElements) {
      if (r instanceof HealthBar) {
        return (HealthBar) r;
      }
    }
    return null;
  }

  /**
   * Gets the AmmoBar model from the list of models
   *
   * @return Renderable that is the AmmoBar
   */
  private AmmoBar getAmmoBar() {
    for (Renderable r : uiElements) {
      if (r instanceof AmmoBar) {
        return (AmmoBar) r;
      }
    }
    return null;
  }

  /**
   * Update the health bar
   *
   * @param health the health of the player
   */
  public void update(int health) {
    assert getHealthBar() != null;
    getHealthBar().update(health);
  }

  /** Update the bullet counter for the player */
  public void update(int numSpeared, Vector2 position) {
    assert getAmmoBar() != null;
    getAmmoBar().update(numSpeared, position);
  }

  @Override
  public void draw(RenderingEngine renderer) {
    for (Renderable r : uiElements) {
      r.draw(renderer);
    }
  }

  @Override
  public FilmStrip progressFrame() {
    return null;
  }

  @Override
  public void alwaysUpdate() {}

  @Override
  public void neverUpdate() {}

  @Override
  public void setAlwaysAnimate(boolean animate) {}

  @Override
  public boolean alwaysAnimate() {
    return true;
  }

  public void clear() {
    uiElements.clear();
  }
}
