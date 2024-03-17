package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

import java.util.HashMap;
import java.util.Vector;


public class BackgroundImage implements Renderable {
    private Vector2 position;
    private int width;
    private int height;
    private TextureRegion texture;
    private float opacity;

    private Array<HashMap<String, Object>> properties;

    public BackgroundImage(HashMap<String, Object> bgLayer) {
        float x = Float.parseFloat((String)bgLayer.get("offsetx"));
        float y = Float.parseFloat((String)bgLayer.get("offsety"));
        position = new Vector2(x, y);
        texture = new TextureRegion(new Texture((String)bgLayer.get("image")));
        opacity = Float.parseFloat((String)bgLayer.get("opacity"));

        properties = (Array<HashMap<String, Object>>) bgLayer.get("properties");
        height = Integer.parseInt((String)getProperty("height").get("value"));
        width = Integer.parseInt((String)getProperty("width").get("value"));
    }

    public void draw(RenderingEngine renderer) {
        renderer.draw(getTexture(), getPosition().x, getPosition().y);
    }
    public Vector2 getPosition() { return position;}

    public void setPosition(Vector2 pos) { position = pos;}

    public TextureRegion getTexture() { return texture; }
    public float getOpactiy() { return opacity; }

    /**
     * Returns the layer with the given name
     *
     * @param propName the name of the layer to return
     *
     * @return the layer with the given name
     *
     * @throws Error if the provided name doesn't match any layer in the level
     * */
    private HashMap<String, Object> getProperty(String propName) {
        for (HashMap<String, Object> prop : properties) {
            if (( (String)prop.get("name") ).equals(propName)) {
                return prop;
            }
        }
        throw new Error("No layer with name " + propName);
    }

    public int getFrameNumber() {
        return 0;
    }

    public void setFrameNumber(int frameNumber) {

    }

    public FilmStrip getFilmStrip() {
        return null;
    }
    public int getFramesInAnimation() {
        return 0;
    }
}

