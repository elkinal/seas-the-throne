package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.*;

import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.gamemodel.CheckpointModel;
import edu.cornell.jade.seasthethrone.gamemodel.PortalModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.ObstacleModel;
import edu.cornell.jade.seasthethrone.gamemodel.gate.GateModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.gamemodel.HealthpackModel;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.level.*;
import edu.cornell.jade.seasthethrone.model.Model;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.ui.PauseMenu;
import edu.cornell.jade.seasthethrone.ui.PauseMenuController;
import edu.cornell.jade.seasthethrone.ui.UIController;
import edu.cornell.jade.seasthethrone.util.ScreenListener;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;

import java.util.Comparator;
import java.util.HashMap;

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

  /** The array of all bosses in a level */
  Array<BossController> bossControllers;

  /** Sub-controller for handling pausing the game */
  PauseController pauseController;

  /** Sub-controller for portals */
  PortalController portalController;

  /** Sub-controller for interactables */
  InteractableController interactController;

  /** Sub-controller for collecting input */
  UIController uiController;

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

  /** Sub-controller for saving/loading the game */
  protected StateController stateController;

  /** The boundary of the world */
  protected Rectangle bounds;

  /** Viewport maintaining relation between screen and world coordinates */
  private ExtendViewport viewport;

  /** UI viewport maintaining relation between screen and world coordinates */
  private ScreenViewport uiViewport;

  /** If the screen and world should be updated */
  protected boolean active;

  /** If the game has been flagged to restart at last checkpoint */
  private boolean restart;

  /** Temporary cache to sort physics renderables */
  private final Array<Model> objectCache = new Array<>();

  /** Comparator to sort Models by height */
  private final HeightComparator comp = new HeightComparator();

  /** Temporary cache used by updateCamera */
  private final Vector2 updateCameraCache = new Vector2();

  /** Used to smooth camera movement. higher = more snappy camera */
  private final float CAMERA_SMOOTHNESS = 0.2f;

  /** Distance at which point the camera will snap to the player */
  private final float CAMERA_SNAP_DISTANCE = 0.1f;

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
    uiViewport = new ScreenViewport();

    bounds = new Rectangle(0, 0, worldWidth, worldHeight);

    active = false;
    restart = false;

    this.stateController = new StateController();
    this.bossControllers = new Array<>();
    this.inputController = new InputController(viewport);
    this.portalController = new PortalController();
    this.interactController = new InteractableController();
    inputController.add(interactController);
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
    HashMap<String, Array<LevelObject>> layers = level.getLayers();

    // Load background
    renderEngine.addRenderable(level.getBackground());

    // Load tiles
    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    // Load player
    // TODO: make this come from the information JSON
    Vector2 playerLoc;
    if (physicsEngine == null || physicsEngine.getSpawnPoint() == null) {
      playerLoc = level.getPlayerLoc();
    } else {
      playerLoc = level.tiledToWorldCoords(physicsEngine.getSpawnPoint());
    }

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
            .setCooldownLimit(10)
            .setShootCooldownLimit(20)
            .build();
    renderEngine.addRenderable(player);

    // Initialize physics engine
    physicsEngine = new PhysicsEngine(bounds, world);
    physicsEngine.addObject(player);

    // Load fish bullets builder
    fishBulletBuilder =
        BulletModel.Builder.newInstance().setFishTexture(new Texture("bullet/yellowfish_east.png"));

    // Load bosses
    bossControllers.clear();
    for (int i = 0; i < layers.get("bosses").size; i++) {
      // TODO: set everything below here based on bossName, load from assets.json
      LevelObject bossContainer = layers.get("bosses").get(i);
      String name = bossContainer.bossName;

      // FIXME: this literly only works because we are dumb it's stupid hack but
      // whatever, should work, no less cursed than what we already have actually
      // despise what I'm about to write, no one do this
      //
      // okay, so I'm just going to do some ad-hoc string parsing stuff here
      // a more systematic solution would require more overhall to this structure
      // which I would do if this were not a project with a due date in 2 weeks

      // jellys are identified by containing the string jelly. They have ad-hoc names determining
      // patterns.
      // See the buildController method for the case statement defining the behavior.
      //
      // clams are similar, but they have a number suffixed determining their angle
      // this number is in degrees
      String[] splitName = name.replaceAll("[^a-zA-Z_]", "").split("_");
      // Assuming that names are going to be of the format "_..._(boss)"
      String assetName = splitName[splitName.length-1];
      if (assetName.equals("shark")) assetName = "jelly";
      int health = name.contains("jelly")
        ? 50 
        : name.contains("clam")
          ? 10
          : 200;
      var bossBuilder =
          BossModel.Builder.newInstance()
              .setType(name)
              .setFrameSize()
              .setX(bossContainer.x)
              .setY(bossContainer.y)
              .setHealth(health)
              //              .setHitbox(new float[]{-3, -3, -3, 3, 3, 3, 3, -3})
              .setHealthThresholds(new int[] {150, 100, 50})
              .setFalloverAnimation(new Texture("bosses/" + assetName + "/fallover.png"))
              .setShootAnimation(new Texture("bosses/" + assetName + "/shoot.png"))
              .setGetHitAnimation(new Texture("bosses/" + assetName + "/hurt.png"))
              .setDeathAnimation(new Texture("bosses/" + assetName + "/death.png"))
              .setAttackAnimation(new Texture("bosses/" + assetName + "/attack.png"))
              .setFrameDelay(12)
              .setRoomId(bossContainer.roomId);
      BossModel boss = bossBuilder.build();
      BossController bossController = bossBuilder.buildController(boss, player, fishBulletBuilder, physicsEngine);
      renderEngine.addRenderable(boss);
      physicsEngine.addObject(boss);
      bossControllers.add(bossController);
    }

    // Load walls
    for (LevelObject wall : layers.get("walls")) {
      PolygonModel model = new PolygonModel(wall.toList(), wall.x, wall.y);
      model.setBodyType(BodyDef.BodyType.StaticBody);
      physicsEngine.addObject(model);
    }

    // Load Obstacles
    for (LevelObject obs : layers.get("obstacles")) {
      ObstacleModel model = new ObstacleModel(obs, worldScale);
      model.setBodyType(BodyDef.BodyType.StaticBody);
      renderEngine.addRenderable(model);
      physicsEngine.addObject(model);
    }

    // Load portals
    for (LevelObject portal : layers.get("portals")) {
      PortalModel model = new PortalModel(portal);
      renderEngine.addRenderable(model);
      physicsEngine.addObject(model);
      portalController.addPortal(model);
    }

    // Load gates
    for (LevelObject gate : layers.get("gates")) {
      int roomId = gate.roomId;
      GateModel model = new GateModel(gate, level.WORLD_SCALE);
      for (BossController bc : bossControllers) {
        if (bc.getBoss().getRoomId() == roomId) {
          model.addBoss(bc.getBoss());
        }
      }
      renderEngine.addRenderable(model);
      physicsEngine.addObject(model);
    }

    // Load checkpoints
    for (LevelObject check : layers.get("checkpoints")) {
      CheckpointModel model = new CheckpointModel(check, worldScale);
      model.setSensor(true);
      physicsEngine.addObject(model);
      renderEngine.addRenderable(model);
      interactController.add(model);
    }

    // Load healthpacks
    for (LevelObject hpack : layers.get("healthpacks")) {
      HealthpackModel model = new HealthpackModel(hpack);
      physicsEngine.addObject(model);
      renderEngine.addRenderable(model);
      interactController.add(model);
    }

    // Initlize controllers
    playerController = new PlayerController(physicsEngine, player);
    inputController.add(playerController);
    interactController.setPlayerController(playerController);

    // Initialize pause controller
    pauseController = new PauseController(renderEngine, physicsEngine, playerController);

    // Load Save State
    stateController.loadState("saves/save1.json");

    if (BuildConfig.DEBUG) System.out.println("post setup ammo: " + playerController.getAmmo());

    // Load UI
    PauseMenu pauseMenu = new PauseMenu(viewport);
    PauseMenuController pauseMenuController = new PauseMenuController(pauseMenu);
    pauseMenuController.setGameplayController(this);
    inputController.add(pauseMenuController);

    uiController =
        new UIController(
            playerController,
            pauseMenuController,
            renderEngine,
            renderEngine.getGameCanvas(),
            uiViewport);
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
    uiController.drawUI();
  }

  public void update(float delta) {
    viewport.apply();
    uiViewport.apply();
    inputController.update();
    portalController.update(stateController);

    // Update entity controllers and camera if the game is not over
    if (gameState != GameState.OVER
        && !uiController.getPauseMenuController().getPauseMenu().isPaused()) {
      playerController.update();
      interactController.update();
      pauseController.continueGame();
      uiController.update(bossControllers);

      for (BossController bc : bossControllers) {
        if (!bc.isDead()) {
          bc.update(delta);
        }
      }

      if (!pauseController.getPaused()) {
        physicsEngine.update(delta);
      }

      // Update camera
      updateCamera();
    }

    // Check if the player is dead, end the game
    if (playerController.isDead()) {
      pauseController.pauseGame();
      uiController.update(bossControllers);
      gameState = GameState.OVER;
    }

    // Check if the game is paused, pause screen
    if (uiController.getPauseMenuController().getPauseMenu().isPaused()) {
      pauseController.pauseGame();
    }

    // Check if the player is alive and all bosses are dead, if so the player wins
    if (!bossControllers.isEmpty() && allBossesDefeated() && !playerController.isDead()) {
      gameState = GameState.WIN;
      for (BossController bc : bossControllers) {
        bc.remove();
      }
    }

    // Check which checkpoints are active
    if (physicsEngine.hasCheckpoint()) {
      if (BuildConfig.DEBUG) {
        System.out.println("hasCheckpoint " + physicsEngine.getCheckpointID());
      }
//      playerController.setHealth(5);
      stateController.updateState(level.name, playerController, bossControllers);
      stateController.setCheckpoint(physicsEngine.checkpointID);
    }

    // Load new level if the player has touched a portal, thus setting a target
    if (physicsEngine.hasTarget()) {
      if (BuildConfig.DEBUG) {
        System.out.println("hasTarget " + physicsEngine.getTarget());
      }
      changeLevel();
      stateController.setRespawnLoc(null);
    }

    // Reset target so player doesn't teleport again on next frame
    physicsEngine.setTarget(null);
    physicsEngine.setSpawnPoint(null);
    physicsEngine.setCheckpointID(null);

    // Render frame
    renderEngine.clear();
    renderEngine.addRenderable(level.getBackground());
    renderEngine.addRenderable(uiController.getAmmoBar());

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

    // Draw the renderables
    draw(delta);
    if (BuildConfig.DEBUG) {
      debugRenderer.render(
          physicsEngine.getWorld(), renderEngine.getViewport().getCamera().combined);
    }

    if (restart) {
      setupGameplay();
      respawn();
      restart = false;
    }

    // Draw reset and debug screen for wins and losses
    if (gameState == GameState.OVER || gameState == GameState.WIN) {
      if (inputController.didReset()) {
        setupGameplay();
        respawn();
        pauseController.continueGame();
      } else {
        renderEngine.drawGameState(gameState);
      }
    }
  }

  /** Changes the current level to the one specified by the physics engine target. */
  private void changeLevel() {
    // Save the current level state
    stateController.updateState(level.name, playerController, bossControllers);

    if (BuildConfig.DEBUG)
      System.out.println("player ammo pre portal: " + playerController.getAmmo());

    listener.exitScreen(this, GDXRoot.EXIT_SWAP);
    level = new Level(physicsEngine.getTarget());
    this.renderEngine.clear();
    bossControllers.clear();
    setupGameplay();
    transferState(stateController.getLevel(level.name));
  }

  /** Loads the stored state of the target level, if it exists */
  private void transferState(LevelState newState) {
    playerController.transferState(stateController);

    // Load level state if this is not the first time entering level
    if (newState != null && newState.getBossHps().size > 0) {
      for (int i = 0; i < bossControllers.size; i++) {
        int storedHp = newState.getBossHps().get(i);
        bossControllers.get(i).transferState(storedHp);
      }
    }
  }

  private void respawn() {
    try {
      playerController.setPlayerLocation(stateController.getRespawnLoc());
    } catch (NullPointerException e) {
      playerController.setPlayerLocation(level.getPlayerLoc());
    }
  }

  /**
   * Gather the assets for this controller.
   *
   * <p>This method extracts the asset variables from the given asset directory. It should only be
   * called after the asset directory is completed.
   *
   * @param directory Reference to global asset manager.
   */
  public void gatherAssets(AssetDirectory directory) {}

  public void resize(int width, int height) {
    viewport.update(width, height);
    uiViewport.update(width, height, true);
    renderEngine.getGameCanvas().resize();
    uiController.resize(width, height);
  }

  /** Updates the camera position to keep the player centered on the screen */
  private void updateCamera() {
    Vector2 playerPos = playerController.getLocation();

    updateCameraCache.set(viewport.getCamera().position.x, viewport.getCamera().position.y);
    Vector2 diff = updateCameraCache.sub(playerPos);

    if (diff.len() < CAMERA_SNAP_DISTANCE) {
      viewport.getCamera().position.set(playerPos.x, playerPos.y, 0);
    } else {
      diff.scl(CAMERA_SMOOTHNESS);
      viewport.getCamera().translate(-diff.x, -diff.y, 0);
    }
  }

  /**
   * Sets the ScreenListener for this mode
   *
   * <p>The ScreenListener will respond to requests to quit.
   */
  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
  }

  public void setRestart(boolean restart) {
    this.restart = restart;
  }

  public void pause() {
    pauseController.pauseGame();
  }

  public void resume() {
    pauseController.continueGame();
  }

  public void hide() {
    active = false;
  }

  public void dispose() {
    if (physicsEngine != null) physicsEngine.dispose();
  }

  public boolean allBossesDefeated() {
    for (BossController bc : bossControllers) if (!bc.isDead()) return false;
    return true;
  }

  /** Compares Models based on height in the world */
  public void disposeBosses() {
    for (BossController boss : bossControllers) {
      boss.dispose();
    }
  }

  /** Compares Models based on height in the world */
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
