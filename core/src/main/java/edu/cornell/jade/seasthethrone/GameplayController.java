package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.*;

import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.ai.CrabBossController;
import edu.cornell.jade.seasthethrone.ai.JellyBossController;
import edu.cornell.jade.seasthethrone.gamemodel.PortalModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.ObstacleModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.level.*;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.ScreenListener;
import edu.cornell.jade.seasthethrone.gamemodel.boss.CrabBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.JellyBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;

import java.util.Comparator;

/**
 * The primary controller class for the game.
 *
 * <p>Delegates all of the work to other subcontrollers including input control, physics engine, and
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

  /** State defining the current logic of the GameplayController. */
  private GameState gameState;

  /** Renderer for debug hitboxes. */
  Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

  /** Sub-controller for collecting input */
  InputController inputController;

  /** Sub-controller for handling updating physics engine based on input */
  PlayerController playerController;

  Array<BossController> bossControllers;

  /** Rendering Engine */
  RenderingEngine renderEngine;

  /** Controller for keeping track of bullet patterns */
  protected AttackPattern bulletController;

  /** Width of the game world in Box2d units */
  protected float worldWidth;

  /** Height of the game world in Box2d units */
  protected float worldHeight;

  /** Ratio between the pixel in a texture and the meter in the world */
  protected float worldScale;

  /** The Box2D world */
  protected PhysicsEngine physicsEngine;

  /** The level currently loaded */
  protected Level level;

  /** The boundary of the world */
  protected Rectangle bounds;

  /** Viewport maintaining relation between screen and world coordinates */
  private ExtendViewport viewport;

  /** If the screen and world should be updated */
  protected boolean active;

  /** Temporary cache to sort physics renderables */
  private final Array<Model> objectCache = new Array<>();

  /** Comparator to sort Models by height */
  private final HeightComparator comp = new HeightComparator();

  /** Temporary cache used by updateCamera */
  private final Vector2 updateCameraCache = new Vector2();

  /** fish bullet builder */
  BulletModel.Builder fishBulletBuilder;

  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  protected GameplayController() {
    gameState = GameState.PLAY;

    this.level = new Level("levels/hub_world.json");

    worldHeight = level.DEFAULT_HEIGHT;
    worldWidth = level.DEFAULT_WIDTH;
    worldScale = level.WORLD_SCALE;
    this.viewport = level.getViewport();

    bounds = new Rectangle(0, 0, worldWidth, worldHeight);

    active = false;

    this.bossControllers = new Array<>();
    this.inputController = new InputController(viewport);
    this.renderEngine = new RenderingEngine(worldWidth, worldHeight, viewport, worldScale);

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
    renderEngine.addRenderable(level.getBackground());

    // Load tiles
    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    // Load player
    // TODO: make this come from the information JSON
    Vector2 playerLoc = level.getPlayerLoc();
    PlayerModel player =
        PlayerModel.Builder.newInstance()
            .setX(playerLoc.x)
            .setY(playerLoc.y)
            .setTextureNEDash(new Texture("player/player_dash_ne.png"))
            .setTextureNWDash(new Texture("player/player_dash_nw.png"))
            .setTextureSWDash(new Texture("player/player_dash_sw.png"))
            .setTextureSEDash(new Texture("player/player_dash_se.png"))
            .setTextureUp(new Texture("player/player_run_up.png"))
            .setTextureDown(new Texture("player/player_run_down.png"))
            .setTextureLeft(new Texture("player/player_run_left.png"))
            .setTextureRight(new Texture("player/player_run_right.png"))
            .setTextureUpDash(new Texture("player/player_dash_up.png"))
            .setTextureDownDash(new Texture("player/player_dash_down.png"))
            .setTextureLeftDash(new Texture("player/player_dash_left.png"))
            .setTextureRightDash(new Texture("player/player_dash_right.png"))
            .setDashIndicatorTexture(new Texture("player/dash_indicator.png"))
            .setIdleLeft(new Texture("player/player_idle_left.png"))
            .setIdleRight(new Texture("player/player_idle_right.png"))
            .setIdleUp(new Texture("player/player_idle_up.png"))
            .setIdleDown(new Texture("player/player_idle_down.png"))
            .setShootDown(new Texture("player/player_shoot_down.png"))
            .setShootUp(new Texture("player/player_shoot_up.png"))
            .setShootLeft(new Texture("player/player_shoot_left.png"))
            .setShootRight(new Texture("player/player_shoot_right.png"))
            .setDeathUp(new Texture("player/player_death_up.png"))
            .setDeathDown(new Texture("player/player_death_down.png"))
            .setDeathLeft(new Texture("player/player_death_left.png"))
            .setDeathRight(new Texture("player/player_death_right.png"))
            .setFramesInAnimation(12)
            .setFramesInAnimationDash(5)
            .setFramesInAnimationDashDiagonal(5)
            .setFramesInAnimationShoot(5)
            .setFramesInAnimationDeath(16)
            .setFrameDelay(3)
            .setDashLength(20)
            .setMoveSpeed(8f)
            .setCooldownLimit(30)
            .setShootCooldownLimit(20)
            .build();
    renderEngine.addRenderable(player);

    // Initialize physics engine
    physicsEngine = new PhysicsEngine(bounds, world);
    physicsEngine.addObject(player);
    // Load fish bullets builder
    fishBulletBuilder = BulletModel.Builder.newInstance()
      .setFishTexture(new Texture("bullet/yellowfish_east.png"));

    // Load bosses
    for (int i = 0; i < level.getBosses().size; i++) {
      // TODO: set everything below here based on bossName, load from assets.json
      LevelObject bossContainer = level.getBosses().get(i);
      String name = bossContainer.bossName;
      int frameSize;
      switch (name) {
        case "crab":
          frameSize = 110;
          break;
        case "jelly":
          frameSize = 45;
          break;
        default:
          frameSize = 0;
          return;
      }
      BossModel boss = BossModel.Builder.newInstance()
              .setX(bossContainer.x)
              .setY(bossContainer.y)
              .setType(name)
              .setHealth(100)

              .setHitbox(name)
//              .setHitbox(new float[]{-3, -3, -3, 3, 3, 3, 3, -3})
              .setHealthThresholds(new int[]{70, 30})

              .setFrameSize(frameSize)
              .setFalloverAnimation(new Texture("bosses/" + name + "/fallover.png"))
              .setFrameDelay(12)
              .build();
      renderEngine.addRenderable(boss);
      physicsEngine.addObject(boss);
      if (boss instanceof CrabBossModel b) {
        bossControllers.add(new CrabBossController(b, player, fishBulletBuilder, physicsEngine));
      } else if (boss instanceof JellyBossModel b) {
        bossControllers.add(new JellyBossController(b, player, fishBulletBuilder, physicsEngine));
      } else {
        // log an error
      }
    }

    // Load walls
    for (LevelObject wall : level.getWalls()) {
      PolygonModel model = new PolygonModel(wall.toList(), wall.x, wall.y);
      model.setBodyType(BodyDef.BodyType.StaticBody);
      physicsEngine.addObject(model);
    }

    // Load Obstacles
    for (LevelObject obs : level.getObstacles()) {
      ObstacleModel model = new ObstacleModel(obs, worldScale);
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

    // Initlize controllers
    playerController = new PlayerController(physicsEngine, player);
    inputController.add(playerController);

    // Load UI
    renderEngine.addUI(playerController.getHealthBar());

    if (BuildConfig.DEBUG) {
      System.out.println("num objects: " + physicsEngine.getObjects().size());
    }
  }

  public void render(float delta) {
    if (active) {
      update(delta);
    }
  }

  public void draw(float delta) {
    renderEngine.drawRenderables();
    renderEngine.drawUI();
  }

  public void update(float delta) {
    viewport.apply();

    inputController.update();

    // Update entity controllers and camera if the game is not over
    if (gameState != GameState.OVER) {
      playerController.update();
      for (BossController bc : bossControllers) {
        if (!bc.isTerminated()) {
          bc.update(delta);
        }
      }

      physicsEngine.update(delta);

      // Update camera
      updateCamera();
    }

    // Check if the player is dead, end the game
    if (playerController.isTerminated()) {
      gameState = GameState.OVER;
    }

    // Check if the player is alive and all bosses are dead, if so the player wins
    if (!bossControllers.isEmpty() && allBossesDefeated() && !playerController.isTerminated()) {
      gameState = GameState.WIN;
      for (BossController bc : bossControllers) {
        bc.remove();
      }
    }

    // Load new level if the player has touched a portal, thus setting a target
    if (physicsEngine.hasTarget()) {
      if (BuildConfig.DEBUG) {
        System.out.println("hasTarget " + physicsEngine.getTarget());
      }

      listener.exitScreen(this, GDXRoot.EXIT_SWAP);
      level = new Level(physicsEngine.getTarget());
      this.renderEngine.clear();

      setupGameplay();
    }

    // Reset target so player doesn't teleport again on next frame
    physicsEngine.setTarget(null);

    // Render frame
    renderEngine.clear();
    renderEngine.addRenderable(level.getBackground());
    renderEngine.addRenderable(playerController.getAmmoBar());

    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    // Add physics objects to rendering engine in height-sorted order
    objectCache.clear();
    for (Model obj : physicsEngine.getObjects()) {
      if (BuildConfig.DEBUG) assert obj.isActive();

      if (obj instanceof Renderable r) objectCache.add((Model) r);
    }
    objectCache.sort(comp);

    for (Model r : objectCache) {
      renderEngine.addRenderable((Renderable) r);
    }

    // Draw the rendereables
    draw(delta);
    if (BuildConfig.DEBUG) {
      debugRenderer.render(physicsEngine.getWorld(), renderEngine.getViewport().getCamera().combined);
    }

    // Draw reset and debug screen for wins and losses
    if (BuildConfig.DEBUG) {
      if (gameState == GameState.OVER || gameState == GameState.WIN) {
        if (inputController.didReset()) {
          setupGameplay();
        } else {
          renderEngine.drawGameState(gameState);
        }
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

    updateCameraCache.set(viewport.getCamera().position.x, viewport.getCamera().position.y);
    Vector2 cameraPos = viewport.unproject(updateCameraCache);

    Vector2 diff = playerPos
      .sub(cameraPos)
      .sub(viewport.getWorldWidth() / 2, -viewport.getWorldHeight() / 2);

    viewport.getCamera().translate(diff.x, diff.y, 0);
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * <p>The ScreenListener will respond to requests to quit.
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
    if (physicsEngine != null) physicsEngine.dispose();
  }

  public boolean allBossesDefeated() {
    for (BossController bc : bossControllers)
      if (!bc.isTerminated())
        return false;
    return true;
  }

  public void disposeBosses() {
    for (BossController boss : bossControllers) {
      boss.dispose();
    }
  }

  /**
   * Compares Models based on height in the world
   */
  class HeightComparator implements Comparator<Model> {
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
