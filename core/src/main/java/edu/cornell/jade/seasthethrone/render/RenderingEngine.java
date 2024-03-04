package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class RenderingEngine {
  /**
   * The Renderable objects for rendering
   */
  Array<Renderable> renderables;

  /**
   * Internal game canvas to handle rendering
   * TODO: REPLACE WITH HOMEBREWED THING
   */
  GameCanvas canvas;

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
   * Creates a new RenderingEngine
   */
  public RenderingEngine() {
    renderables = new Array<>();
    canvas = new GameCanvas();
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
   *
   * @param canvas Drawing Context
   */
  public void drawRenderables() {
    canvas.begin();
    for (Renderable r : renderables) {
      float x = toScreenCoord(r.getX());
      float y = toScreenCoord(r.getY());
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
        canvas.draw(filmStrip, x, y);
        canvas.draw(speartexture, spearx, speary);
        ((PlayerRenderable) r).updateAnimationFrame();
      } else if (r instanceof FishRenderable) {
        canvas.draw(FISH_TEXTURE, x, y);
      }
    }
    canvas.end();
  }

  /**
   * Converts coordinate from world to screen
   *
   * @return screen coordinate
   *
   * @param x the coordinate to be converted
   */
  public float toScreenCoord(float x) {
    return x;
  }

  @Override
  public String toString() {
    return "RenderingEngine [renderables=" + renderables + "]";
  }

}
