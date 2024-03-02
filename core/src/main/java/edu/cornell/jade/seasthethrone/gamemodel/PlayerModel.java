package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.PolygonModel;

public class PlayerModel extends ComplexModel {

    private boolean isDashing;

    private String pointSensorName;

    /**
     * Returns true if the player is dashing.
     *
     * @return true if the player is dashing.
     */
    public boolean isDashing() {
        return isDashing;
    }

    public PlayerModel(float x, float y){
        super(x, y);

        //make a triangle for now
        float vertices[] = new float[6];
        vertices[0] = -0.5f;
        vertices[1] = -1;
        vertices[2] = 0.5f;
        vertices[3] = -1;
        vertices[4] = 0;
        vertices[5] = 1;

        PolygonModel nose = new PolygonModel(vertices);
        nose.setName("nose");
        bodies.add(nose);

        pointSensorName = "NosePointSensor";

        // Create sensor on the points of the "nose," this should be factored to a diff function later
        Vector2 sensorCenter = new Vector2(0, 1);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.isSensor = true;
        CircleShape sensorShape = new CircleShape();
        //sensor has 0 radius, maybe modify?
        sensorShape.setRadius(0);
        sensorShape.setPosition(sensorCenter);
        sensorDef.shape = sensorShape;

        Fixture sensorFixture = body.createFixture( sensorDef );
        sensorFixture.setUserData(getPointSensorName());
    }

    /**
     * Returns the name of the nose point sensor
     *
     * This is used by ContactListener
     *
     * @return the name of the nose point sensor
     */
    public String getPointSensorName() {
        return pointSensorName;
    }

    // built from multiple polygonmodels?
    @Override
    protected boolean createJoints(World world) {
        // TODO:
        return false;
    }
}
