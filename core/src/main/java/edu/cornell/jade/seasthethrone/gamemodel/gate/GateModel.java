package edu.cornell.jade.seasthethrone.gamemodel.gate;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.ComplexModel;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;


public class GateModel extends ComplexModel implements Renderable {

    /** The sensors that raise this gate when the player contacts them */
    private Array<GateSensorModel> sensors = new Array<>();

    /** Box models that activate when the gate is raised */
    private Array<GateWallModel> walls = new Array<>();

    /** If this gate is raised */
    private boolean raised;

    /** If this gate should be permanently lowered, even when the sensor is activated */
    private boolean permaLower;

    /** The bosses this gate tracks. The gate will lower when all tracked bosses are dead */
    private Array<BossModel> bosses;

    /** World scale to render gate texture */
    private final float WORLD_SCALE;

    public GateModel(LevelObject obs, float worldScale) {
        this.WORLD_SCALE = worldScale;
        this.raised = false;
        this.permaLower = false;
        this.bosses = new Array<>();

        for (LevelObject sObs : obs.sensors) {
            GateSensorModel sensor = new GateSensorModel(sObs);
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

    public Array<GateWallModel> getWalls() { return this.walls; }

    @Override
    protected boolean createJoints(World world) {
        return true;
    }

    @Override
    public void draw(RenderingEngine renderer) {
        if (raised) {
            for (GateWallModel wall : walls) {
                wall.draw(renderer);
            }
        }
    }

    @Override
    public void progressFrame() {
    }

    @Override
    public void alwaysUpdate() {

    }

    @Override
    public void neverUpdate() {

    }

    @Override
    public void setAlwaysAnimate(boolean animate) {

    }

    @Override
    public boolean alwaysAnimate() {
        return false;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // If a sensor has been activated by the player, activate this gate
        for (GateSensorModel sensor : sensors) {
            if (sensor.isRaised()) {
                activate();
                return;
            }
        }
        for (GateWallModel wall : walls) {
            if (!raised) {
                wall.setActive(false);
            }
        }
        permaLower = allBossesDead();
        // If this gate is inactive, reset all walls and sensors to deactivated state
        if (permaLower) {
            deactivate();
        }
    }

    /** Returns if this gate active */
    public boolean isRaised() { return raised; }

    /** Sets this gate to active and sets all its walls to active in the physics engine */
    public void activate() {
        if (!permaLower) {
            this.raised = true;

            for (GateWallModel wall : walls) {
                wall.setActive(true);
            }

            for (GateSensorModel sensor : sensors) {
                sensor.setRaised(false);
            }
        }
    }

    /** Resets all sensors and walls to inactive */
    public void deactivate(){
        raised = false;
        for (GateWallModel wall : walls) {
            wall.setActive(false);
        }
        for (GateSensorModel sensor : sensors) {
            sensor.setRaised(false);
        }
    }

    /** Adds a boss to the bosses tracked by this gate */
    public void addBoss(BossModel bm) {
        this.bosses.add(bm);
    }

    /** Returns if all the bosses tracked by this gate are dead */
    private boolean allBossesDead() {
        for (BossModel bm : bosses) {
            if (!bm.isDead()) {
                return false;
            }
        }
        return true;
    }

}
