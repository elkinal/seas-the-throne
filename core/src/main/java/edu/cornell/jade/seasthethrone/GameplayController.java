package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.*;

import edu.cornell.jade.util.*;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.model.*;
import edu.cornell.jade.seasthethrone.input.PlayerController;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.math.Matrix4;
// IMPORT INPUT CONTROLLER

public class GameplayController implements Screen {
  Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
  Matrix4 cam = new Matrix4().scl(0.2f);
  /** Sub-controller for collecting input */
  InputController inputController;
  /** Sub-controller for handling updating physics engine based on input */
  PlayerController playerController;
  /** Rendering Engine */
  RenderingEngine renderEngine;
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

  protected GameplayController() {
    bounds = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    physicsEngine = new PhysicsEngine(bounds);
    physicsEngine.reset();

    this.scale = new Vector2(1, 1);
    active = false;

    this.inputController = new InputController();
    this.playerController = new PlayerController(physicsEngine);
    this.renderEngine = new RenderingEngine();
    renderEngine.addRenderable(physicsEngine.getPlayerModel());
  }

  public void show() {
    active = true;
  }

  public void render(float delta) {
    if (active) {
      update(delta);
    }
    draw(delta);
    debugRenderer.render(physicsEngine.getWorld(), cam);
  }

  public void draw(float delta) {
    renderEngine.drawRenderables();
  }

  public void update(float delta) {
    inputController.update();
    playerController.update();
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
