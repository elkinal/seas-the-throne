package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public interface FishRenderable extends Renderable {

    public float angle();
    public default void draw(RenderingEngine renderer) {

        int frame = getFrameNumber();
        FilmStrip filmStrip = getFilmStrip();
        filmStrip.setFrame(frame);

        Vector2 pos = getPosition();
        renderer.draw(filmStrip, pos.x, pos.y, true, angle());

//    setFrameNumber((frame + 1) % getFramesInAnimation());


    }
}
