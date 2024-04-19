package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.utils.*;
import edu.cornell.jade.seasthethrone.BuildConfig;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.level.LevelState;

import java.io.*;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Scanner;

public class StateController {

    /** All levels/rooms the player has been in */
    private HashMap<String, LevelState> storedLevels;

    /** The level/room the player is currently in */
    private LevelState currentLevel;

    /** The player's most recently updated ammo count */
    private int playerAmmo;

    /** The player's most recently updated hp */
    private int playerHealth;

    /** The ID of the player's most recent checkpoint */
    private int checkpoint;

    /** The index of the current save file */
    private int saveIndex;

    public StateController() {
        this.storedLevels = new HashMap<>();
        this.checkpoint = -1;
        this.saveIndex = 1;
    }

    /** Copies the current in-game state into this controller */
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
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        String out = json.prettyPrint(json.toJson(this));
        if (BuildConfig.DEBUG) {
          System.out.println("save "+out);
        }
        try {
            File myObj = new File("saves/save"+saveIndex+".json");
            if (myObj.createNewFile()) {
                System.out.println("New save: " + "save"+saveIndex+".json");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("saves/save"+saveIndex+".json");
            myWriter.write(out);
            myWriter.close();
            System.out.println("Game saved!");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /** Loads in state from a json */
    public void loadGame(String saveName) throws FileNotFoundException {
        Scanner input = new Scanner(new File(saveName));
        java.lang.StringBuilder loadString = new StringBuilder();
        while (input.hasNext()) {
            loadString.append(input.next());
        }

        String out = loadString.toString();
        JsonValue loadState = new JsonReader().parse(out);

        this.checkpoint = loadState.getInt("checkpoint");
//        this.currentLevel = loadState.currentLevel;
        this.playerHealth = loadState.getInt("playerHealth");
        try {
            this.playerAmmo = loadState.getInt("playerAmmo");
        } catch (IllegalArgumentException e) {
            this.playerAmmo = 0;
        }

        JsonValue levelsRoot =  loadState.get("storedLevels");
        this.storedLevels.clear();
        for (int i = 0; i < levelsRoot.size; i++) {
            LevelState thisLevel = new LevelState( levelsRoot.get(i).get("bossHps").asIntArray());
            storedLevels.put(levelsRoot.get(i).name, thisLevel);
        }
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
        this.checkpoint = Math.max(checkpoint, this.checkpoint);
        saveGame();
    }

    public int getPlayerAmmo() {
        return playerAmmo;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }
}
