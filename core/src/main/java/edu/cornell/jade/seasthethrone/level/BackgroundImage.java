package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;


public class BackgroundImage {
    private float x;
    private float y;
    private Texture texture;
    private float opacity;

    public BackgroundImage(HashMap<String, Object> bgLayer) {
        x = Float.parseFloat((String)bgLayer.get("x"));
        y = Float.parseFloat((String)bgLayer.get("y"));
        texture = new Texture((String)bgLayer.get("image"));
        opacity = Float.parseFloat((String)bgLayer.get("opacity"));;
    }

    public float getX() { return x; }

    public float getY() { return y; }

    public Texture getTexture() { return texture; }
    public float getOpactiy() { return opacity; }
}
