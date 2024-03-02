package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class RenderingEngine {
  Array<Renderable> renderables;

  /** FIXME: stop hardcoding texture regions */
  private static final Texture PLAYER_TEXTURE = new Texture("spritebox_down.png");
  /** FIXME: stop hardcoding texture regions */
  private static final Texture FISH_TEXTURE = null;

  public RenderingEngine() {
    renderables = new Array<>();
  }

  public void addRenderable(Renderable r) {
    renderables.add(r);
  }

  public void drawRenderables(GameCanvas canvas) {
    for (Renderable r : renderables) {
      if (r instanceof PlayerRenderable) {
        canvas.draw(PLAYER_TEXTURE, r.getX(), r.getY());
      } else if (r instanceof FishRenderable) {
        canvas.draw(FISH_TEXTURE, r.getX(), r.getY());
      }
    }
  }
}
