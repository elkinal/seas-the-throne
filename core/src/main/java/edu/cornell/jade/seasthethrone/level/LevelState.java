package edu.cornell.jade.seasthethrone.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.PlayerController;
import edu.cornell.jade.seasthethrone.ai.BossController;

public class LevelState {

    private Array<Integer> bossHps = new Array<>();

    public LevelState(Array<BossController> bossControllers) {
        for (BossController bc : bossControllers) {
            this.bossHps.add(bc.getBoss().getHealth());
        }
    }

    public LevelState(int[] hps) {
        for (int hp : hps) {
            this.bossHps.add(hp);
        }
    }

    public void update(Array<BossController> bossControllers) {
        this.getBossHps().clear();
        for (BossController bc : bossControllers) {
            this.bossHps.add(bc.getBoss().getHealth());
        }
    }

    public Array<Integer> getBossHps() {return bossHps;}

    // Use this to store more boss state if we ever need more than just hp
    private class BossState {
        private int bossHealth;
        public BossState(BossController bc) {
        }

    }
}
