package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

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

  /** The HP bars of enemies */
  private Array<EnemyHealthBar> enemies;

  /** Whether the player is fighting a boss (need to render HP bar) */
  private boolean isBoss;

  /** The red part of the hp bar */
  private TextureRegion foreground = new TextureRegion(new Texture("ui/enemy_hp_full.png"));

  /** The outline of the hp bar */
  private TextureRegion background = new TextureRegion(new Texture("ui/enemy_hp_empty.png"));

  /**
   * Constructs a new UIModel. All UI elements are initially empty.
   *
   * @param x the x coordinate of the middle of the screen
   * @param y the y coordinate of the middle of the screen
   */
  public UIModel(int x, int y) {
    ammo = new AmmoBar();
    boss = new BossHealthBar();

    health = new HealthBar();
    enemies = new Array<>();
    isBoss = false;
  }

  /**
   * Returns the AmmoBar
   *
   * @return the ammo bar for the player
   */
  public AmmoBar getAmmoBar() {
    return ammo;
  }

  /**
   * Returns the Enemies
   *
   * @return the enemies
   */
  public Array<EnemyHealthBar> getEnemies() {
    return enemies;
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
      if (boss.isBoss()) {
        isBoss = true;
        this.boss.changeHP(boss.getHealth(), boss.getMaxHealth());
      } else {
        EnemyHealthBar newEnemy = new EnemyHealthBar(foreground, background);
        newEnemy.changeHP(boss.getHealth(), boss.getBoss().getFullHealth());
        newEnemy.changeEnemyPosition(boss.getBoss().getPosition());
        this.enemies.add(newEnemy);
      }
    }
  }

  /** Clear enemy cache */
  public void clearEnemies() {
    enemies.clear();
  }

  /**
   * Draws UI with the Boss HP bar
   *
   * @param renderer the rendering engine
   */
  public void draw(RenderingEngine renderer, int finishAnimate) {
    health.draw(renderer);
    if (isBoss && finishAnimate > 0) {
      boss.draw(renderer);
    }
  }

  /**
   * Draws UI without Boss HP bar
   *
   * @param renderer the rendering engine
   */
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
