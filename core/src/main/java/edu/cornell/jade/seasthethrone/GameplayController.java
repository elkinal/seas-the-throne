package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;

import edu.cornell.jade.seasthethrone.gamemodel.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.PlayerModel;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.input.PlayerController;
import edu.cornell.jade.seasthethrone.level.Level;
import edu.cornell.jade.seasthethrone.level.Tile;
import edu.cornell.jade.seasthethrone.level.Wall;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

import java.util.Vector;

/**
 * The primary controller class for the game.
 *
 * <p>
 * Delegates all of the work to other subcontrollers including input control,
 * physics engine, and
 * rendering engine. Contains the central update method.
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
  protected static float DEFAULT_WIDTH;

  /** Height of the game world in Box2d units */
  protected static float DEFAULT_HEIGHT;

  /** Ratio between the pixel in a texture and the meter in the world */
  private static float WORLD_SCALE;

  /** The Box2D world */
  protected PhysicsEngine physicsEngine;

  /** The level currently loaded */
  protected Level level;

  /** The boundary of the world */
  protected Rectangle bounds;

  /** Viewport maintaining relation between screen and world coordinates */
  private FitViewport viewport;

  protected boolean active;

  protected GameplayController() {
    gameState = GameState.PLAY;

    this.level = new Level("levels/test1.json");
    DEFAULT_HEIGHT = level.DEFAULT_HEIGHT;
    DEFAULT_WIDTH = level.DEFAULT_WIDTH;
    WORLD_SCALE = level.WORLD_SCALE;
    this.viewport = level.getViewport();

    bounds = new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);

    active = false;

    this.inputController = new InputController(viewport);
    this.renderEngine = new RenderingEngine(DEFAULT_WIDTH, DEFAULT_HEIGHT, viewport, WORLD_SCALE);

    setupGameplay();
  }

  public void show() {
    active = true;
  }

  public void setupGameplay() {
    dispose();
    gameState = GameState.PLAY;

    World world = new World(new Vector2(0, 0), false);

    // Load background
//    renderEngine.setBackground(level.getBackground());
    renderEngine.addRenderable(level.getBackground());
    // Load tiles
    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    // Load player
    Vector2 playerLoc = level.getPlayerLoc();
    PlayerModel player = new PlayerModel(playerLoc.x, playerLoc.y);
    renderEngine.addRenderable(player);

    physicsEngine = new PhysicsEngine(bounds, world, player);
    playerController = new PlayerController(bounds, player);

    // Load bosses
    for (Vector2 bossLoc : level.getBosses()) {
      BossModel boss = new BossModel(bossLoc.x, bossLoc.y);
      boss.setBodyType(BodyDef.BodyType.StaticBody);
      renderEngine.addRenderable(boss);
      physicsEngine.addObject(boss);
    }

    // Load walls
    for (Wall wall : level.getWalls()) {
      BoxModel wallModel = new BoxModel(wall.x, wall.y, wall.width, wall.height);
      wallModel.setBodyType(BodyDef.BodyType.StaticBody);
      physicsEngine.addObject(wallModel);
    }

    inputController.add(playerController);

  }

  public void render(float delta) {
    if (active) {
      update(delta);
    }
    // draw(delta);
  }

  public void draw(float delta) {
//    renderEngine.drawBackground();
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

    if (!playerController.isAlive()) {
      gameState = GameState.OVER;
    }

    renderEngine.clear();
    renderEngine.addRenderable(level.getBackground());
    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    for (Model obj : physicsEngine.getObjects()) {
      assert (obj.isActive());
      if (obj instanceof Renderable r)
        renderEngine.addRenderable(r);
    }


    draw(delta);
//    debugRenderer.render(physicsEngine.getWorld(), renderEngine.getViewport().getCamera().combined);

    if (gameState == GameState.OVER) {
      if (inputController.didReset()) {
        setupGameplay();
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
    if (physicsEngine != null)
      physicsEngine.dispose();
  }
}
