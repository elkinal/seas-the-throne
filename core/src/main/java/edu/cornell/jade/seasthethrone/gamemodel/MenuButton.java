package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class MenuButton extends Actor implements Renderable {

    private final TextureRegion PAUSE_BUTTON_TEXTURE_REGION;

    public MenuButton(Texture texture) {
        PAUSE_BUTTON_TEXTURE_REGION = new TextureRegion(texture);
        setWidth(PAUSE_BUTTON_TEXTURE_REGION.getRegionWidth());
        setHeight(PAUSE_BUTTON_TEXTURE_REGION.getRegionHeight());
    }

    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        renderer.draw(PAUSE_BUTTON_TEXTURE_REGION, getX(), getY());
        System.out.println("bruh");
    }

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
}
