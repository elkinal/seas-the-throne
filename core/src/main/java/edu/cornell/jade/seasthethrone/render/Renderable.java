package edu.cornell.jade.seasthethrone.render;

public interface Renderable {

    float getX();
    float getY();

    default String getType(){
        return "Default";
    }
}

