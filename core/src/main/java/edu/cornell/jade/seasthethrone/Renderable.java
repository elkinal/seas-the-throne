package edu.cornell.jade.seasthethrone;

public interface Renderable {

    float getX();
    float getY();

    default String getType(){
        return "Default";
    }
}

