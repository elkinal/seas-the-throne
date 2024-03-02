package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.PlayerModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.util.*;
// DO NOT IMPORT GameplayController

public class PhysicsEngine implements ContactListener {

    /** All the objects in the world. */
    protected PooledList<Model> objects  = new PooledList<Model>();
    /** The Box2D world */
    private World world;
    /** The boundary of the world */
    private Rectangle bounds;
    /** The world scale */
    private Vector2 scale;
    /** The player */
    private PlayerModel player;

    public PhysicsEngine(Rectangle bounds, Vector2 gravity){

        // not sure if its gonna make sense to have some concept of gravity
        world = new World(gravity, false);
        this.bounds = new Rectangle(bounds);
        this.scale = new Vector2(1, 1);
        world.setContactListener(this);
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );
        objects.clear();
        world.dispose();

        world = new World(gravity,false);
        world.setContactListener(this);
        setupWorld();
    }

    private void setupWorld(){
        player = new PlayerModel(0, 0);
        addObject(player);

        BulletModel bullet = new BulletModel(10, 10, 5);
        bullet.createFixtures();
        addObject(bullet);
    }
    public void update(float delta){
        // Pretend we got some input

    }

    /**
     * Immediately adds the object to the physics world
     *
     * @param obj The object to add
     */
    protected void addObject(Model obj) {
        assert inBounds(obj) : "Object is not in bounds";
        objects.add(obj);
    }

    /**
     * Creates the physics body for this model and adds it to the physics engine.
     *
     * @param obj Model to be added to the world
     * TODO: Finish this implementation; must call world.createBody(BodyDef).
     * */
    public void activatePhysics(Model obj) {
        obj.setActive(true);

    }

    /**
     * Returns true if the object is in bounds.
     *
     * This assertion is useful for debugging the physics.
     *
     * @param obj The object to check.
     *
     * @return true if the object is in bounds.
     */
    public boolean inBounds(Model obj) {
        boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
        boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
        return horiz && vert;
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
