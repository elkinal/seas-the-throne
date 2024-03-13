package edu.cornell.jade.seasthethrone;


import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.*;
import edu.cornell.jade.seasthethrone.util.JsonHandler;

import java.util.HashMap;


public class Level {

    private PlayerModel player;

    private Array<BossModel> bosses;

    private Array<EnemyModel> enemies;

    private HashMap<String, Object> tiles;

    private HashMap<String, Object> background;

    private Array<HashMap<String, Object>> layers;


    public Level(String fileName) {
        HashMap<String, Object> levelMap = JsonHandler.jsonToMap(fileName);
        layers = (Array<HashMap<String, Object>>)levelMap.get("layers");

        player = parsePlayerLayer(getLayer("player"));
        bosses = parseBossLayer(getLayer("bosses"));
        enemies = parseEnemyLayer(getLayer("enemies"));

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

    /**
     * Extracts the position of the player from the player layer and creates a PlayerModel.
     *
     * @param playerLayer the JSON Tiled layer containing the player
     *
     * @return A PlayerModel initialized at the proper coordinates
     * */
    private PlayerModel parsePlayerLayer(HashMap<String, Object> playerLayer) {
        return new PlayerModel((float)playerLayer.get("x"), (float)playerLayer.get("y"));
    }
    private Array<EnemyModel> parseEnemyLayer(HashMap<String, Object> objLayer) {
        throw new UnsupportedOperationException("parseEnemyLayer not implemented");
    }

    private Array<BossModel> parseBossLayer(HashMap<String, Object> BossLayer) {
        throw new UnsupportedOperationException("parseBossLayer not implemented");
    }



}
