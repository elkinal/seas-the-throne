package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Obstacle {
    public float x;
    public float y;
    public float width;
    public float height;
    public TextureRegion texture;
    private TextureRegion DEFAULT_TEXTURE = new TextureRegion(new Texture("empty.png"));

    public Obstacle(float x, float y, float width, float height, TextureRegion texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public Obstacle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = DEFAULT_TEXTURE;
    }
}
