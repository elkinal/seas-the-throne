package edu.cornell.jade.seasthethrone.level;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.model.*;
import edu.cornell.jade.seasthethrone.util.JsonHandler;
import com.badlogic.gdx.math.Vector2;

import javax.annotation.processing.SupportedSourceVersion;
import javax.swing.text.View;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.StreamSupport;


public class Level {
    private final Array<HashMap<String, Object>> layers;

    private final Vector2 playerLoc;

    private Array<BossModel> bosses;

    private Array<EnemyModel> enemies;

    private Array<Model> walls;

    private final BackgroundImage background;

    private Array<Tile> tiles;

    /** Width of the game world in Box2d units */
    public float DEFAULT_WIDTH = 64.0f;

    /** Height of the game world in Box2d units */
    public float DEFAULT_HEIGHT = 48.0f;

    /** Ratio between the pixel in a texture and the meter in the world */
    private static final float WORLD_SCALE = 0.1f;

    private final int TILE_SIZE;

    /** Width of the Tiled map in tiles*/
    private int TILED_WORLD_WIDTH;

    /** Height of the Tiled map in tiles*/
    private int TILED_WORLD_HEIGHT;

    /** The array of tileSets, each tileSet being a nested list of textures representing tiles */
    private Array<TextureRegion[][]> tileSets = new Array<>();

    private Array<Integer> firstGids = new Array<>();

    private FitViewport viewport;

    private Vector2 tempPos;

    public Level(String fileName) {
        this.viewport = new FitViewport(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        tempPos = new Vector2();

        // Load JSON to map
        HashMap<String, Object> levelMap = JsonHandler.jsonToMap(fileName);
        // Load in level constants
        TILE_SIZE = Integer.parseInt((String) levelMap.get("tilewidth"));
        TILED_WORLD_HEIGHT = Integer.parseInt((String) levelMap.get("height"));
        TILED_WORLD_WIDTH = Integer.parseInt((String) levelMap.get("width"));

        // Create tileSets
        Array<HashMap<String, Object>> tileSetsList = (Array<HashMap<String, Object>>) levelMap.get("tilesets");
        for (HashMap<String, Object> tileSet : tileSetsList) {
            // For each tileSet
            Texture thisTexture = new Texture((String) tileSet.get("image"));
            int thisGid = Integer.parseInt((String) tileSet.get("firstgid"));
            firstGids.add(thisGid);
            // Split this tileSet up into textures
//            TextureRegion[][] a = new TextureRegion(thisTexture).split(TILE_SIZE, TILE_SIZE);
//            System.out.println("tile height: "+a[0][0].getRegionHeight());
             tileSets.add(new TextureRegion(thisTexture).split(TILE_SIZE, TILE_SIZE));
        }

        // Load in layers
        layers = (Array<HashMap<String, Object>>)levelMap.get("layers");

        background = new BackgroundImage(getLayer("background"));

        playerLoc = parsePlayerLayer(getLayer("player"));
        tiles = parseTileLayer(getLayer("tiles"));
//        bosses = parseBossLayer(getLayer("bosses"));
//        enemies = parseEnemyLayer(getLayer("enemies"));

    }

    /**
     * Returns the layer with the given name
     *
     * @param layerName the name of the layer to return
     *
     * @return the layer with the given name
     *
     * @throws Error if the provided name doesn't match any layer in the level
     * */
    private HashMap<String, Object> getLayer(String layerName) {
        for (HashMap<String, Object> layer : layers) {
            if (( (String)layer.get("name") ).equals(layerName)) {
                return layer;
            }
        }
        throw new Error("No layer with name " + layerName);
    }

    public BackgroundImage getBackground() { return background; }

    public Vector2 getPlayerLoc() { return playerLoc; }

    public FitViewport getViewport() { return viewport; }

    public Array<Tile> getTiles() { return tiles; }


    /**
     * Extracts the position of the player from the player layer and creates a PlayerModel.
     *
     * @param playerLayer the JSON Tiled player layer
     *
     * @return A PlayerModel initialized at the proper coordinates
     * */
    private Vector2 parsePlayerLayer(HashMap<String, Object> playerLayer) {
        HashMap<Object, String> playerWrapper = ((Array<HashMap<Object, String>>) playerLayer.get("objects")).get(0);

        float x = Float.parseFloat((String)playerWrapper.get("x"));
        float y = Float.parseFloat((String)playerWrapper.get("y"));
        Vector2 playerPos = new Vector2(x,y);

        System.out.println("player pos: " + tiledToWorldCoords(playerPos));
        return tiledToWorldCoords(playerPos);
    }

    /**
     * Reads a JSON Tiled tile layer into an array of Tile objects
     *
     * @param tileLayer the JSON Tiled tile layer
     *
     * @return an array of Tile objects
     * */
    private Array<Tile> parseTileLayer(HashMap<String, Object> tileLayer) {
        Array<Tile> tiles = new Array<>();
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
        return tiles;
    }

    private Array<EnemyModel> parseEnemyLayer(HashMap<String, Object> enemyLayer) {
        throw new UnsupportedOperationException("parseEnemyLayer not implemented");
    }

    private Array<BossModel> parseBossLayer(HashMap<String, Object> bossLayer) {
        throw new UnsupportedOperationException("parseBossLayer not implemented");
    }

    /**
     * 20 px in Tiled is 1 meter in Box2d
     * */
    private Vector2 tiledToWorldCoords(Vector2 tiledCoords) {
        float x = tiledCoords.x - (TILED_WORLD_WIDTH * TILE_SIZE) / 2f;
        float y  = - (tiledCoords.y - (TILED_WORLD_HEIGHT * TILE_SIZE) / 2f);

        return new Vector2(WORLD_SCALE * x, WORLD_SCALE * y);
    }

    /**
     * Takes an index from a tile in the JSON 'data' array and converts it into a TextureRegion
     * of the corresponding tile in the tile set.
     *
     * @param index the index from the array
     * @return
     */
    public TextureRegion indexToTexture(int index){
        //the tileset that the inputed texture is in
        int tileSetIndex = tileSets.size - 1;
        TextureRegion[][] thisTileSet = tileSets.get(tileSetIndex);
        //find which tile set the tile is in
        //index should never be > firstGids[last index]
        while(tileSetIndex >= 0){
            if (index > firstGids.get(tileSetIndex)) {
                thisTileSet = tileSets.get(tileSetIndex);
            }
            tileSetIndex--;
        }

        //split the texture that the tile belongs to into tiles
        int numCols = thisTileSet.length;

        //return the specific tile
        TextureRegion a = thisTileSet[index/numCols][index%numCols];
        return thisTileSet[index/numCols][index%numCols];
    }

    /**
     * Finds the position of a tile in the world from its index in the tile layer array.
     *
     * NOTE: This index is NOT the same as the index of the tile in the TileSet, it is
     * the index in the array of tile cells in the world.
     * */
    public Vector2 tiledCoordsFromIndex(int index) {
        int x = TILE_SIZE * (index % TILED_WORLD_WIDTH);
        int y = TILE_SIZE * (index / TILED_WORLD_WIDTH);
        tempPos.set(x,y);
        return tempPos;
    }
}
