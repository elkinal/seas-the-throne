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

  /** Width of the game world in Box2d units */
  protected static final float DEFAULT_WIDTH = 32.0f;
  /** Height of the game world in Box2d units */
  protected static final float DEFAULT_HEIGHT = 18.0f;

  /** The Box2D world */
  protected PhysicsEngine physicsEngine;
  /** The boundary of the world */
  protected Rectangle bounds;
  /** The world scale */
  protected Vector2 scale;

  protected boolean active;

  /** All the objects in the world. */
  protected PooledList<Model> objects = new PooledList<Model>();

  private PlayerModel player;

  private void populateLevel() {
    player = new PlayerModel(0, 0);
    objects.add(player);
  }

  protected GameplayController() {
    bounds = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    physicsEngine = new PhysicsEngine(bounds);
    physicsEngine.reset();

    this.scale = new Vector2(1, 1);
    active = false;
  }

  public void show() {
    active = true;
  }

  public void render(float delta) {
    if (active) {
      update(delta);
    }
    draw(delta);
  }

  public void draw(float delta) {
  }

  public void update(float delta) {
    physicsEngine.update(delta);
  }

  public void resize(int width, int height) {
  }

  public void pause() {
  }

  public void resume() {
  }

  public void hide() {
    active = false;
  }

  public void dispose() {
  }
}
