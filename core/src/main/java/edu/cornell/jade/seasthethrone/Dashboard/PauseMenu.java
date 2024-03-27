package edu.cornell.jade.seasthethrone.Dashboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class PauseMenu extends BoxModel implements Renderable {

    private boolean visible;

    // Defines the location
    private final float offsetX;
    private final float offsetY;

    // Used when a button only has one texture
    private final Texture pauseMenuTexture1 = new Texture("pause_menu/pausescreendisplay1.png");
    private final Texture pauseMenuTexture2 = new Texture("pause_menu/pausescreendisplay2.png");
    private final Texture pauseMenuTexture3 = new Texture("pause_menu/pausescreendisplay3.png");
    private final Texture pauseMenuTexture4 = new Texture("pause_menu/pausescreendisplay4.png");

    private TextureRegion[] pauseButtonTextureRegions;

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


    /** 1 or more textures may be passed as arguments */
    public PauseMenu(float x, float y, float width, float height, float offsetX, float offsetY) {

        // Loads textures
        super(x, y, width, height);

        // Setting parameters
        setActive(false);
        visible = true;

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        // Loading textures
        pauseButtonTextureRegions = new TextureRegion[] {
                new TextureRegion(pauseMenuTexture1),
                new TextureRegion(pauseMenuTexture2),
                new TextureRegion(pauseMenuTexture3),
                new TextureRegion(pauseMenuTexture4)
        };
    }

    /** Updates the button's position on the game canvas */
    public void updatePosition(Viewport viewport) {

        // Updating the position
        float newX = Gdx.graphics.getWidth() + offsetX;
        float newY = offsetY;

        Vector2 newLoc = viewport.unproject(new Vector2(newX, newY));

        setX(newLoc.x);
        setY(newLoc.y);

        // Updating the rectangle
        float rectangleX = getX() - getWidth() / 2;
        float rectangleY = getY() - getHeight() / 2;

    }

    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        if (visible) {
            renderer.draw(pauseButtonTextureRegions[currentTexture], getX(), getY());
        }
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


    /** Hides the button */
    public void show() {
        visible = true;
    }

    /** Shows the button */
    public void hide() {
        visible = false;
    }

    /** Returns true if the menu is visible */
    public boolean isVisible() {
        return visible;
    }

    /** Sets the visibility of the button */
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (!visible)
            currentTexture = MenuSelection.RESUME.label;
    }

    /** Switches to a lower menu item */
    public void cycleDown() {
        if (visible && currentTexture < 3)
            currentTexture++;
    }

    /** Switches to a higher menu item */
    public void cycleUp() {
        if (visible && currentTexture > 0)
            currentTexture--;
    }
}
