package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.util.FilmStrip;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Color;

public class RenderingEngine {
  /** Player sprite scale to world scale */
  private float PLAYER_SCALE = 0.1f;
  /** Bullet sprite scale to world scale */
  private float BULLET_SCALE = 0.2f;

  /**
   * The Renderable objects for rendering
   */
  Array<Renderable> renderables;

  /** Game Viewport */
  FitViewport viewport;

  /**
   * Internal game canvas to handle rendering
   * TODO: REPLACE WITH HOMEBREWED THING
   */
  GameCanvas canvas;

  /** Font for display text */
  BitmapFont textFont;

  /**
   * FIXME: stop hardcoding texture regions
   */
  private static final Texture PLAYER_TEXTURE_UP = new Texture("playerspriterunfilmstrip_up.png");
  private static final Texture PLAYER_TEXTURE_DOWN = new Texture("playerspriterunfilmstrip_down.png");
  private static final Texture SPEAR_TEXTURE_DOWN = new Texture("spear_up_down.png");
  private static final Texture SPEAR_TEXTURE_LEFT = new Texture("spear_left_right.png");
  FilmStrip filmStrip = new FilmStrip(PLAYER_TEXTURE_DOWN, 1, 7, 6);

  /**
   * FIXME: stop hardcoding texture regions
   */
  private static final Texture FISH_TEXTURE = new Texture("bullet_test.png");

  /**
   * Creates a new RenderingEngine based on the world width and world height for
   * the camera.
   *
   * @param worldWidth  the width in world coords on the camera
   * @param worldHeight the width in world coords on the camera
   *
   */
  public RenderingEngine(float worldWidth, float worldHeight) {
    renderables = new Array<>();
    canvas = new GameCanvas();

    /** LOADING IN FONT, might be better to have an AssetDirectory later */
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("EBGaramond.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 30; // font size
    textFont = generator.generateFont(parameter);

    this.viewport = new FitViewport(worldWidth, worldHeight);
  }

  /**
   * Adds a renderable object to the list to be rendered
   *
   * @param r The renderable to be added
   */
  public void addRenderable(Renderable r) {
    renderables.add(r);
  }

  /**
   * Draws all the renderable objects in the list to be rendered
   */
  public void drawRenderables() {
    canvas.clear();
    canvas.begin(canvas.getWidth() / viewport.getWorldWidth(), canvas.getHeight() / viewport.getWorldHeight());
    for (Renderable r : renderables) {
      float x = r.getX();
      float y = r.getY();
      if (r instanceof PlayerRenderable) {
        Texture texture;
        Texture speartexture;
        float spearx = 0;
        float speary = 0;
        if (((PlayerRenderable) r).direction() == 0) {
          texture = PLAYER_TEXTURE_UP;
          speartexture = SPEAR_TEXTURE_DOWN;
          spearx = x + 10;
          speary = y;
        } else if (((PlayerRenderable) r).direction() == 1) {
          texture = PLAYER_TEXTURE_DOWN;
          speartexture = SPEAR_TEXTURE_DOWN;
          spearx = x + 10;
          speary = y;
        } else if (((PlayerRenderable) r).direction() == 2) {
          // FIXME: Replace with Texture Left and Right when we have those
          texture = PLAYER_TEXTURE_DOWN;
          speartexture = SPEAR_TEXTURE_LEFT;
          spearx = x + 6;
          speary = y + 20;
        } else {
          texture = PLAYER_TEXTURE_UP;
          speartexture = SPEAR_TEXTURE_LEFT;
          spearx = x + 6;
          speary = y + 20;
        }
        filmStrip.setTexture(texture);
        filmStrip.setFrame(((PlayerRenderable) r).frameNumber());
        canvas.draw(filmStrip, Color.WHITE, -303, -220, x, y, 0, PLAYER_SCALE, PLAYER_SCALE);
        canvas.draw(speartexture, Color.WHITE, -220, -233, spearx, speary, 0, PLAYER_SCALE, PLAYER_SCALE);
        ((PlayerRenderable) r).updateAnimationFrame();
      } else if (r instanceof FishRenderable) {
        canvas.draw(FISH_TEXTURE, Color.WHITE, -157, -117, x, y, 0, BULLET_SCALE, BULLET_SCALE);
      }
    }
    canvas.end();
  }

  public void drawGameOver() {
    canvas.begin();
    canvas.drawTextCentered("Game Over!", textFont, 40f);
    canvas.drawTextCentered("Press R to Restart", textFont, 0);
    canvas.end();
  }

  /**
   * Converts coordinate from world to screen
   *
   * @return screen coordinate
   *
   * @param pos the coordinate to be converted
   */
  // TODO: fix this to utilize the camera, right now collisioin looks sketchy with
  // this transformation.
  public Vector2 toScreenCoord(Vector2 pos) {
    return pos.scl(10f, 4f).sub(-300f, -45f);
  }

  public void clear() {
    renderables.clear();
  }

  /**
   * Resizes the viewport used by the RenderEngine.
   *
   * @param screenWidth  the width of the new screen in pixels
   * @param screenHeight the height of the new screen in pixels
   */
  public void resize(int screenWidth, int screenHeight) {
    viewport.update(screenWidth, screenHeight);
  }

  /** 
   * Returns the engine's viewport
   *
   * @return the engine's viewport
   */
  public Viewport getViewport() {
    return viewport;
  }

  @Override
  public String toString() {
    return "RenderingEngine [renderables=" + renderables + "]";
  }

}
