package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;

public class LevelState {

    private Array<Integer> bossHps = new Array<>();
    private Vector2 playerLoc;
    private int playerHealth;
    private int playerAmmo;

    public LevelState(PlayerController player, Array<BossController> bossControllers) {
        this.playerAmmo = player.getAmmo();
        this.playerHealth = player.getHealth();
        this.playerLoc = player.getLocation();
        for (BossController bc : bossControllers) {
            this.bossHps.add(bc.getBoss().getHealth());
        }
    }

    public void update(PlayerController player, Array<BossController> bossControllers) {
        this.playerAmmo = player.getAmmo();
        this.playerHealth = player.getHealth();
        this.playerLoc = player.getLocation();
        this.getBossHps().clear();
        for (BossController bc : bossControllers) {
            this.bossHps.add(bc.getBoss().getHealth());
        }
    }

    public int getPlayerAmmo() {return playerAmmo;}
    public int getPlayerHealth() {return playerHealth;}
    public Vector2 getPlayerLoc() {return playerLoc;}

    public Array<Integer> getBossHps() {return bossHps;}

    // Use this to store more boss state if we ever need more than just hp
    private class BossState {
        private int bossHealth;
        public BossState(BossController bc) {
        }

    }
}
