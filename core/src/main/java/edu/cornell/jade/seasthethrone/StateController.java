package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.gamemodel.CheckpointModel;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.level.LevelState;
import com.badlogic.gdx.Preferences;

import java.io.*;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Scanner;

public class StateController {

  private Preferences prefs;

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

  /** Where to respawn the player when they die or restart */
  private Vector2 respawnLoc;

  /** The name of the level to respawn to */
  private String respawnLevel;

  /** The index of the current save file */
  private int saveIndex;

  /** Json initialization for (de)serializing level state */
  private final Json json;

  public StateController() {
    this.storedLevels = new HashMap<>();
    this.checkpoint = 8;
    this.saveIndex = 1;
    this.prefs = Gdx.app.getPreferences("save");
    json = new Json();
    json.setOutputType(JsonWriter.OutputType.json);
  }

  /** Copies the current in-game state into this controller */
  public void updateState(String levelName, PlayerController player, Array<BossController> bosses) {
    // Update player state
    this.playerAmmo = player.getAmmo();
    this.playerHealth = player.getHealth();

    // Update level state in stored levels
    if (storedLevels.containsKey(levelName)) {
      LevelState levelState = storedLevels.get(levelName);
      levelState.updateBosses(bosses);
    } else {
      storedLevels.put(levelName, new LevelState(bosses));
    }
  }

  /** Serializes the state of this controller to a json */
  public void saveGame() {
    if (BuildConfig.DEBUG) {
      System.out.println("Saving game");
      System.out.println("Checkpoint "+this.checkpoint+" activated");
    }
    prefs.putInteger("checkpoint", this.checkpoint);
    prefs.putInteger("player health", this.playerHealth);
    prefs.putInteger("player ammo", this.playerAmmo);
    prefs.putString("respawn level", this.respawnLevel);
    try {
      prefs.putFloat("respawn x", this.respawnLoc.x);
      prefs.putFloat("respawn y", this.respawnLoc.y);
      if (BuildConfig.DEBUG) {
        System.out.println("Saved respawn loc: "+respawnLoc);
      }
    } catch (NullPointerException e) {
      System.out.println("NullPointer on save respawn loc");
    }

    String storedLevelsString = json.prettyPrint(json.toJson(storedLevels));
    prefs.putString("stored levels", storedLevelsString);
    prefs.flush();
  }

  /** Loads in state from a json */
  public void loadState() {
    try {
      this.checkpoint = prefs.getInteger("checkpoint", -1);
      this.playerHealth = prefs.getInteger("playerHealth", 5);
      try {
        this.playerAmmo = prefs.getInteger("playerAmmo", 0);
        System.out.println("loaded ammo: " + this.playerAmmo);
      } catch (IllegalArgumentException e) {
        this.playerAmmo = 0;
      }

      String storedLevelsString = prefs.getString("stored levels", "");
      this.storedLevels.clear();

     JsonValue levelsRoot = new JsonReader().parse(storedLevelsString);
      for (int i = 0; i < levelsRoot.size; i++) {
        LevelState thisLevel = new LevelState(levelsRoot.get(i).get("bossHps").asIntArray());
        storedLevels.put(levelsRoot.get(i).name, thisLevel);

        if (BuildConfig.DEBUG) System.out.println("State loaded successfully");
      }
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        System.out.println("Loading defaults");
      }
      this.checkpoint = -1;
      this.playerHealth = 5;
      this.playerAmmo = 0;
    }
  }

  public void reset() {
    for (String key : storedLevels.keySet()) {
      storedLevels.get(key).clear();

    }
  }

  public void clear() {
    this.checkpoint = -1;
    storedLevels.clear();
  }

  /** Returns if this controller has saved state on the specified level */
  public boolean hasLevel(String name) {
    return storedLevels.containsKey(name);
  }

  public void setRespawnLoc(Vector2 respawnLoc) {
    this.respawnLoc = respawnLoc;
  }

  public Vector2 getRespawnLoc() {
    return respawnLoc;
  }

  public boolean hasRespawnLoc() {
    return respawnLoc != null && !respawnLevel.isEmpty();
  }

  public void setRespawnLevel(String respawnLevel) {
    this.respawnLevel = respawnLevel;
  }

  public String getRespawnLevel() {
    return this.respawnLevel;
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
  }

  public int getPlayerAmmo() {
    return playerAmmo;
  }

  public int getPlayerHealth() {
    return playerHealth;
  }
}
