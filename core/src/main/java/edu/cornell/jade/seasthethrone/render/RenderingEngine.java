package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.*;

public class RenderingEngine {
  Array<Renderable> renderables;

  public RenderingEngine() {
    renderables = new Array<>();
  }

  public void addRenderable(Renderable r) {
    renderables.add(r);
  }

  public void drawRenderables(GameCanvas canvas) {
      //TODO
      for (Renderable r: renderables){
          Texture texture = null;
          canvas.draw(texture, r.getX(), r.getY());
                    throw new RuntimeException("unimplemented");
      }
  }
}
