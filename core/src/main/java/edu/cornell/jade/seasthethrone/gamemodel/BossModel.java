package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class BossModel extends ComplexModel implements Renderable {

    /** Number of frames in boss animation TODO: stop hardcoding animation */
    private static int FRAMES_IN_ANIMATION = 4;

    //TODO: stop hardcoding textures
    private Texture CRAB_SHOOTING = new Texture("bosses/crab/crab_shoot.png");
    public FilmStrip filmStrip;

    /** The number of frames since this boss was inititalized */
    private int frameCounter;

    /** The number of frames between animation updates */
    private int frameDelay;

    /** The current position in the filmstrip */
    private int animationFrame;

    /**
     * {@link BossModel} constructor using an x and y coordinate.
     *
     * @param x The x-position for this boss in world coordinates
     * @param y The y-position for this boss in world coordinates
     */
    public BossModel(float x, float y) {
        super(x, y);

        this.filmStrip = new FilmStrip(CRAB_SHOOTING, 1, FRAMES_IN_ANIMATION);
        frameCounter = 1;
        frameDelay = 12;

        BoxModel hitbox = new BoxModel(x, y, 5f, 10f);
        hitbox.setBodyType(BodyDef.BodyType.KinematicBody);
        bodies.add(hitbox);

        CollisionMask.setCategoryMaskBits(this);
    }

    protected boolean createJoints(World world) {
        return true;
    }


    public void draw(RenderingEngine renderer) {
        int frame = getFrameNumber();
        FilmStrip filmStrip = getFilmStrip();
        filmStrip.setFrame(frame);
        Vector2 pos = getPosition();
        renderer.draw(filmStrip, pos.x, pos.y, 0.16f);

        if (frameCounter % frameDelay == 0) {
            setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
        }
        frameCounter +=1 ;
    }

    public int getFrameNumber() {
        return animationFrame;
    }

    public void setFrameNumber(int frameNumber) {
        animationFrame = frameNumber;
    }

    public FilmStrip getFilmStrip() {
        return filmStrip;
    }

    public int getFramesInAnimation() {
        return FRAMES_IN_ANIMATION;
    }
}
