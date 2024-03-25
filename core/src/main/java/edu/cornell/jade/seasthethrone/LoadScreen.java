package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class LoadScreen implements Screen {

    /** Reference to GameCanvas created by the root */
    private GameCanvas canvas;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Whether or not this player mode is still active */
    private boolean active;

    /** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
    private int budget;

    private int timer;

    /** Scaling factor for when the screen changes resolution. */
    private float scale;

    /** Background texture for start-up */
    private Texture background;

    /** Default budget for asset loader (do nothing but load 60 fps) */
    private static int DEFAULT_BUDGET = 100;

    public LoadScreen(GameCanvas canvas, int millis) {
        this.canvas = canvas;
        budget = millis;
        timer = 0;
        this.background = new Texture("levels/arenamaprough.png");
    }

    public LoadScreen(GameCanvas canvas) {
        this(canvas, DEFAULT_BUDGET);
    }

    /**
     * Update the status of this player mode.
     *
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     *
     * @param delta Number of seconds since last animation frame
     */
    private void update(float delta) {
        timer += 1;
    }


    private void draw() {
        canvas.begin();
        canvas.draw(background, Color.WHITE, 0, 0,
                0, 0, 0, 4f, 4f);
        canvas.end();
    }

    /**
     * Called when the Screen should render itself.
     *
     * We defer to the other methods update() and draw().  However, it is VERY important
     * that we only quit AFTER a draw.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void render(float delta) {
        if (active) {
            update(delta);
            draw();

            // We are are ready, notify our listener
            if (isReady() && listener != null) {
                System.out.println("timer "+this.timer);
                listener.exitScreen(this, 0);
            }
        }
    }

    /**
     * Returns true if all assets are loaded and the player is ready to go.
     *
     * @return true if the player is ready to go
     */
    public boolean isReady() {
        return timer >= budget;
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the scale for this mode
     */
    public void setScale(float scale) { this.scale = scale; }

    @Override
    public void show() {
        active = true;
    }

    @Override
    public void hide() {
        active = false;
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
