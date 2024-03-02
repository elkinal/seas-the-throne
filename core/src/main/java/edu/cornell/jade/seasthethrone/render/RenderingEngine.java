package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.*;

public class RenderingEngine {
    /**The Renderable objects for rendering*/
    Array<Renderable> renderables;

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
     * @param canvas  Drawing Context
     */
    public void drawRenderables(GameCanvas canvas) {
        //TODO
        for (Renderable r : renderables) {
            Texture texture = null;
            canvas.draw(texture, r.getX(), r.getY());
            throw new RuntimeException("unimplemented");
        }
    }
}
