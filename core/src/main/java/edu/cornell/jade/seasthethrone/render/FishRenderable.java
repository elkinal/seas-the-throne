package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public interface FishRenderable extends Renderable {
    /**
     * Returns the fish's texture for when they face north
     *
     * @return the fish's texture when facing north
     */
    public Texture getTextureNorth();
    /**
     * Returns the fish's texture for when they face northeast
     *
     * @return the fish's texture when facing northeast
     */
    public Texture getTextureNorthEast();
    /**
     * Returns the fish's texture for when they face northwest
     *
     * @return the fish's texture when facing northwest
     */
    public Texture getTextureNorthWest();
    /**
     * Returns the fish's texture for when they face west
     *
     * @return the fish's texture when facing west
     */
    public Texture getTextureWest();
    /**
     * Returns the fish's texture for when they face east
     *
     * @return the fish's texture when facing east
     */
    public Texture getTextureEast();
    /**
     * Returns the fish's texture for when they face southeast
     *
     * @return the fish's texture when facing southeast
     */
    public Texture getTextureSouthEast();
    /**
     * Returns the fish's texture for when they face southwest
     *
     * @return the fish's texture when facing southwest
     */
    public Texture getTextureSouthWest();
    /**
     * Returns the fish's texture for when they face south
     *
     * @return the fish's texture when facing south
     */
    public Texture getTextureSouth();
    public Direction direction();
    public default void draw(RenderingEngine renderer) {

        int frame = getFrameNumber();
        FilmStrip filmStrip = getFilmStrip();
        switch (direction()) {
            case UP:
                filmStrip.setTexture(getTextureNorth());
                break;
            case DOWN:
                filmStrip.setTexture(getTextureSouth());
                break;
            case LEFT:
                filmStrip.setTexture(getTextureWest());
                break;
            case RIGHT:
                filmStrip.setTexture(getTextureEast());
                break;
            case NE:
                filmStrip.setTexture(getTextureNorthEast());
                break;
            case NW:
                filmStrip.setTexture(getTextureNorthWest());
                break;
            case SE:
                filmStrip.setTexture(getTextureSouthEast());
                break;
            case SW:
                filmStrip.setTexture(getTextureSouthWest());
                break;
        }
        filmStrip.setFrame(frame);

        Vector2 pos = getPosition();
        renderer.draw(filmStrip, pos.x, pos.y);

//    setFrameNumber((frame + 1) % getFramesInAnimation());


    }
}
