package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.*;

import edu.cornell.jade.util.*;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.model.*;
import edu.cornell.jade.seasthethrone.input.PlayerController;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
// IMPORT INPUT CONTROLLER

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
    gameState = GameState.PLAY;
    bounds = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    physicsEngine = new PhysicsEngine(bounds);
    physicsEngine.reset();

    this.scale = new Vector2(1, 1);
    active = false;

    this.inputController = new InputController();
    this.playerController = new PlayerController(physicsEngine);
    this.renderEngine = new RenderingEngine(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    renderEngine.addRenderable(physicsEngine.getPlayerModel());

    this.inputController.add(playerController);
  }

  public void show() {
    active = true;
  }

  public void render(float delta) {
    if (active) {
      update(delta);
    }
    //draw(delta);
  }

  public void draw(float delta) {
    renderEngine.drawRenderables();
  }

  public void update(float delta) {

    inputController.update();

    // Right now just errors if you try to update playerController or physicsEngine when player is null
    if(gameState != GameState.OVER){
      playerController.update();
      physicsEngine.update(delta);
    }
    physicsEngine.getWorld().step(delta, 8, 4);

    if(!physicsEngine.isAlive()){
      gameState = GameState.OVER;
    }

    draw(delta);
    debugRenderer.render(physicsEngine.getWorld(), renderEngine.getViewport().getCamera().combined);
    renderEngine.clear();
    for (Model obj: physicsEngine.getObjects()){
      renderEngine.addRenderable(obj);
    }
    if(gameState == GameState.OVER){
      if(inputController.didReset()){
        gameState = GameState.PLAY;
        physicsEngine.reset();
      } else{
        renderEngine.drawGameOver();
      }
    }
    //System.out.println(physicsEngine.getPlayerModel().getX());
  }

  public void resize(int width, int height) {
    renderEngine.resize(width, height);
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
