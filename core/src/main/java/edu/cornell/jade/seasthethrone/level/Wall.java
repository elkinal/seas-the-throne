package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Wall {
    public float x;
    public float y;
    public Array<Float> vertecies = new Array<>();


    public Wall(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void addVertex(float vertex) {
        vertecies.add(vertex);
    }

    public float[] toList() {
        float[] list = new float[vertecies.size];
        for (int i = 0; i < vertecies.size; i++) {
            list[i] = vertecies.get(i);
        }
        return list;
    }
}
