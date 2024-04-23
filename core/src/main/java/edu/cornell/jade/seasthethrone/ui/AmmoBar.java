package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class AmmoBar implements Renderable {

  /** Current texture for the ammo */
  private Texture texture;

  /** Textures for each ammo state, ordered from lowest to highest ammo */
  private Array<Texture> textures;

  private Vector2 playerPos;

  /** Offset below the player to display the ammo bar */
  private final float AMMO_OFFSET = 2.5f;

  public AmmoBar() {
    this.textures = new Array<>();
    this.playerPos = new Vector2();
    textures.add(new Texture("empty.png"));
    textures.add(new Texture("ui/darkammo_1_5.png"));
    textures.add(new Texture("ui/darkammo_2_5.png"));
    textures.add(new Texture("ui/darkammo_3_5.png"));
    textures.add(new Texture("ui/darkammo_4_5.png"));
    textures.add(new Texture("ui/darkammo_5_5.png"));
    this.texture = textures.get(textures.size - 1);
  }

  /** Updates the texture of this health bar to be zero (player is dead) */
  protected void makeHPEmpty() {
    texture = textures.get(0);
  }

  /**
   * Updates the texture of this health bar to match player health
   *
   * @param currHealth the player's current health
   */
  protected void changeHP(int currHealth) {
    texture = textures.get(currHealth);
  }

  protected void changePlayerPos(Vector2 position) {
    playerPos = position;
  }

  /**
   * Returns the number of textures in the AmmoBar. There is one for each bullet.
   *
   * @return the number of textures in the AmmoBar
   */
  public int numTextures() {
    return textures.size;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    renderer.draw(new TextureRegion(texture), playerPos.x, playerPos.y - AMMO_OFFSET);
  }

  @Override
  public void progressFrame() {
  }

  @Override
  public void alwaysUpdate() {}

  @Override
  public void neverUpdate() {}

  @Override
  public void setAlwaysAnimate(boolean animate) {}

  @Override
  public boolean alwaysAnimate() {
    return false;
  }
}
