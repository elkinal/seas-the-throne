package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;

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

    public void saveGame() {}

    public void loadGame() {}

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
    }

    public int getPlayerAmmo() {
        return playerAmmo;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }
}
