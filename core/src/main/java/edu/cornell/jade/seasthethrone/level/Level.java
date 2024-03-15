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

import javax.swing.text.View;
import java.util.HashMap;
import java.util.Vector;


public class Level {

    private final Array<HashMap<String, Object>> layers;

    private final Vector2 playerLoc;

    private Array<BossModel> bosses;

    private Array<EnemyModel> enemies;

    private Array<Model> walls;

    private final BackgroundImage background;

    private HashMap<String, Object> tiles;

    /** Width of the game world in Box2d units */
    public float DEFAULT_WIDTH = 64.0f;

    /** Height of the game world in Box2d units */
    public float DEFAULT_HEIGHT = 48.0f;

    /** Ratio between the pixel in a texture and the meter in the world */
    private static final float WORLD_SCALE = 0.1f;

    private final int TILE_SIZE = 32;

    private FitViewport viewport;

    public Level(String fileName) {
        this.viewport = new FitViewport(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        HashMap<String, Object> levelMap = JsonHandler.jsonToMap(fileName);
        layers = (Array<HashMap<String, Object>>)levelMap.get("layers");

        background = new BackgroundImage(getLayer("background"));
        playerLoc = parsePlayerLayer(getLayer("player"));
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


    /**
     * Extracts the position of the player from the player layer and creates a PlayerModel.
     *
     * @param playerLayer the JSON Tiled layer containing the player
     *
     * @return A PlayerModel initialized at the proper coordinates
     * */
    private Vector2 parsePlayerLayer(HashMap<String, Object> playerLayer) {
        float x = Float.parseFloat((String)playerLayer.get("x"));
        float y = Float.parseFloat((String)playerLayer.get("y"));
        return new Vector2(x,y);
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
        return null;
    }

    /**
     * Takes an index from a tile in the JSON 'data' array and converts it into a TextureRegion
     * of the corresponding tile in the tile set.
     * @param index the index from the array
     * @param firstID an array of all 'firstgid' values of each tile set,
     *                which represents the index of the top left tile in the tile set
     * @param tileSets array of every tileSet, as a Texture
     * @param firstGids array of the first indices of every tile set
     * @return
     */
    public TextureRegion indexToTexture(int index, int[] firstID, Texture[] tileSets, int[] firstGids){
        //the tileset that the inputed texture is in
        int tileSheet = 0;

        //find which tile set the tile is in
        //index should never be > firstGids[last index]
        while(index > firstGids[tileSheet + 1]){
            tileSheet++;
        }

        //split the texture that the tile belongs to into tiles
        TextureRegion[][] tiles = new TextureRegion(tileSets[tileSheet]).split(TILE_SIZE, TILE_SIZE);
        int numCols = tiles.length;

        //return the specific tile
        return tiles[index/numCols][index%numCols];
    }

}
