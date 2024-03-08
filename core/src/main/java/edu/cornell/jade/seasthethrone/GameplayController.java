package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.*;

import edu.cornell.jade.util.*;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.model.*;
import edu.cornell.jade.seasthethrone.input.PlayerController;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.cornell.jade.seasthethrone.render.Renderable;

public class GameplayController implements Screen {

  /** Track state of the game */
  public enum GameState {
    /** While we are playing the game */
    PLAY,
    /** When the ships is dead (but shells still work) */
    OVER
  }

  private GameState gameState;
  Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
  /** Sub-controller for collecting input */
  InputController inputController;
  /** Sub-controller for handling updating physics engine based on input */
  PlayerController playerController;
  /** Rendering Engine */
  RenderingEngine renderEngine;
  /** Width of the game world in Box2d units */
  protected static final float DEFAULT_WIDTH = 64.0f;
  /** Height of the game world in Box2d units */
  protected static final float DEFAULT_HEIGHT = 48.0f;
  /** Ratio between the pixel in a texture and the meter in the world */ 
  private static final float WORLD_SCALE = 0.1f;

  /** The Box2D world */
  protected PhysicsEngine physicsEngine;
  /** The boundary of the world */
  protected Rectangle bounds;

  /** Viewport maintaining relation between screen and world coordinates */
  private FitViewport viewport;

  protected boolean active;

  /** All the objects in the world. */
  protected PooledList<Model> objects = new PooledList<Model>();

  protected GameplayController() {
    gameState = GameState.PLAY;
    bounds = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    physicsEngine = new PhysicsEngine(bounds);
    physicsEngine.reset();

    this.viewport = new FitViewport(DEFAULT_WIDTH, DEFAULT_HEIGHT);

    active = false;

    this.inputController = new InputController(viewport);
    this.playerController = new PlayerController(physicsEngine);
    this.renderEngine = new RenderingEngine(DEFAULT_WIDTH, DEFAULT_HEIGHT, viewport, WORLD_SCALE);

    this.renderEngine.addRenderable(physicsEngine.getPlayerModel());
    this.inputController.add(playerController);
  }

  public void show() {
    active = true;
  }

  public void render(float delta) {
    if (active) {
      update(delta);
    }
    // draw(delta);
  }

  public void draw(float delta) {
    renderEngine.drawBackground();
    renderEngine.drawRenderables();
  }

  public void update(float delta) {
    viewport.apply();
    inputController.update();

    // Right now just errors if you try to update playerController or physicsEngine
    // when player is null
    if (gameState != GameState.OVER) {
      playerController.update();
      physicsEngine.update(delta);
    }
    physicsEngine.getWorld().step(delta, 8, 4);

    if (!physicsEngine.isAlive()) {
      gameState = GameState.OVER;
    }

    draw(delta);
    debugRenderer.render(physicsEngine.getWorld(), renderEngine.getViewport().getCamera().combined);
    renderEngine.clear();
    for (Model obj : physicsEngine.getObjects()) {
      if (obj instanceof Renderable r)
        renderEngine.addRenderable(r);
    }
    if (gameState == GameState.OVER) {
      if (inputController.didReset()) {
        gameState = GameState.PLAY;
        physicsEngine.reset();
      } else {
        renderEngine.drawGameOver();
      }
    }
  }

  public void resize(int width, int height) {
    viewport.update(width, height);
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
