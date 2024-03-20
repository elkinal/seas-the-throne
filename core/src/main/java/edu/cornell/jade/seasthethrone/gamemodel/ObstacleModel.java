package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.level.Obstacle;
import edu.cornell.jade.seasthethrone.level.Wall;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import edu.cornell.jade.seasthethrone.model.SimpleModel;
import edu.cornell.jade.seasthethrone.render.Renderable;

public class ObstacleModel extends BoxModel {

    public ObstacleModel(Wall wall) {
        super(wall.x, wall.y, wall.width, wall.height);

    }

    public ObstacleModel(float x, float y, float width, float height) {
        super(x,y,width,height);
    }

    protected boolean createJoints(World world) {
        return true;
    }
    protected void createFixtures() {

    }
    protected void releaseFixtures() {

    }
}
