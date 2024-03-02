package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class RenderingEngine {
  /** The Renderable objects for rendering */
  Array<Renderable> renderables;

  /** FIXME: stop hardcoding texture regions */
  private static final Texture PLAYER_TEXTURE = new Texture("spritebox_down.png");

  /** FIXME: stop hardcoding texture regions */
  private static final Texture FISH_TEXTURE = new Texture("spear_up_down.png");

  /**
   * Creates a new RenderingEngine
   */
  public RenderingEngine() {
    renderables = new Array<>();
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
  public void drawRenderables(GameCanvas canvas) {
    for (Renderable r : renderables) {
      if (r instanceof PlayerRenderable) {
        canvas.draw(PLAYER_TEXTURE, r.getX(), r.getY());
      } else if (r instanceof FishRenderable) {
        canvas.draw(FISH_TEXTURE, r.getX(), r.getY());
      }
    }
  }

  @Override
  public String toString() {
    return "RenderingEngine [renderables=" + renderables + "]";
  }

}
