package edu.cornell.jade.seasthethrone.gamemodel.gate;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.gamemodel.ObstacleModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;


public class GateModel extends ComplexModel implements Renderable {
    /*
    * GATE CLASS
    * State:
    *   - Model sensor, the body that checks if the player is "in" the room.
    *   - Array<ObstacleModel> walls, the walls that become active
    *   - boolean active: whether the walls are up and active. Should be set by the physics engine contact handler
    *               to true when sensor sees player and active is false, then set back to false when room clear conditions
    *               are met (prob when all enemies are dead)
    *   - boolean permaLower: whether the rooms conditions have been met and the gate should be permanently lowered.
    *
    *
    * Methods:
    * update(): checks if active flag has changed. If it has, it lower/raises the walls.
    * */

    private Array<GateSensorModel> sensors = new Array<>();
    private Array<GateWallModel> walls = new Array<>();
    private boolean active;
    private boolean permaLower;

    private Array<BossModel> bosses;

    private final float WORLD_SCALE;

    public GateModel(LevelObject obs, float worldScale) {
        this.WORLD_SCALE = worldScale;
        this.active = false;
        this.permaLower = false;

        for (LevelObject sObs : obs.sensors) {
            GateSensorModel sensor = new GateSensorModel(sObs);
            sensor.setSensor(true);
            this.sensors.add(sensor);
            bodies.add(sensor);
        }

        for (LevelObject wObs : obs.walls) {
            GateWallModel wall = new GateWallModel(wObs, WORLD_SCALE);
            wall.setBodyType(BodyDef.BodyType.StaticBody);
            this.walls.add(wall);
            bodies.add(wall);
        }
    }


    @Override
    protected boolean createJoints(World world) {
        return true;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        for (GateWallModel wall : walls) {
            wall.draw(renderer);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!active) {
            for (GateWallModel wall : walls) {
                if (wall.isActive()) { wall.setActive(false); }
            }
        }
    }

}
