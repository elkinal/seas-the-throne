package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.*;
import edu.cornell.jade.seasthethrone.BuildConfig;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.util.JsonHandler;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class Level {
  /** Name of this level */
  public String name;

  private Vector2 playerLoc;

  private final HashMap<String, Array<LevelObject>> layers;

  private BackgroundImage background;

  private final Array<Tile> tiles = new Array<>();

  /** The array of tileSets, each tileSet being a nested list of textures representing tiles */
  private final Array<TextureRegion[][]> tileSets = new Array<>();

  /** The tile IDs of the first tile in each tileSet */
  private final Array<Integer> firstGids = new Array<>();

  /** Width of the game world in Box2d units */
  public float DEFAULT_WIDTH = 64.0f;

  /** Height of the game world in Box2d units */
  public float DEFAULT_HEIGHT = 48.0f;

  /** Ratio between the pixel in a texture and the meter in the world */
  public float WORLD_SCALE = 0.135f;

  /** Side length of a square tile in pixels */
  private final int TILE_SIZE;

  /** Width of the Tiled map in tiles */
  private final int TILED_WORLD_WIDTH;

  /** Height of the Tiled map in tiles */
  private final int TILED_WORLD_HEIGHT;

  private ExtendViewport viewport;

  private Vector2 tempPos;

  public Level(String fileName) {
    this.viewport = new ExtendViewport(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    tempPos = new Vector2();
    this.name = parseName(fileName);
    if (BuildConfig.DEBUG) {
      System.out.println("Loading level:" + this.name);
    }

    // Load JSON to map
    HashMap<String, Object> levelMap = JsonHandler.jsonToMap(fileName);
    // Load in level constants
    TILE_SIZE = JsonHandler.getInt(levelMap, "tilewidth");
    TILED_WORLD_HEIGHT = JsonHandler.getInt(levelMap, "height");
    TILED_WORLD_WIDTH = JsonHandler.getInt(levelMap, "width");

    // Create tileSets
    Array<HashMap<String, Object>> tileSetsList =
        (Array<HashMap<String, Object>>) levelMap.get("tilesets");
    for (HashMap<String, Object> tileSet : tileSetsList) {
      // For each tileSet
      Texture thisTexture = new Texture("levels/" + (String) tileSet.get("image"));
      int thisGid = JsonHandler.getInt(tileSet, "firstgid");
      firstGids.add(thisGid);
      // Split this tileSet up into textures
      tileSets.add(new TextureRegion(thisTexture).split(TILE_SIZE, TILE_SIZE));
    }

    // Load in layers
    Array<HashMap<String, Object>> layerArray = (Array<HashMap<String, Object>>) levelMap.get("layers");

    layers = new HashMap<>();

    parseBackgroundLayer(getLayer(layerArray, "background"));
    parsePlayerLayer(getLayer(layerArray,"player"));
    parseGatesLayer(getLayer(layerArray,"gates"));
    parseTileLayer(getLayer(layerArray,"tiles"));
    parseBossLayer(getLayer(layerArray,"bosses"));
    parseWallLayer(getLayer(layerArray,"walls"));
    parseObstacleLayer(getLayer(layerArray,"obstacles"));
    parsePortalLayer(getLayer(layerArray,"portals"));
    parseInteractableLayer(getLayer(layerArray, "interactables"));
  }

  /**
   * Returns the layer with the given name
   *
   * @param layerName the name of the layer to return
   * @return the layer with the given name
   * @throws Error if the provided name doesn't match any layer in the level
   */
  private HashMap<String, Object> getLayer(Array<HashMap<String, Object>> layerArray, String layerName) {
    for (HashMap<String, Object> layer : layerArray) {
      if (((String) layer.get("name")).equals(layerName)) {
        return layer;
      }
    }
    return new HashMap<>();
    //    throw new Error("No layer with name " + layerName);
  }

  public HashMap<String, Array<LevelObject>> getLayers() {
    return layers;
  }

  public BackgroundImage getBackground() {
    return background;
  }

  public Vector2 getPlayerLoc() {
    return playerLoc;
  }

  public ExtendViewport getViewport() {
    return viewport;
  }

  public Array<Tile> getTiles() {
    return tiles;
  }

  private void parseBackgroundLayer(HashMap<String, Object> bgLayer) {
    int width;
    try {
      width = JsonHandler.getIntProperty(bgLayer, "width");
    } catch (Error e) {
      return;
    }

    int height = JsonHandler.getIntProperty(bgLayer, "height");
    TextureRegion texture =
        new TextureRegion(new Texture("levels/" + JsonHandler.getString(bgLayer, "image")));

    float x, y;
    if ((String) bgLayer.get("offsetx") == null) {
      x = width / 2f;
      y = height / 2f;
    } else {
      x = JsonHandler.getFloat(bgLayer, "offsetx") + width / 2f;
      y = JsonHandler.getFloat(bgLayer, "offsety") + height / 2f;
    }

    Vector2 pos = tiledToWorldCoords(new Vector2(x, y));

    background =
        new BackgroundImage(
            pos, (int) (width * WORLD_SCALE), (int) (height * WORLD_SCALE), texture, 100);
  }

  /**
   * Extracts the position of the player from the player layer.
   *
   * @param playerLayer the JSON Tiled player layer
   */
  private void parsePlayerLayer(HashMap<String, Object> playerLayer) {
    if (playerLayer.isEmpty()) {
      return;
    }

    HashMap<String, Object> playerWrapper =
        ((Array<HashMap<String, Object>>) playerLayer.get("objects")).get(0);

    float x = JsonHandler.getFloat(playerWrapper, "x");
    float y = JsonHandler.getFloat(playerWrapper, "y");
    Vector2 playerPos = new Vector2(x, y);

    playerLoc = tiledToWorldCoords(playerPos);
  }

  /**
   * Reads a JSON Tiled tile layer into an array of Tile objects
   *
   * @param tileLayer the JSON Tiled tile layer
   */
  private void parseTileLayer(HashMap<String, Object> tileLayer) {
    if (tileLayer.isEmpty()) {
      return;
    }

    Array<String> tileList = (Array<String>) tileLayer.get("data");

    for (int row = 0; row < TILED_WORLD_HEIGHT; row++) {
      for (int col = 0; col < TILED_WORLD_WIDTH; col++) {
        int index = col + row * TILED_WORLD_WIDTH;
        String s = tileList.get(index);
        int tileSetIndex = Integer.parseInt(s) - 1;
        if (tileSetIndex > 0) {
          TextureRegion tileTexture = indexToTexture(tileSetIndex);
          Vector2 pos = tiledCoordsFromIndex(index);
          pos = tiledToWorldCoords(pos);
          tiles.add(new Tile(tileTexture, pos.x, pos.y));
        }
      }
    }
  }

  /**
   * Extracts boss locations from a JSON boss layer
   *
   * @param bossLayer JSON object layer containing bosses
   */
  private void parseBossLayer(HashMap<String, Object> bossLayer) {
    layers.put("bosses", new Array<>());

    if (bossLayer.isEmpty()) {
      return;
    }

    Array<HashMap<String, Object>> bossWrapperList =
        (Array<HashMap<String, Object>>) bossLayer.get("objects");

    for (HashMap<String, Object> bossWrapper : bossWrapperList) {
      float x = JsonHandler.getFloat(bossWrapper, "x");
      float y = JsonHandler.getFloat(bossWrapper, "y");
      String name = JsonHandler.getString(bossWrapper, "name");
      int id;
      try {
        id = JsonHandler.getIntProperty(bossWrapper, "id");
      } catch (Error e) {
        id = -1;
      }

      Vector2 pos = tiledToWorldCoords(new Vector2(x, y));

      LevelObject boss = new LevelObject(pos.x, pos.y);
      boss.setBossName(name);
      boss.roomId = id;

      layers.get("bosses").add(boss);
    }
  }

  /**
   * Extracts wall objects from JSON wall layer
   *
   * @param wallLayer JSON object layer containing walls
   */
  private void parseWallLayer(HashMap<String, Object> wallLayer) {
    layers.put("walls", new Array<>());

    if (wallLayer.isEmpty()) {
      return;
    }

    Array<HashMap<String, Object>> wallWrapperList = JsonHandler.getArray(wallLayer, "objects");

    for (int i = 0; i < wallWrapperList.size; i++) {
      HashMap<String, Object> thisWallWrapper = wallWrapperList.get(i);
      Vector2 tiledCoords =
          new Vector2(
              JsonHandler.getFloat(thisWallWrapper, "x"),
              JsonHandler.getFloat(thisWallWrapper, "y"));

      Vector2 worldCoords = tiledToWorldCoords(tiledCoords);
      LevelObject thisWall = new LevelObject(worldCoords.x, worldCoords.y);

      Array<HashMap<String, Object>> vertexList = JsonHandler.getArray(thisWallWrapper, "polygon");

      for (HashMap<String, Object> vertex : vertexList) {
        float x = WORLD_SCALE * JsonHandler.getFloat(vertex, "x");
        float y = -WORLD_SCALE * JsonHandler.getFloat(vertex, "y");

        thisWall.addVertex(x);
        thisWall.addVertex(y);
      }
      layers.get("walls").add(thisWall);
    }
  }

  /**
   * Extracts obstacle objects from JSON obstacles layer
   *
   * @param obstacleLayer JSON object layer containing obstacles
   */
  private void parseObstacleLayer(HashMap<String, Object> obstacleLayer) {
    layers.put("obstacles", new Array<>());
    if (obstacleLayer.isEmpty()) {
      return;
    }

    Array<HashMap<String, Object>> obsWrapperList =
        (Array<HashMap<String, Object>>) obstacleLayer.get("objects");

    for (HashMap<String, Object> obsWrapper : obsWrapperList) {
      float x = Float.parseFloat((String) obsWrapper.get("x"));
      float y = Float.parseFloat((String) obsWrapper.get("y"));
      float width = Float.parseFloat((String) obsWrapper.get("width"));
      float height = Float.parseFloat((String) obsWrapper.get("height"));
      Vector2 pos = tiledToWorldCoords(new Vector2(x + width / 2f, y + height / 2f));
      Vector2 dims = (new Vector2(width * WORLD_SCALE, height * WORLD_SCALE));

      LevelObject obs = new LevelObject(pos.x, pos.y, dims.x, dims.y);
      try {
        obs.animated = JsonHandler.getBoolProperty(obsWrapper, "animated");
      } catch (Error e) {
        obs.animated = false;
      }

      // get frames in animation if animated
      try {
        obs.framesInAnimation = JsonHandler.getIntProperty(obsWrapper, "framesInAnimation");
        ;
      } catch (Error e) {
        obs.framesInAnimation = 1;
      }

      if (((String) obsWrapper.get("name")).length() > 0) {
        obs.texture = new TextureRegion(new Texture((String) obsWrapper.get("name")));
      }
      layers.get("obstacles").add(obs);
    }
  }

  /** Extracts gates objects from JSON gates layer */
  private void parseGatesLayer(HashMap<String, Object> gatesLayer) {
    layers.put("gates", new Array<>());
    if (gatesLayer.isEmpty()) {
      return;
    }

    Array<HashMap<String, Object>> gateWrapperList =
        (Array<HashMap<String, Object>>) gatesLayer.get("objects");

    // This is a map from IDs to a map of walls and sensors
    HashMap<Integer, HashMap<String, Array<LevelObject>>> gateGroups = new HashMap<>();
    Array<Integer> ids = new Array<>();

    // Populate gateGroups
    for (HashMap<String, Object> gateWrapper : gateWrapperList) {
      boolean isSensor = JsonHandler.getBoolProperty(gateWrapper, "sensor");
      int id = JsonHandler.getIntProperty(gateWrapper, "id");

      float width = JsonHandler.getFloat(gateWrapper, "width");
      float height = JsonHandler.getFloat(gateWrapper, "height");
      tempPos.set(
          JsonHandler.getFloat(gateWrapper, "x") + width / 2f,
          JsonHandler.getFloat(gateWrapper, "y") + height / 2f);
      tempPos = tiledToWorldCoords(tempPos);

      LevelObject thisObject =
          new LevelObject(tempPos.x, tempPos.y, WORLD_SCALE * width, WORLD_SCALE * height);

      // If this is the first object of a gateGroup, add it to the map
      if (!gateGroups.containsKey(id)) {
        gateGroups.put(id, new HashMap<>());
        gateGroups.get(id).put("sensors", new Array<>());
        gateGroups.get(id).put("walls", new Array<>());
        ids.add(id);
      }

      if (isSensor) {
        gateGroups.get(id).get("sensors").add(thisObject);
      } else {
        gateGroups.get(id).get("walls").add(thisObject);
      }
    }

    // Each gateGroup gets a level object
    for (int id : ids) {
      HashMap<String, Array<LevelObject>> thisGateGroup = gateGroups.get(id);
      LevelObject gateObject = new LevelObject();
      gateObject.walls = thisGateGroup.get("walls");
      gateObject.sensors = thisGateGroup.get("sensors");
      gateObject.roomId = id;

      layers.get("gates").add(gateObject);
    }
  }

  /**
   * Extracts portal objects from JSON portals layer
   *
   * @param portalLayer JSON object layer containing portals
   */
  private void parsePortalLayer(HashMap<String, Object> portalLayer) {
    layers.put("portals", new Array<>());
    if (portalLayer.isEmpty()) {
      return;
    }

    Array<HashMap<String, Object>> portWrapperList =
        (Array<HashMap<String, Object>>) portalLayer.get("objects");

    for (HashMap<String, Object> portWrapper : portWrapperList) {
      float x = Float.parseFloat((String) portWrapper.get("x"));
      float y = Float.parseFloat((String) portWrapper.get("y"));
      float width = Float.parseFloat((String) portWrapper.get("width"));
      float height = Float.parseFloat((String) portWrapper.get("height"));
      Vector2 pos = tiledToWorldCoords(new Vector2(x + width / 2f, y + height / 2f));
      Vector2 dims = (new Vector2(width * WORLD_SCALE, height * WORLD_SCALE));

      LevelObject portal;
      if (((String) portWrapper.get("name")).length() > 0) {
        TextureRegion texture = new TextureRegion(new Texture((String) portWrapper.get("name")));
        portal = new LevelObject(pos.x, pos.y, dims.x, dims.y, texture);
      } else {
        portal = new LevelObject(pos.x, pos.y, dims.x, dims.y);
      }
      portal.setTarget(JsonHandler.getStringProperty(portWrapper, "target"));

      try {
        portal.checkpointID = JsonHandler.getIntProperty(portWrapper, "requiredCheckpoint");
      } catch (Error e) {
        portal.checkpointID = -1;
      }
      Vector2 playerLoc =
          new Vector2(
              JsonHandler.getFloatProperty(portWrapper, "playerX"),
              JsonHandler.getFloatProperty(portWrapper, "playerY"));
      portal.playerLoc = playerLoc;
      layers.get("portals").add(portal);
    }
  }

  private void parseInteractableLayer(HashMap<String, Object> interactLayer) {
    layers.put("checkpoints", new Array<>());
    layers.put("healthpacks", new Array<>());
    if (interactLayer.isEmpty()) {
      return;
    }

    Array<HashMap<String, Object>> interactWrapperList =
        (Array<HashMap<String, Object>>) interactLayer.get("objects");

    for (HashMap<String, Object> interactWrapper : interactWrapperList) {
      float x = Float.parseFloat((String) interactWrapper.get("x"));
      float y = Float.parseFloat((String) interactWrapper.get("y"));
      float width = Float.parseFloat((String) interactWrapper.get("width"));
      float height = Float.parseFloat((String) interactWrapper.get("height"));
      Vector2 pos = tiledToWorldCoords(new Vector2(x + width / 2f, y + height / 2f));
      Vector2 dims = (new Vector2(width * WORLD_SCALE, height * WORLD_SCALE));
      String type = JsonHandler.getStringProperty(interactWrapper, "type");

      LevelObject obj = new LevelObject(pos.x, pos.y, dims.x, dims.y);
      if (!((String) interactWrapper.get("name")).isEmpty()) {
        obj.texture = new TextureRegion(new Texture((String) interactWrapper.get("name")));
      }

      switch (type) {
        case "checkpoint":
          obj.checkpointID = JsonHandler.getIntProperty(interactWrapper, "checkpointID");
          layers.get("checkpoints").add(obj);
          break;
        case "healthpack":
          layers.get("healthpacks").add(obj);
          break;
        default:
          break;
      }
    }
  }

  /**
   * Converts Tiled coordinates to physics world coordinates based on WORLD_SCALE.
   *
   * <p>Aligns the center of the Tiled world with the origin in the physics world
   *
   * @param tiledCoords vector position in Tiled coordinates
   * @return the vector position in physics world coordinates
   */
  public Vector2 tiledToWorldCoords(Vector2 tiledCoords) {
    float x = tiledCoords.x - (TILED_WORLD_WIDTH * TILE_SIZE) / 2f;
    float y = -(tiledCoords.y - (TILED_WORLD_HEIGHT * TILE_SIZE) / 2f);

    return new Vector2(WORLD_SCALE * x, WORLD_SCALE * y);
  }

  /**
   * Takes an index from a tile in the JSON 'data' array and converts it into a TextureRegion of the
   * corresponding tile in the tile set.
   *
   * @param index the index from the array
   * @return
   */
  public TextureRegion indexToTexture(int index) {
    // the tileset that the inputed texture is in
    int tileSetIndex = tileSets.size - 1;
    TextureRegion[][] thisTileSet = tileSets.get(tileSetIndex);
    // find which tile set the tile is in
    // index should never be > firstGids[last index]
    while (tileSetIndex >= 0) {
      if (index > firstGids.get(tileSetIndex)) {
        thisTileSet = tileSets.get(tileSetIndex);
      }
      tileSetIndex--;
    }

    // split the texture that the tile belongs to into tiles
    int numCols = thisTileSet.length;

    // return the specific tile
    TextureRegion a = thisTileSet[index / numCols][index % numCols];
    return thisTileSet[index / numCols][index % numCols];
  }

  /**
   * Finds the position of a tile in the world from its index in the tile layer array.
   *
   * <p>NOTE: This index is NOT the same as the index of the tile in the TileSet, it is the index in
   * the array of tile cells in the world.
   */
  public Vector2 tiledCoordsFromIndex(int index) {
    int x = TILE_SIZE * (index % TILED_WORLD_WIDTH) + TILE_SIZE / 2;
    int y = TILE_SIZE * (index / TILED_WORLD_WIDTH) + TILE_SIZE / 2;
    tempPos.set(x, y);
    return tempPos;
  }

  /**
   * Returns the name of the JSON used to load this level.
   *
   * <p>"levels/name.json" -> "name"
   */
  private String parseName(String fileName) {
    int start = fileName.indexOf("/") + 1;
    int end = fileName.indexOf(".");
    return fileName.substring(start, end);
  }
}
