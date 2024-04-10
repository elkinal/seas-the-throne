package edu.cornell.jade.seasthethrone;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.CrabBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.JellyBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

public class JellyBossController implements BossController {
    /** Enumeration of AI states. */
    private static enum State {
        /** The boss is stationary */
        IDLE,
        /** The boss is attacking */
        ATTACK,
        /** The boss is moving towards a target */
        MOVE,
        /** The boss has been defeated */
        DEAD,
    }

    /*
     * -----------------------------------
     * CONSTANTS
     * -----------------------------------
     */
    /** The distance the player must be from the boss before it begins attacking. */
    private static float AGRO_DISTANCE = 30f;

    /*
     * -------------------------------
     * STATE
     * -------------------------------
     */
    /** The model being controlled */
    private JellyBossModel boss;

    /** The player model being attacked */
    private PlayerModel player;

    /** The boss's current state */
    private JellyBossController.State state;


    public JellyBossController(JellyBossModel boss, PlayerModel player, PhysicsEngine physicsEngine) {
        this.boss = boss;
        this.player = player;
        this.state = State.IDLE;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void remove() {
        boss.markRemoved(true);
    }

    @Override
    public boolean isTerminated() {
        return boss.isTerminated();
    }

    @Override
    public void dispose() {

    }

    public void attack() {

    }

    /** Progresses to the next state of the controller. */
    private void nextState() {
        switch (state) {
            case IDLE:
                if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE) {
                    state = JellyBossController.State.ATTACK;
                }
                break;
            case ATTACK:
                break;
            case MOVE:
                break;
            case DEAD:
                break;
        }
    }

    /** Performs actions based on the controller state */
    private void act() {
        switch (state) {
            case IDLE:
                break;
            case ATTACK:
                attack();
                break;
            case MOVE:
                break;
            case DEAD:
                break;
        }
    }
}
