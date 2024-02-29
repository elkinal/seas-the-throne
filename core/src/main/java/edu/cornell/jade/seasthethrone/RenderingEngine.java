package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.utils.*;

public class RenderingEngine {
    Array<Renderable> renderables;
    public RenderingEngine(){
        renderables = new Array<>();
    }
    public void addRenderable(Renderable r){
        renderables.add(r);
    }
    public String getType(Renderable r){
        return r.getType();
    }
    public void drawRenderables(){

    }
}
