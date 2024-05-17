package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

/** A model representing the player's health bar. The bar is updated in UIController. */
public class BossHealthBar implements Renderable {

  /** Texture for the HP bar foreground */
  private TextureRegion foreground;

  /** Texture for the HP bar background */
  private TextureRegion background;

  /** Ratio of the bar width to the screen */
  private static float SCALE = 0.5f;

  /** The x-coordinate of the center of the progress bar */
  private int centerX;

  /** The height of the canvas window (necessary since sprite origin != screen origin) */
  private int height;

  /** Width of the total health bar */
  private float width;

  /** Width of the health bar given HP level */
  private float hWidth;

  /** Standard window size (for scaling) */
  private static int STANDARD_WIDTH = 800;

  /** Standard window height (for scaling) */
  private static int STANDARD_HEIGHT = 700;

  /** Creates a new Health Bar */
  public BossHealthBar() {
    foreground = new TextureRegion(new Texture("ui/boss_hp_full.png"));
    background = new TextureRegion(new Texture("ui/boss_hp_empty.png"));
    // set width of full hp bar
    hWidth = width;
  }

  /**
   * Updates the texture of this health bar to match player health
   *
   * @param currHealth the player's current health
   */
  protected void changeHP(int currHealth, int maxHealth) {
    hWidth = width * (currHealth / (float) maxHealth);
    if (hWidth < 0) {
      hWidth = 0;
    }
  }

  /**
   * Draws the health bar with the health drawn separately from the base bar. The bars are centered
   * at the center of the screen.
   *
   * @param renderer the render engine
   */
  @Override
  public void draw(RenderingEngine renderer) {
    resize(renderer.getGameCanvas().getWidth(), renderer.getGameCanvas().getHeight());
    renderer.getGameCanvas().drawUI(background, centerX - width / 2, 0.1f * height, width, 30);
    renderer.getGameCanvas().drawUI(foreground, centerX - width / 2, 0.1f * height, hWidth, 30);
  }

  /**
   * Resizes the health bar to fit the screen
   *
   * @param canvasWidth the width of the canvas
   * @param canvasHeight the height of the canvas
   */
  public void resize(int canvasWidth, int canvasHeight) {
    // Compute the drawing scale
    float sx = ((float) canvasWidth) / STANDARD_WIDTH;
    float sy = ((float) canvasHeight) / STANDARD_HEIGHT;

    width = (int) (SCALE * canvasWidth);
    centerX = canvasWidth / 2;
    height = canvasHeight;
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
    return false;
  }
}
