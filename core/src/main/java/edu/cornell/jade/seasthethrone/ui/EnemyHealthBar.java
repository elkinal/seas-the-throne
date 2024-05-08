package edu.cornell.jade.seasthethrone.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class EnemyHealthBar implements Renderable {
  /**
   * Texture for the HP bar foreground
   */
  private TextureRegion foreground;

  /**
   * Texture for the HP bar background
   */
  private TextureRegion background;

  /**
   * Ratio of the bar width to the screen
   */
  private static float SCALE = 0.5f;

  /**
   * Width of the total health bar
   */
  private float width;

  /**
   * Width of the health bar given HP level
   */
  private float hWidth;

  /**
   * Standard window size (for scaling)
   */
  private static int STANDARD_WIDTH = 800;

  /**
   * Standard window height (for scaling)
   */
  private static int STANDARD_HEIGHT = 700;

  /** Offset below the enemy to display the health bar */
  private final float HEALTH_OFFSET = 4.0f;

  /** Enemy Position */
  private Vector2 enemyPosition;

  /**
   * Creates a new Health Bar
   */
  public EnemyHealthBar(TextureRegion foreground, TextureRegion background) {
    this.foreground = foreground;
    this.background = background;
    // set width of full hp bar
    hWidth = width;
  }

  /**
   * Updates the texture of this health bar to match player health
   *
   * @param currHealth the player's current health
   */
  protected void changeHP(int currHealth, int fullHealth) {
    hWidth = (float)currHealth/(float)fullHealth;
  }

  protected void changeEnemyPosition (Vector2 pos){
    enemyPosition = pos;
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
    renderer.draw(background, enemyPosition.x, enemyPosition.y + HEALTH_OFFSET, 1.0f, 1.0f);

    // TODO: don't hardcode worldscale here
    float x_offset = 0.135f*(background.getRegionWidth()/2f)*(hWidth-1);
    renderer.draw(foreground, enemyPosition.x + x_offset,enemyPosition.y + HEALTH_OFFSET, hWidth, 1.0f);
  }

  /**
   * Resizes the health bar to fit the screen
   *
   * @param canvasWidth  the width of the canvas
   * @param canvasHeight the height of the canvas
   */
  public void resize(int canvasWidth, int canvasHeight) {
    // Compute the drawing scale
    float sx = ((float) canvasWidth) / STANDARD_WIDTH;
    float sy = ((float) canvasHeight) / STANDARD_HEIGHT;

    width = (int) (SCALE * 200);
  }

  @Override
  public void progressFrame() {
  }

  @Override
  public void alwaysUpdate() {
  }

  @Override
  public void neverUpdate() {
  }

  @Override
  public void setAlwaysAnimate(boolean animate) {
  }

  @Override
  public boolean alwaysAnimate() {
    return false;
  }
}
