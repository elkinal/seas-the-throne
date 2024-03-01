package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.PolygonModel;

public class PlayerModel extends ComplexModel {

    private boolean isDashing;

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
    }

    // built from multiple polygonmodels?
    @Override
    protected boolean createJoints(World world) {
        // TODO:
        return false;
    }
}
