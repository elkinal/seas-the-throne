package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.SimpleModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

import javax.sound.sampled.Port;

public class PortalModel extends BoxModel implements Renderable {

    /** Portal texture */
    private TextureRegion texture;


    private final TextureRegion DEFUALT_TEXTURE = new TextureRegion(new Texture("empty.png"));

    public PortalModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        setSensor(true);
        this.texture = DEFUALT_TEXTURE;
    }

    public PortalModel(float x, float y, float width, float height, TextureRegion texture) {
        this(x, y, width, height);
        this.texture = texture;
    }

    @Override
    protected void createFixtures() {

    }

    @Override
    protected void releaseFixtures() {

    }

    @Override
    public void draw(RenderingEngine renderer) {
        renderer.draw(texture, getX(), getY());
    }

    @Override
    public int getFrameNumber() {
        return 0;
    }

    @Override
    public void setFrameNumber(int frameNumber) {}

    @Override
    public FilmStrip getFilmStrip() {
        return null;
    }

    @Override
    public int getFramesInAnimation() {
        return 0;
    }
}
