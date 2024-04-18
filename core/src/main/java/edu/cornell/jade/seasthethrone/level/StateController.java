package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.utils.*;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class StateController {

    /** All levels/rooms the player has been in */
    private HashMap<String, LevelState> storedLevels;

    /** The level/room the player is currently in */
    private LevelState currentLevel;

    /** The player's most recently updated hp */
    private int playerHealth;

    /** The player's most recently updated ammo count */
    private int playerAmmo;

    /** The ID of the player's most recent checkpoint */
    private int checkpoint;

    /** Json object to serialize */
    private Json json = new Json();

    /** Json reader to deserialize */
    JsonReader reader = new JsonReader();

    public StateController() {
        this.storedLevels = new HashMap<>();
        this.checkpoint = -1;
    }

    public void updateState(String levelName, PlayerController player, Array<BossController> bosses) {
        // Update player state
        this.playerAmmo = player.getAmmo();
        this.playerHealth = player.getHealth();

        // Update level state in stored levels
        if (storedLevels.containsKey(levelName)) {
            storedLevels.get(levelName).update(bosses);
        } else {
            storedLevels.put(levelName, new LevelState(bosses));
        }
    }

    /** Serializes the state of this controller to a json */
    public void saveGame() {
        json.setOutputType(JsonWriter.OutputType.json);
        String out = json.prettyPrint(json.toJson(this));
        try {
            File myObj = new File("saves/save1.json");
            if (myObj.createNewFile()) {
                System.out.println("New save created");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("saves/save1.json");
            myWriter.write(out);
            myWriter.close();
            System.out.println("Game saved!");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /** Loads in state from a json */
    public void loadGame(String saveName) {
        JsonReader reader = new JsonReader();
        JsonValue loadState = reader.parse("levels/save1.json");
//        this.storedLevels = loadState.g;
        this.checkpoint = loadState.getInt("checkpoint");
//        this.currentLevel = loadState.currentLevel;
        this.playerHealth = loadState.getInt("playerHealth");
//        this.playerAmmo = loadState.playerAmmo;

    }

    /** Returns if this controller has saved state on the specified level */
    public boolean hasLevel(String name) {
        return storedLevels.containsKey(name);
    }

    /** Returns the specified level */
    public LevelState getLevel(String name) {
        return storedLevels.get(name);
    }

    /** Returns the state of level the player is currently in */
    public LevelState getCurrentLevel() {
        return currentLevel;
    }

    /** Sets the current level state to a specified stored state */
    public void setCurrentLevel(String name) {
        this.currentLevel = storedLevels.get(name);
    }

    public int getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(int checkpoint) {
        this.checkpoint = checkpoint;
        saveGame();
    }

    public int getPlayerAmmo() {
        return playerAmmo;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }
}
