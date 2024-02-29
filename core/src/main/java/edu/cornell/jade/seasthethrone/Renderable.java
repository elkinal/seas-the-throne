package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.physics.box2d.World;

public class Renderable extends Obstacle{
    RenderingEngine renderingEngine = new RenderingEngine();
    @Override
    public void draw(GameCanvas canvas) {
        renderingEngine.addRenderable(this);
    }

    public float getX(){
        return 0;
    }
    public float getY(){
        return 0;
    }
    public void printPosition(){

    }

}
