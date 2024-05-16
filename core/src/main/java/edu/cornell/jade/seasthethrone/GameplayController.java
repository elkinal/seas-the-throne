package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.*;

import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.gamemodel.CheckpointModel;
import edu.cornell.jade.seasthethrone.gamemodel.PortalModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.ObstacleModel;
import edu.cornell.jade.seasthethrone.gamemodel.gate.GateModel;
import edu.cornell.jade.seasthethrone.gamemodel.gate.GateWallModel;
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
import edu.cornell.jade.seasthethrone.ui.*;
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

  /** Assets to be loaded */
  private AssetDirectory assets;

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

  /** Map of previously loaded levels */
  private HashMap<String, Level> loadedLevels;

  /** If the player has entered a portal and the level should be changed */
  private boolean changeLevelFlag;

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

  /** If the game has been set to quit */
  private boolean quit;

  /** If level select was clicked in the pause menu */
  private boolean returnToHub;

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

  /** Timer to prevent saving multiple frames in a row */
  private int saveTimer;

  /** Minimum number of frames between saves */
  private final int SAVE_DELAY = 30;

  /** fish bullet builder */
  BulletModel.Builder fishBulletBuilder;

  /** Listener that will update the player mode when we are done */
  private ScreenListener listener;

  protected GameplayController() {
    gameState = GameState.PLAY;

    loadedLevels = new HashMap<>();
    this.level = new Level("levels/hub_world.json");
    loadedLevels.put(level.name, level);

    this.assets = new AssetDirectory("assets.json");
    assets.loadAssets();
    assets.finishLoading();

    worldHeight = level.DEFAULT_HEIGHT;
    worldWidth = level.DEFAULT_WIDTH;
    worldScale = level.WORLD_SCALE;
    this.viewport = level.getViewport();
    uiViewport = new ScreenViewport();

    bounds = new Rectangle(0, 0, worldWidth, worldHeight);

    active = false;
    restart = false;
    quit = false;
    returnToHub = false;
    saveTimer = 0;

    this.stateController = new StateController();
    stateController.setCurrentLevel(level.name);
    stateController.setRespawnLevel(level.name);
    stateController.setRespawnLoc(level.getPlayerLoc());

    this.bossControllers = new Array<>();
    this.inputController = new InputController(viewport);
    this.portalController = new PortalController();
    this.interactController = new InteractableController();
    inputController.add(interactController);
    this.renderEngine = new RenderingEngine(worldWidth, worldHeight, viewport, worldScale);

////     Initialize physics engine
//    World world = new World(new Vector2(0, 0), false);
//    physicsEngine = new PhysicsEngine(bounds, world);

    // Load UI
    PauseMenu pauseMenu = new PauseMenu(viewport);
    PauseMenuController pauseMenuController = new PauseMenuController(pauseMenu);
    pauseMenuController.setGameplayController(this);
    inputController.add(pauseMenuController);

////     Initlize controllers
//    playerController = new PlayerController(physicsEngine);
//    inputController.add(playerController);
//    interactController.setPlayerController(playerController);

    // Initialize pause controller
    pauseController = new PauseController(renderEngine);

    // Load the Pause Menu's dialogue box
    DialogueBoxController pauseMenuDialogueBoxController = pauseMenu.getDialogueBoxController();
    pauseMenuDialogueBoxController.setGameplayController(this);
    inputController.add(pauseMenuDialogueBoxController);
    uiController =
            new UIController(
                    pauseMenuController,
                    renderEngine,
                    renderEngine.getGameCanvas(),
                    uiViewport);
    uiController.gatherAssets(assets);

    setupGameplay();
  }

  public void show() {
    active = true;
  }

  public void setupGameplay() {
    dispose();

    // Load player
    // TODO: make this come from the information JSON
    Vector2 playerLoc;
    if (restart && stateController.hasRespawnLoc()) {
      String levelName = stateController.getRespawnLevel();
      if (loadedLevels.containsKey(levelName)) {
        level = loadedLevels.get(levelName);
      } else {
        level = new Level(levelName);
        loadedLevels.put(level.name, level);
      }
      playerLoc = stateController.getRespawnLoc();
    } else if (physicsEngine != null && physicsEngine.getSpawnPoint() != null) {
      playerLoc = level.tiledToWorldCoords(physicsEngine.getSpawnPoint());
    } else {
      playerLoc = level.getPlayerLoc();
    }

    // Initialize physics engine
    World world = new World(new Vector2(0, 0), false);
    physicsEngine = new PhysicsEngine(bounds, world);

    gameState = GameState.PLAY;

    HashMap<String, Array<LevelObject>> layers = level.getLayers();

    // Load background
    renderEngine.addRenderable(level.getBackground());

    // Load tiles
    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
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
            .setMoveSpeed(12f)
            .setCooldownLimit(10)
            .setShootCooldownLimit(20)
            .build();

//    playerController.setPlayer(player);
    // Initlize controllers
    playerController = new PlayerController(physicsEngine, player);
    inputController.add(playerController);
    interactController.setPlayerController(playerController);

    renderEngine.addRenderable(player);
    physicsEngine.addObject(player);

    // Initialize pause controller
    pauseController.setPhysicsEngine(physicsEngine);
    pauseController.setPlayerController(playerController);
    uiController.setPlayer(playerController);

    // Load fish bullets builder
    fishBulletBuilder =
        BulletModel.Builder.newInstance()
            .setBaseTexture(new Texture("bullet/whitefish.png"))
            .setUnbreakableTexture(new Texture("bullet/urchinbullet.png"));
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

      JsonValue bossInfo = assets.getEntry(assetName, JsonValue.class);
      var bossBuilder =
          BossModel.Builder.newInstance()
              .setType(name)
              .setFrameSize(bossInfo.getInt("frame_size", 0))
              .setX(bossContainer.x)
              .setY(bossContainer.y)
              .setHealth(bossInfo.getInt("health", 0))
              .setHealthThresholds(bossInfo.get("thresholds").asIntArray())
              .setHitbox(bossInfo.get("hitbox").asFloatArray())
              .setScale(bossInfo.getFloat("scale", 1))
              .setFalloverAnimation(new Texture("bosses/" + assetName + "/fallover.png"))
              .setShootAnimation(new Texture("bosses/" + assetName + "/shoot.png"))
              .setGetHitAnimation(new Texture("bosses/" + assetName + "/hurt.png"))
              .setDeathAnimation(new Texture("bosses/" + assetName + "/death.png"))
              .setAttackAnimation(new Texture("bosses/" + assetName + "/attack.png"))
              .setFrameDelay(12)
              .setRoomId(bossContainer.roomId);
      if (name.contains("swordfish")){
        bossBuilder.setShootDownAnimation(new Texture("bosses/" + assetName + "/shoot_vertical.png"))
            .setShootUpAnimation(new Texture("bosses/" + assetName + "/shoot_vertical_up.png"))
            .setShootRightAnimation(new Texture("bosses/" + assetName + "/shoot_side_right.png"))
            .setAttackDownAnimation(new Texture("bosses/" + assetName + "/attack_vertical.png"))
            .setAttackUpAnimation(new Texture("bosses/" + assetName + "/attack_vertical_up.png"))
            .setAttackRightAnimation(new Texture("bosses/" + assetName + "/attack_side_right.png"))
            .setGetHitDownAnimation(new Texture("bosses/" + assetName + "/front_hurt.png"))
            .setGetHitUpAnimation(new Texture("bosses/" + assetName + "/up_hurt.png"))
            .setGetHitRightAnimation(new Texture("bosses/" + assetName + "/right_hurt.png"));
      }
      if (name.contains("final")){
        bossBuilder.setTransformAnimation(new Texture("bosses/" + assetName + "/transform.png"))
            .setFinalAttackAnimation(new Texture("bosses/" + assetName + "/final_attack.png"))
            .setFinalShootAnimation(new Texture("bosses/" + assetName + "/final_shoot.png"))
            .setFinalGetHitAnimation(new Texture("bosses/" + assetName + "/final_hurt.png"));
      }
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

      for (GateWallModel wall : model.getWalls()) {
        renderEngine.addRenderable(wall);
      }

      physicsEngine.addObject(model);
    }
    // Load checkpoints
    for (LevelObject check : layers.get("checkpoints")) {
      CheckpointModel model = new CheckpointModel(check, worldScale);
      model.setBodyType(BodyDef.BodyType.StaticBody);
      physicsEngine.addObject(model);
      renderEngine.addRenderable(model);
      interactController.add(model);
    }
    // Load healthpacks
    for (LevelObject hpack : layers.get("healthpacks")) {
      HealthpackModel model = new HealthpackModel(hpack, worldScale);
      physicsEngine.addObject(model);
      renderEngine.addRenderable(model);
      interactController.add(model);
    }

    // load foreground
    renderEngine.addRenderable(level.getForeground());
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

      // Update saving
      if (saveTimer > 0) saveTimer += 1;
      if (saveTimer > SAVE_DELAY) {
        saveTimer = 0;
        uiController.setDrawSave(false);
      }

      if (interactController.isCheckpointActivated() && saveTimer == 0) {
        stateController.setRespawnLoc(playerController.getLocation().cpy());
        stateController.setRespawnLevel(level.name);
        stateController.updateState(level.name, playerController, bossControllers);
        stateController.saveGame();
        uiController.setDrawSave(true);
        saveTimer++;
      }

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

    if (changeLevelFlag) {
      changeLevel();
      // Reset target so player doesn't teleport again on next frame
      physicsEngine.setTarget(null);
      physicsEngine.setSpawnPoint(null);
      changeLevelFlag = false;
    }

    // Load new level if the player has touched a portal, thus setting a target
    if (physicsEngine.hasTarget()) {
      changeLevelFlag = true;
      // Save the current level state
      stateController.updateState(level.name, playerController, bossControllers);
      listener.exitScreen(this, GDXRoot.EXIT_SWAP);
    }

    // Render frame
    renderEngine.clear();
    renderEngine.addRenderable(level.getBackground());
    renderEngine.addRenderable(uiController.getAmmoBar());
    for (EnemyHealthBar e : uiController.getEnemies()){
      renderEngine.addRenderable(e);
    }

    for (Tile tile : level.getTiles()) {
      renderEngine.addRenderable(tile);
    }

    // Add physics objects to rendering engine in height-sorted order
    objectCache.clear();
    for (Model obj : physicsEngine.getObjects()) {
      if (BuildConfig.DEBUG) assert obj.isActive();

      if (obj instanceof Renderable r) {
        if (obj instanceof GateModel) {
          for (GateWallModel wall : ((GateModel) obj).getWalls()) {
            objectCache.add(wall);
          }
        } else {
          objectCache.add((Model) r);
        }
      }
    }
    objectCache.sort(comp);

    for (Model r : objectCache) {
      renderEngine.addRenderable((Renderable) r);
    }

    renderEngine.addRenderable(level.getForeground());

    // Draw the renderables
    draw(delta);
    if (BuildConfig.DEBUG) {
      debugRenderer.render(
          physicsEngine.getWorld(), renderEngine.getViewport().getCamera().combined);
    }

    if (returnToHub) {
      physicsEngine.setTarget("levels/hub_world.json");
      returnToHub = false;
    }

    if (restart) restart();

    if (quit) {
      if (BuildConfig.DEBUG) {
        System.out.println("Exiting game");
      }
      quitGame();
//      listener.exitScreen(this, 4);
    }

    // Draw reset and debug screen for wins and losses
    if (gameState == GameState.OVER) {
      if (inputController.didReset()) {
        restart = true;
        pauseController.continueGame();
      } else {
        renderEngine.drawGameState(gameState);
      }
    }
  }

  /** Changes the current level to the one specified by the physics engine target. */
  private void changeLevel() {
    if (BuildConfig.DEBUG) {
      System.out.println("Changing level to: " + physicsEngine.getTarget());
    }

    // Load in new level
    if (loadedLevels.containsKey(physicsEngine.getTarget())) {
      level = loadedLevels.get(physicsEngine.getTarget());
    } else {
      level = new Level(physicsEngine.getTarget());
      loadedLevels.put(level.name, level);
    }
    stateController.setCurrentLevel(level.name);

    // Reload
    setupGameplay();

    transferState(stateController.getLevel(level.name));
  }

  /** Loads the stored state of the target level, if it exists */
  private void transferState(LevelState newState) {
    if (BuildConfig.DEBUG) {
      System.out.println("Transferring state");
    }

    // Transfer player state
    playerController.transferState(stateController);

    // Transfer level state if this is not the first time entering level
    if (newState != null && newState.getBossHps().size > 0) {
      for (int i = 0; i < bossControllers.size; i++) {
        int storedHp = newState.getBossHps().get(i);
        bossControllers.get(i).transferState(storedHp);
      }
    }
  }

  private void restart() {
    if (BuildConfig.DEBUG) {
      System.out.println("Respawning");
    }
    setupGameplay();
//    try {
//      System.out.println("respawn loc " + stateController.getRespawnLoc());
//      playerController.setPlayerLocation(stateController.getRespawnLoc());
//    } catch (NullPointerException e) {
//      System.out.println("respawn to default loc");
//      playerController.setPlayerLocation(level.getPlayerLoc());
//    }
    restart = false;
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

  public void setAssets(AssetDirectory assets) {
    this.assets = assets;
  }

  public void setReturnToHub(boolean returnToHub) {
    this.returnToHub = returnToHub;
  }

  public void setRestart(boolean restart) {
    this.restart = restart;
  }

  public void setQuit(boolean quit) {
    this.quit = quit;
  }

  private void quitGame() {
    loadedLevels.clear();
    renderEngine.clear();
    bossControllers.clear();
    assets.dispose();
    dispose();
    ((GDXRoot) listener).dispose();
    System.exit(0);
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
//    playerController.setPlayer(null);
    bossControllers.clear();
    uiController.clear();
//    renderEngine.clear();
    interactController.dispose();
    portalController.dispose();
    if (physicsEngine!=null) physicsEngine.dispose();
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
      if (o1 instanceof PlayerModel) {
        o1 = ((PlayerModel) o1).getShadowModel();
      } else if (o2 instanceof PlayerModel) {
        o2 = ((PlayerModel) o2).getShadowModel();
      }

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
