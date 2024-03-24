package edu.cornell.jade.seasthethrone.gamemodel;

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

public class MenuButton extends BoxModel implements Renderable {

    private final TextureRegion pauseButtonTextureRegion;
    private boolean display;

    private final float offsetX;
    private final float offsetY;

    private final Rectangle boundingBox;
    private float rectangleX;
    private float rectangleY;

    public MenuButton(float x, float y, float width, float height, float offsetX, float offsetY, Texture texture) {

        // Loads textures
        super(x, y, width, height);
        pauseButtonTextureRegion = new TextureRegion(texture);

        // Setting parameters
        setActive(false);
        display = true;

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        // Setting the bounding Rectangle
        rectangleX = getX() - getWidth() / 2;
        rectangleY = getY() - getHeight() / 2;

        boundingBox = new Rectangle(rectangleX, rectangleY, width, height);
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

        boundingBox.setX(rectangleX);
        boundingBox.setY(rectangleY);

    }

    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        if (display)
            renderer.draw(pauseButtonTextureRegion, getX(), getY());
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
        display = true;
    }

    /** Shows the button */
    public void hide() {
        display = false;
    }

    /** Returns a rectangle covering the button's outline */
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

}
