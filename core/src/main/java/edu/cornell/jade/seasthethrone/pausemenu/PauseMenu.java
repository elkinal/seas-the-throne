package edu.cornell.jade.seasthethrone.pausemenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;
import org.w3c.dom.Text;

public class PauseMenu extends BoxModel implements Renderable {

    private boolean paused;

    // Defines the location
    private final float offsetX;
    private final float offsetY;

    // Used when a button only has one texture
    private final Texture scrollTexture = new Texture("pause_menu/pausescreen.png");

    private final TextureRegion scrollTextureRegion;
    private final TextureRegion backgroundTextureRegion;

    // Enum to select menu option
    private int currentTexture = MenuSelection.RESUME.label;

    public enum MenuSelection {
        RESUME(0),
        RESTART(1),
        LEVEL_SELECT(2),
        QUIT(3);

        public final int label;

        private MenuSelection(int label) {
            this.label = label;
        }
    }


    /** Constructor for the Pause Menu */
    public PauseMenu(float x, float y, float width, float height, float offsetX, float offsetY, Viewport viewport) {

        super(x, y, width, height);

        // Setting parameters
        setActive(false);

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        // Loading scroll texture
        scrollTextureRegion = new TextureRegion(scrollTexture);

        // Creating dark background texture
        Pixmap pixmap = new Pixmap(viewport.getScreenWidth()/2, viewport.getScreenHeight()/2, Pixmap.Format.RGBA8888);
        Color backgroundColor = new Color(0, 0, 0, 0.55f);
        pixmap.setColor(backgroundColor);
        pixmap.fill();
        Texture backgroundTexture = new Texture(pixmap);
        backgroundTextureRegion = new TextureRegion(backgroundTexture);

    }

    /** Updates the button's position on the game canvas */
    public void updatePosition(Viewport viewport) {
        float newX = viewport.getScreenWidth() + offsetX;
        float newY = offsetY;
        Vector2 newLoc = viewport.unproject(new Vector2(newX, newY));
        setPosition(newLoc);
    }


    /** Returns true if the menu is paused */
    public boolean isPaused() {
        return paused;
    }

    /** Displays the menu when the game is paused */
    public void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused)
            currentTexture = MenuSelection.RESUME.label;
    }

    /** Switches to a lower menu item */
    public void cycleDown() {
        if (paused && currentTexture < 3)
            currentTexture++;
    }

    /** Switches to a higher menu item */
    public void cycleUp() {
        if (paused && currentTexture > 0)
            currentTexture--;
    }

    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        if (paused) {
            renderer.draw(backgroundTextureRegion, getX(), getY());
            renderer.draw(scrollTextureRegion, getX(), getY());
        }
    }

    @Override
    public int getFrameNumber() {
        return 0;
    }

    @Override
    public void setFrameNumber(int frameNumber) { }

    @Override
    public FilmStrip getFilmStrip() {
        return null;
    }

    @Override
    public int getFramesInAnimation() {
        return 0;
    }
}
