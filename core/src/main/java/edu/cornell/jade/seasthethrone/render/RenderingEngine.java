package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.GameplayController;
import edu.cornell.jade.seasthethrone.level.BackgroundImage;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class RenderingEngine {
  /** The Renderable objects for rendering */
  private Array<Renderable> renderables;

  /** Layers of renderables to be rendered in order */
  private Array<Array<Renderable>> renderLayers;

  /** Game Viewport */
  private Viewport viewport;

  /** Internal game canvas to handle rendering */
  private GameCanvas canvas;

  /** Font for display text */
  private BitmapFont textFont;

  /** The ratio of a pixel in a texture to a meter in the world */
  private float worldScale;

  private BackgroundImage BACKGROUND;

  /**
   * Creates a new RenderingEngine based on the world width and world height for the camera.
   *
   * @param worldWidth the width in world coords on the camera
   * @param worldHeight the width in world coords on the camera
   */
  public RenderingEngine(float worldWidth, float worldHeight, Viewport viewport, float worldScale) {
    renderables = new Array<>();
    renderLayers = new Array<>();
    canvas = new GameCanvas();

    /** LOADING IN FONT, might be better to have an AssetDirectory later */
    FreeTypeFontGenerator generator =
        new FreeTypeFontGenerator(Gdx.files.internal("Alagard.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter =
        new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 50; // font size
    textFont = generator.generateFont(parameter);

    this.viewport = viewport;
    this.worldScale = worldScale;
  }

  /**
   * Adds a renderable object to the list to be rendered
   *
   * @param r The renderable to be added
   */
  public void addRenderable(Renderable r) {
    if (r != null) { renderables.add(r); }
  }

  public void setBackground(BackgroundImage bg) { BACKGROUND = bg;}

  /** Draw the background for the game */
  public void drawBackground() {
    canvas.clear();
    canvas.begin();
    canvas.draw(BACKGROUND.getTexture(), Color.WHITE, BACKGROUND.getPosition().x, BACKGROUND.getPosition().y,
            0, 0, 0, 4f, 4f);
    canvas.end();
  }

  /** Draws all the renderable objects in the list to be rendered */
  public void drawRenderables() {
    canvas.clear();
    canvas.begin();
    canvas.getSpriteBatch().setProjectionMatrix(getViewport().getCamera().combined);
    for (Renderable r : renderables) {
      r.draw(this);
    }
    canvas.end();
  }

  public void drawRenderLayers() {
    canvas.clear();
    canvas.begin();
    canvas.getSpriteBatch().setProjectionMatrix(getViewport().getCamera().combined);
    for (Array<Renderable> rArr : renderLayers) {
      for (Renderable r : rArr) {
        r.draw(this);
      }
    }
    canvas.end();
  }

  public void setNumLayers(int n) { renderLayers.ensureCapacity(n); }

  /** Gets the viewport of the render engine */
  public Viewport getViewport() {
    return viewport;
  }

  //Temporary for prototype
  public void drawGameState(GameplayController.GameState gs) {
    canvas.begin();
    switch(gs) {
      case WIN:
        canvas.drawTextCentered("YOU WIN!", textFont, 60f);
        break;
      default:
        canvas.drawTextCentered("Game Over!", textFont, 60f);
    }
    canvas.drawTextCentered("Press R to Restart", textFont, 0);
    canvas.end();
  }

  /**
   * Draws a given sprite with its center at the screen at a given position
   *
   * @param filmStrip the filmstrip representing the sprite to draw
   * @param x the x coordinate in world coordinates to draw at
   * @param y the y coordinate in world coordinates to draw at
   */
  public void draw(FilmStrip filmStrip, float x, float y) {
    float ox = filmStrip.getRegionWidth() / 2f;
    float oy = filmStrip.getRegionHeight() / 2f;

    canvas.draw(filmStrip, Color.WHITE, oy, ox, x, y, 0, worldScale, worldScale);
  }
  public void draw(FilmStrip filmStrip, float x, float y, boolean rot, float angle) {
    float ox = filmStrip.getRegionWidth() / 2f;
    float oy = filmStrip.getRegionHeight() / 2f;

    canvas.draw(filmStrip, Color.WHITE, oy, ox, x, y, angle, worldScale, worldScale);
  }

  public void draw(TextureRegion texture, float x, float y) {
    float ox = texture.getRegionWidth() / 2f;
    float oy = texture.getRegionHeight() / 2f;

    canvas.draw(texture, Color.WHITE, ox, oy, x, y, 0, worldScale, worldScale);
  }

  public void draw(FilmStrip filmStrip, float x, float y, float scale) {
    float ox = filmStrip.getRegionWidth() / 2f;
    float oy = filmStrip.getRegionHeight() / 2f;

    canvas.draw(filmStrip, Color.WHITE, oy, ox, x, y, 0, scale, scale);
  }
  public void draw(FilmStrip filmStrip, float x, float y, float scale, Color color) {
    float ox = filmStrip.getRegionWidth() / 2f;
    float oy = filmStrip.getRegionHeight() / 2f;

    canvas.draw(filmStrip, color, oy, ox, x, y, 0, scale, scale);
  }

  public void clear() {
    renderables.clear();
  }

  /** Returns the gameCanvas */
  public GameCanvas getGameCanvas() {
    return canvas;
  }

  @Override
  public String toString() {
    return "RenderingEngine [renderables=" + renderables + "]";
  }
}
