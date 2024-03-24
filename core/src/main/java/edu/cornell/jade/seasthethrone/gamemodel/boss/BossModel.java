package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public abstract class BossModel extends PolygonModel implements Renderable {
    protected FilmStrip filmStrip;

    /** The number of frames since this boss was inititalized */
    protected int frameCounter;

    /** The number of frames between animation updates */
    protected int frameDelay;

    /** The current position in the filmstrip */
    protected int animationFrame;

    protected float scale;

    /** Amount of knockback force applied to player on body collision */
    private float bodyKnockbackForce;

    /** Amount of knockback force applied to player on spear collision */
    private float spearKnockbackForce;

    /** Number of health points the boss has */
    protected int health;

    /**
     * {@link BossModel} constructor using an x and y coordinate.
     *
     * @param x The x-position for this boss in world coordinates
     * @param y The y-position for this boss in world coordinates
     */
    public BossModel(float[] points, float x, float y) {
        super(points, x, y);
        setBodyType(BodyDef.BodyType.StaticBody);
        bodyKnockbackForce = 70f;
        spearKnockbackForce = 130f;
    }

    public void draw(RenderingEngine renderer) {
        int frame = getFrameNumber();
        FilmStrip filmStrip = getFilmStrip();
        filmStrip.setFrame(frame);
        Vector2 pos = getPosition();
        renderer.draw(filmStrip, pos.x, pos.y, scale);

        if (frameCounter % frameDelay == 0) {
            setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
        }
        frameCounter +=1 ;
    }
    public float getBodyKnockbackForce() { return bodyKnockbackForce; }
    public float getSpearKnockbackForce() { return spearKnockbackForce; }

    /** Get remaining health points of the boss */
    public int getHealth() {
        return health;
    }

    /** Reduce boss HP by a specified amount
     *  If the boss dies, mark boss as removed */
    public void decrementHealth(int damage){
        health-= damage;
        if(health <= 0){
            markRemoved(true);
        }
    }

    public void setScale(float s) { scale = s; }

    public int getFrameNumber() {
        return animationFrame;
    }

    public void setFrameNumber(int frameNumber) {
        animationFrame = frameNumber;
    }

    public FilmStrip getFilmStrip() {
        return filmStrip;
    }

}
