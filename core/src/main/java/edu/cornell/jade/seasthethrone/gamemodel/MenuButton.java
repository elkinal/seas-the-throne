package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class MenuButton extends BoxModel implements Renderable {

    private final TextureRegion PAUSE_BUTTON_TEXTURE_REGION;
    private boolean display;

    private final float offsetX;
    private final float offsetY;

    public MenuButton(float x, float y, float width, float height, float offsetX, float offsetY, Texture texture) {
        super(x, y, width, height);
        PAUSE_BUTTON_TEXTURE_REGION = new TextureRegion(texture);

        setActive(false);
        display = true;

        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /** Updates the button's position on the game canvas */
    public void updatePosition(Viewport viewport) {

        // magic numbers represent offset from the edges of the screen.
        // they depend on the size of the graphics object.

        float newX = Gdx.graphics.getWidth() + offsetX;
        float newY = offsetY;

        Vector2 newLoc = viewport.unproject(new Vector2(newX, newY));

        setX(newLoc.x);
        setY(newLoc.y);

    }

    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        if (display)
            renderer.draw(PAUSE_BUTTON_TEXTURE_REGION, getX(), getY());
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
        float x = getX() - getWidth() / 2;
        float y = getY() - getHeight() / 2;

        return new Rectangle(x, y, getWidth(), getHeight());
    }

}
