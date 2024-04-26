package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/**
 * This is a model class that combines all UI objects into one, and contains all of them in one
 * place so they are able to be updated all at once by UIController.
 */
public class UIModel implements Renderable {
  // UI renderable objects
  /** The health bar of the player */
  private HealthBar health;

  /** The ammo bar of the player */
  private AmmoBar ammo;

  /** The HP bar of the current boss */
  private BossHealthBar boss;

  /** Whether the player is fighting a boss (need to render HP bar) */
  private boolean isBoss;

  /** Constructs a new UIModel. All UI elements are initially empty. */
  public UIModel() {
    health = new HealthBar();
    ammo = new AmmoBar();
    boss = new BossHealthBar();
    isBoss = false;
  }

  /** Returns the AmmoBar */
  public AmmoBar getAmmoBar() {
    return ammo;
  }

  /**
   * Updates the texture of the health bar to match player health
   *
   * @param currHealth the current HP of the player
   */
  public void update(int currHealth) {
    if (currHealth < 0) {
      health.makeHPEmpty();
      return;
    }
    health.changeHP(currHealth);
  }

  /** Updates the texture of the ammo bar to match ammo count */
  public void update(int currAmmo, Vector2 pos) {
    if (currAmmo > ammo.numTextures() || currAmmo < 1) {
      ammo.makeHPEmpty();
      return;
    }
    ammo.changeHP(currAmmo);
    ammo.changePlayerPos(pos);
  }

  /**
   * Updates the texture of the boss HP bar to match boss health
   *
   * @param boss the boss that the player is fighting; null if none
   */
  public void update(BossController boss) {
    if (boss == null) {
      isBoss = false;
    } else {
      isBoss = true;
      this.boss.changeHP(boss.getHealth());
      System.out.println("hp changed" + boss.getHealth());
    }
  }

  @Override
  public void draw(RenderingEngine renderer) {
    health.draw(renderer);
    if (isBoss) {
      boss.draw(renderer);
    }
  }

  @Override
  public void progressFrame() {}

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

  /** Clears the UIModel of all elements */
  public void clear() {
    health = null;
  }
}
