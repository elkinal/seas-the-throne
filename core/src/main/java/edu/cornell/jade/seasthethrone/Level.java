package edu.cornell.jade.seasthethrone;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.util.JsonHandler;

import java.util.ArrayList;
import java.util.HashMap;


public class Level {
    /*
    * Note on Tiled
    *
    * The position of tiles is from their bottom left corner.
    *
    * Origin of position coords is top-left corner, same as gameCanvas.
    * */

    private PlayerModel player;

    private Array<BossModel> bosses;

    private Array<EnemyModel> enemies;

    private HashMap<String, Object> tiles;

    private HashMap<String, Object> background;

    private final int TILE_SIZE = 32;


    public Level(String fileName) {
        player = new PlayerModel(0,0);
        bosses = new Array<>();
        enemies = new Array<>();

        HashMap<String, Object> levelMap = JsonHandler.jsonToMap(fileName);

        Array<HashMap<String, Object>> layers = (Array<HashMap<String, Object>>)levelMap.get("layers");

        background = layers.get(0);


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
