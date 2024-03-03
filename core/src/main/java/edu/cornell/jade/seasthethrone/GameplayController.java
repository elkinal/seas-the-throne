package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.jade.util.*;
import edu.cornell.jade.seasthethrone.PhysicsEngine;
import edu.cornell.jade.seasthethrone.model.*;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
// IMPORT INPUT CONTROLLER

public class GameplayController implements Screen {

    /** The Box2D world */
    protected PhysicsEngine physicsEngine;
    /** The boundary of the world */
    protected Rectangle bounds;
    /** The world scale */
    protected Vector2 scale;

    protected boolean active;

//    /** All the objects in the world. */
//    protected PooledList<Model> objects  = new PooledList<Model>();

    private PlayerModel player;

//    private void populateLevel() {
//        player = new PlayerModel(0,0);
//        objects.add(player);
//        physicsEngine.activatePhysics(player);
//    }


    public void show() {}

    public void render(float delta) {
        if (active) {
            update(delta);
        }
        draw(delta);
    }
    public void draw(float delta) {}
    public void update(float delta) {}

    public void resize(int width, int height) {}

    public void pause() {}

    public void resume() {}

    public void hide() {}

    public void dispose() {}
}
