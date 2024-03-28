package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.*;

import edu.cornell.jade.seasthethrone.gamemodel.PortalModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.ObstacleModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.CrabBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.input.BossController;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.input.PlayerController;
import edu.cornell.jade.seasthethrone.bpedit.BulletController;
import edu.cornell.jade.seasthethrone.level.*;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

import java.util.Comparator;

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
    OVER,
    /** Game win */
    WIN,
  }

  private GameState gameState;
  Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

  /** Sub-controller for collecting input */
  InputController inputController;

  /** Sub-controller for handling updating physics engine based on input */
  PlayerController playerController;

  BossController bossController;
  /** Rendering Engine */
  RenderingEngine renderEngine;

  /** Controller for keeping track of bullet patterns */
  protected BulletController bulletController;

  /** Width of the game world in Box2d units */
  protected static float DEFAULT_WIDTH;

  /** Height of the game world in Box2d units */
  protected static float DEFAULT_HEIGHT;

  /** Ratio between the pixel in a texture and the meter in the world */
  protected float WORLD_SCALE;

  /** The Box2D world */
  protected PhysicsEngine physicsEngine;

  /** The level currently loaded */
  protected Level level;

  /** The boundary of the world */
  protected Rectangle bounds;

  /** Viewport maintaining relation between screen and world coordinates */
  private ExtendViewport viewport;

  protected boolean active;

  /** Temporary cache to sort physics renderables */
  private Array<Model> objectCache = new Array<>();

  /** Comparator to sort Models by height */
  private heightComparator comp = new heightComparator();

  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  protected GameplayController() {
    gameState = GameState.PLAY;

    this.level = new Level("levels/hub_world.json");
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
    // renderEngine.setBackground(level.getBackground());
    renderEngine.addRenderable(level.getBackground());
    // Load tiles
    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    // Load player
    Vector2 playerLoc = level.getPlayerLoc();
    PlayerModel player = PlayerModel.Builder.newInstance()
            .setX(playerLoc.x)
            .setY(playerLoc.y)
            .setTextureUp(new Texture("player/playerspriterun_up_wspear.png"))
            .setTextureDown(new Texture("player/playerspriterun_down_wspear.png"))
            .setTextureLeft(new Texture("player/playerspriterun_left_wspear.png"))
            .setTextureRight(new Texture("player/playerspriterun_right_wspear.png"))
            .setTextureUpDash(new Texture("player/playerspritedashfilmstrip_up.png"))
            .setTextureDownDash(new Texture("player/playerspritedashfilmstrip_down.png"))
            .setTextureLeftDash(new Texture("player/playerspritedashfilmstrip_left.png"))
            .setTextureRightDash(new Texture("player/playerspritedashfilmstrip_right.png"))
            .setDashIndicatorTexture(new Texture("player/dash_indicator.png"))
            .setFramesInAnimation(12)
            .setFramesInAnimationDash(5)
            .setFrameDelay(3)
            .setDashLength(20)
            .setMoveSpeed(8f)
            .setCooldownLimit(30)
            .setShootCooldownLimit(20)
            .build();
    renderEngine.addRenderable(player);

    physicsEngine = new PhysicsEngine(bounds, world);
    physicsEngine.addObject(player);
    playerController = new PlayerController(physicsEngine, player);
    bulletController = new BulletController(physicsEngine);

    // Load bosses
    for (int i = 0; i < level.getBosses().size; i++) {
      LevelObject bossContainer = level.getBosses().get(i);
      String name = bossContainer.bossName;
      BossModel boss = BossModel.Builder.newInstance()
              .setX(bossContainer.x)
              .setY(bossContainer.y)
              .setType(name)
              // TODO: set everything below here based on bossName, load from assets.json
              .setHealth(100)
              .setHitbox(new float[]{-4, -7, -4, 7, 4, 7, 4, -7})
              .setFrameSize()
              .setShootAnimation(new Texture("bosses/"+name+"/"+name+"_idle.png"))
              .setFrameDelay(12)
              .build();
      renderEngine.addRenderable(boss);
      physicsEngine.addObject(boss);
      bossController = new BossController(boss);
    }

    // Load walls
    for (LevelObject wall : level.getWalls()) {
      PolygonModel model = new PolygonModel(wall.toList(), wall.x, wall.y);
      model.setBodyType(BodyDef.BodyType.StaticBody);
      physicsEngine.addObject(model);
    }

    for (LevelObject obs : level.getObstacles()) {
      // BoxModel model = new BoxModel(obs.x, obs.y, obs.width, obs.height);
      ObstacleModel model = new ObstacleModel(obs, WORLD_SCALE);
      model.setBodyType(BodyDef.BodyType.StaticBody);
      renderEngine.addRenderable(model);
      physicsEngine.addObject(model);
    }

    // Load portals
    for (LevelObject portal : level.getPortals()) {
      PortalModel model = new PortalModel(portal);
      renderEngine.addRenderable(model);
      physicsEngine.addObject(model);
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
    // renderEngine.drawBackground();
    renderEngine.drawRenderables();
  }

  public void update(float delta) {
    viewport.apply();
    inputController.update();
    bulletController.update();

    // Right now just errors if you try to update playerController or physicsEngine
    // when player is null
    if (gameState != GameState.OVER) {
      playerController.update();
      if (this.bossController != null) {bossController.update();}
      physicsEngine.update(delta);

      // Update camera
      updateCamera();

    }

    if (!playerController.isAlive()) {
      gameState = GameState.OVER;
    } else if (this.bossController != null && !bossController.isAlive()) {
      gameState = GameState.WIN;
    }

    if (physicsEngine.hasTarget()) {
      System.out.println("hasTarget "+physicsEngine.getTarget());
      listener.exitScreen(this, 1);
      level = new Level(physicsEngine.getTarget());
      setupGameplay();
    }

    // Reset target
    physicsEngine.setTarget(null);

    renderEngine.clear();
    renderEngine.addRenderable(level.getBackground());
    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    // Add physics objects to rendering engine in height-sorted order
    objectCache.clear();
    for (Model obj : physicsEngine.getObjects()) {
      assert (obj.isActive());
      if (obj instanceof Renderable r)
        objectCache.add((Model) r);
    }
    objectCache.sort(comp);

    for (Model r : objectCache) {
      renderEngine.addRenderable((Renderable) r);
    }

    draw(delta);
    debugRenderer.render(physicsEngine.getWorld(), renderEngine.getViewport().getCamera().combined);

    if (gameState == GameState.OVER || gameState == GameState.WIN) {
      if (inputController.didReset()) {
        setupGameplay();
      } else {
        renderEngine.drawGameState(gameState);
      }
    }
  }

  public void resize(int width, int height) {
    viewport.update(width, height);
    renderEngine.getGameCanvas().resize();
  }

  /** Updates the camera position to keep the player centered on the screen */
  private void updateCamera() {
    Vector2 playerPos = playerController.getLocation();
    Vector2 cameraPos = viewport
        .unproject(new Vector2(viewport.getCamera().position.x, viewport.getCamera().position.y));

    Vector2 worldDims = new Vector2(viewport.getWorldWidth(), viewport.getWorldHeight());

    Vector2 diff = playerPos.sub(cameraPos).sub(worldDims.x / 2, -worldDims.y / 2);
    viewport.getCamera().translate(diff.x, diff.y, 0);
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
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

  /**
   * Compares Models based on height in the world
   */
  class heightComparator implements Comparator<Model> {
    @Override
    public int compare(Model o1, Model o2) {
      float diff = o2.getBody().getPosition().y - o1.getBody().getPosition().y;
      if (diff > 0) {
        return 1;
      } else if (diff < 0) {
        return -1;
      }
      return 0;
    }
  }

}
