package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;
import org.w3c.dom.Text;

public class LevelObject implements Renderable {
    public enum LevelObjType {
        PORTAL,
        WALL,
        OBSTACLE,
    }

    public float x;
    public float y;
    public float width;
    public float height;
    public TextureRegion texture;

    public LevelObjType type;
    public Array<Float> vertecies = new Array<>();
    private final TextureRegion DEFAULT_TEXTURE = new TextureRegion(new Texture("empty.png"));

    public Vector2 pos;

    public LevelObject(float x, float y) {
        this.x = x;
        this.y = y;
        this.pos = new Vector2(x, y);
        this.texture = DEFAULT_TEXTURE;
    }

    public LevelObject(float x, float y, TextureRegion texture) {
        this(x,y);
        this.texture = texture;
    }

    public LevelObject(float x, float y, Array<Float> vertecies) {
        this(x,y);
        this.vertecies = vertecies;
    }

    public LevelObject(float x, float y, float width, float height) {
        this(x, y);
        this.width = width;
        this.height = height;
    }

    public LevelObject(float x, float y, float width, float height, TextureRegion texture) {
        this(x, y, width, height);
        this.texture = texture;
    }

    public void setType(LevelObjType type) { this.type = type; }

    @Override
    public Vector2 getPosition() {
        return pos;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        renderer.draw(texture, getPosition().x, getPosition().y);
    }

    public void setTexture(TextureRegion texture) { this.texture = texture; }

    @Override
    public int getFrameNumber() {
        return 0;
    }

    @Override
    public void setFrameNumber(int frameNumber) {

    }

    @Override
    public FilmStrip getFilmStrip() {
        return null;
    }

    @Override
    public int getFramesInAnimation() {
        return 0;
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
