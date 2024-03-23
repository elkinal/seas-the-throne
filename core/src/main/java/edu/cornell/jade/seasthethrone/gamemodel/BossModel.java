package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class BossModel extends ComplexModel implements Renderable {

    /** Number of frames in boss animation TODO: stop hardcoding animation */
    private int frameSize;

    private FilmStrip shootAnimation;
    private FilmStrip idleAnimation;
    private FilmStrip moveAnimation;
    private FilmStrip getHitAnimation;
    private FilmStrip dieAnimation;
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
        frameSize = builder.frameSize;
        shootAnimation = builder.shootAnimation;
        idleAnimation = builder.idleAnimation;
        moveAnimation = builder.moveAnimation;
        getHitAnimation = builder.getHitAnimation;
        dieAnimation = builder.dieAnimation;
        this.filmStrip = shootAnimation;
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
        return filmStrip.getSize();
    }
    public static class Builder{
        /**boss x position */
        private float x;
        /**boss y position */
        private float y;

        /** Number of frames in boss animation */
        private int frameSize;

        private FilmStrip shootAnimation;
        private FilmStrip idleAnimation;
        private FilmStrip moveAnimation;
        private FilmStrip getHitAnimation;
        private FilmStrip dieAnimation;

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
        public Builder setFrameSize(int frameSize){
            this.frameSize = frameSize;
            return this;
        }
        public Builder setShootAnimation(Texture texture){
            int width = texture.getWidth();
            shootAnimation = new FilmStrip(texture, 1, width/frameSize);;
            return this;
        }
        public Builder setIdleAnimation(Texture texture){
            int width = texture.getWidth();
            idleAnimation = new FilmStrip(texture, 1, width/frameSize);;
            return this;
        }
        public Builder setGetHitAnimation(Texture texture){
            int width = texture.getWidth();
            getHitAnimation = new FilmStrip(texture, 1, width/frameSize);;
            return this;
        }
        public Builder setMoveAnimation(Texture texture){
            int width = texture.getWidth();
            moveAnimation = new FilmStrip(texture, 1, width/frameSize);;
            return this;
        }
        public Builder setDieAnimation(Texture texture){
            int width = texture.getWidth();
            dieAnimation = new FilmStrip(texture, 1, width/frameSize);;
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
