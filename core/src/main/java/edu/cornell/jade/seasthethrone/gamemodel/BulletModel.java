package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.SimpleModel;

/**
 * Model for the game bullet objects. This class extends
 * {@link SimpleModel} to be represented as a single body (with circular shape).
 * This is subject to change later as we design bullets with more complex shapes.
 */
public class BulletModel extends SimpleModel {

    /** Shape of the bullet, modeled as a circle */
    public CircleShape shape;

    /** Fixture to be attached to the body */
    private Fixture geometry;

    /**
     * {@link BulletModel} constructor using an x and y coordinate & radius.
     * NOTE: as of now, you must call activatePhysics then createFixtures
     * after constructing the BulletModel for it to be properly created.
     *
     * @param x         The x-position for this bullet in world coordinates
     * @param y         The y-position for this bullet in world coordinates
     * @param radius    The radius of this bullet
     */
    public BulletModel(float x, float y, float radius){
        super(x, y);
        shape = new CircleShape();
        shape.setRadius(radius);
    }

    /**
     * Create new fixtures for this body, defining the shape
     *
     * This is the primary method to override for custom physics objects
     */
    public void createFixtures() {
        if (body == null) {
            return;
        }

        releaseFixtures();

        // Create the fixture
        fixture.shape = shape;
        geometry = body.createFixture(fixture);
        markDirty(false);
    }

    /**
     * Release the fixtures for this body, reseting the shape
     *
     * This is the primary method to override for custom physics objects
     */
    public void releaseFixtures() {
        if (geometry != null) {
            body.destroyFixture(geometry);
            geometry = null;
        }
    }

}
