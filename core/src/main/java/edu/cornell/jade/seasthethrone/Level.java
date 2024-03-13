package edu.cornell.jade.seasthethrone;


import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.util.JsonHandler;

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

    private Array<HashMap<String, Object>> layers;


    public Level(String fileName) {
        HashMap<String, Object> levelMap = JsonHandler.jsonToMap(fileName);
        layers = (Array<HashMap<String, Object>>)levelMap.get("layers");

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
            if ((String) layer.get("name") == layerName) {
                return layer;
            }
        }
        throw new Error("No layer with name " + layerName);
    }

}
