package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/** A model representing the player's health bar. The bar is updated in UIController. */
public class HealthBar implements Renderable {

  /** Current texture for the healthbar */
  private TextureRegion texture;

  /** Textures for each health state, ordered from lowest to highest hp */
  private Array<TextureRegion> textures;

  /** Proportion of the screen width the health bar should take up */
  private float SCALE = 0.2f;

  /** Creates a new Health Bar */
  public HealthBar() {
    this.textures = new Array<>(6);
    textures.add(new TextureRegion(new Texture("ui/health_v3_0.png")));
    textures.add(new TextureRegion(new Texture("ui/health_v3_1.png")));
    textures.add(new TextureRegion(new Texture("ui/health_v3_2.png")));
    textures.add(new TextureRegion(new Texture("ui/health_v3_3.png")));
    textures.add(new TextureRegion(new Texture("ui/health_v3_4.png")));
    textures.add(new TextureRegion(new Texture("ui/health_v3_5.png")));

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

  @Override
  public void draw(RenderingEngine renderer) {
    GameCanvas canvas = renderer.getGameCanvas();
    float drawScale = SCALE * ((float)renderer.getGameCanvas().getWidth()/texture.getRegionWidth());
    float width = texture.getRegionWidth() * drawScale;
    float height = texture.getRegionHeight() * drawScale;
    canvas.drawUI(texture, 0.01f * canvas.getWidth(), 0.9f * canvas.getHeight(), width, height);
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
