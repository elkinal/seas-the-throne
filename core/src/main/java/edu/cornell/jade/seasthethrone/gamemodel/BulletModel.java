package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import edu.cornell.jade.seasthethrone.model.SimpleModel;

public class BulletModel extends SimpleModel {

    // MODELING BULLETS AS A CIRCLE TEMPORARILY
    public CircleShape shape;
    
    private Fixture geometry;

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
