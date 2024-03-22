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
    private static int FRAMES_IN_ANIMATION;

    private Texture CRAB_SHOOTING;
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
     * @param builder builder for BossModel
     */
    public BossModel(Builder builder) {
        super(builder.x, builder.y);
        FRAMES_IN_ANIMATION = builder.FRAMES_IN_ANIMATION;
        CRAB_SHOOTING = builder.CRAB_SHOOTING;
        this.filmStrip = new FilmStrip(CRAB_SHOOTING, 1, FRAMES_IN_ANIMATION);
        frameCounter = 1;
        frameDelay = builder.frameDelay;

        BoxModel hitbox = new BoxModel(builder.x, builder.y, 5f, 10f);
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
    public static class Builder{
        /**boss x position */
        private float x;
        /**boss y position */
        private float y;

        /** Number of frames in boss animation */
        private int FRAMES_IN_ANIMATION;

        private Texture CRAB_SHOOTING;

        /** The number of frames between animation updates */
        private int frameDelay;

        public static Builder newInstance()
        {
            return new Builder();
        }

        private Builder() {}
        public Builder setX(float x){
            this.x = x;
            return this;
        }
        public Builder setY(float y){
            this.y = y;
            return this;
        }
        public Builder setFramesInAnimation(int frames){
            FRAMES_IN_ANIMATION = frames;
            return this;
        }
        public Builder setCrabShooting(Texture texture){
            CRAB_SHOOTING = texture;
            return this;
        }
        public Builder setFrameDelay(int frameDelay){
            this.frameDelay = frameDelay;
            return this;
        }
        public BossModel build(){
            return new BossModel(this);
        }
    }
}
