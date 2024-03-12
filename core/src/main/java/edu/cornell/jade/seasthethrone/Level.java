package edu.cornell.jade.seasthethrone;


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


    public Level(String fileName) {
        player = new PlayerModel(0,0);
        bosses = new Array<>();
        enemies = new Array<>();

        HashMap<String, Object> levelMap = JsonHandler.jsonToMap(fileName);

        Array<HashMap<String, Object>> layers = (Array<HashMap<String, Object>>)levelMap.get("layers");

        background = layers.get(0);


    }

}
