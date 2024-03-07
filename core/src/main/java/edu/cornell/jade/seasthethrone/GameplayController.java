package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.gamemodel.PlayerModel;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.input.PlayerController;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Vector;

/**
 * The primary controller class for the game.
 *
 * Delegates all of the work to other subcontrollers including input control,
 * physics engine, and rendering engine. Contains the central update method.
 */
public class GameplayController implements Screen {

  /** Track state of the game */
  public enum GameState {
    /** While we are playing the game */
    PLAY,
    /** Game over */
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
  /** If the current (gameplay) screen is active */
  protected boolean active;

  protected GameplayController() {
    active = false;
    bounds = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    scale = new Vector2(1, 1);
    inputController = new InputController();
    renderEngine = new RenderingEngine(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    setupGameplay();
  }

  public void show() {
    active = true;
  }

  public void setupGameplay() {
    dispose();
    gameState = GameState.PLAY;

    // create playermodel for the game
    World world = new World(new Vector2(0, 0), false);
    PlayerModel player = new PlayerModel(0, 0);
    physicsEngine = new PhysicsEngine(bounds, world, player);
    playerController = new PlayerController(bounds, player);

    renderEngine.addRenderable(player);
    inputController.add(playerController);
  }

  public void render(float delta) {
    if (active) {
      update(delta);
    }
    //draw(delta);
  }

  public void draw(float delta) {
    renderEngine.drawBackground();
    renderEngine.drawRenderables();
  }

  public void update(float delta) {
    Vector2 screenDims = new Vector2(renderEngine.getCanvas().getWidth(), renderEngine.getCanvas().getHeight());
    playerController.setScreenDims(screenDims);

    inputController.update();

    // Right now just errors if you try to update playerController or physicsEngine when player is null
    if(gameState != GameState.OVER){
      playerController.update();
      physicsEngine.update(delta);
    }

    if(!playerController.isAlive()){
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
        setupGameplay();
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
    if(physicsEngine != null) physicsEngine.dispose();
  }
}
