package edu.cornell.jade.seasthethrone.input;

import java.util.Random;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.gamemodel.BossModel;

public class BossController implements Controllable {
    /** The boss this controller controls */
    private BossModel boss;
    /** Enumeration of this boss's AI states */
    private static enum StateEnum {
        /** The boss is stationary */
        IDLE,
        /** The boss is moving towards its target */
        MOVE
    }
    /** This boss's current state */
    private StateEnum state;
    /** The position to move to in world coords */
    private Vector2 target = new Vector2();
    /** Controls how fast the boss moves */
    private float speed;
    /** Counter of ticks since this boss was initialized */
    private long ticks;
    /** Counter of ticks spent in current state, resets when state changes */
    private long currStateDuration;
    /** Maximum number of ticks to spend in a state before switching */
    private long STATE_TIME_LIMIT = 260;

    private Random rand = new Random();


    public BossController(BossModel boss) {
        this.boss = boss;
        state = StateEnum.IDLE;
        speed = 2.5f;
        ticks = 0;
        currStateDuration = 0;
    }

    /** Called every tick. Checks to update state every 10 ticks */
    public void update() {
        ticks++;
        if (ticks % 10 == 0) {
            changeStateIfApplicable();
            switch (state) {
                case IDLE:
                    break;
                case MOVE:
                    break;

            }
        }
    }

    /**
     * If in IDLE: switches to MOVE if time limit is reached, while also setting a new target and velocity
     * based on that target.
     *
     * If in MOVE: switches to IDLE if time limit is reached or close to target, while setting velocity to 0.
     * */
    private void changeStateIfApplicable() {
        currStateDuration += 10;
        switch (state) {
            case IDLE:
                if (currStateDuration >= STATE_TIME_LIMIT) {
                    selectTarget();
                    state = StateEnum.MOVE;
                    Vector2 direction = target.sub(boss.getPosition()).nor();
                    boss.setVX(direction.x * speed);
                    boss.setVY(direction.y * speed);
                    currStateDuration = 0;
                }
                break;
            case MOVE:
                if (currStateDuration >= STATE_TIME_LIMIT || distanceToTarget() < 0.5f) {
                    state = StateEnum.IDLE;
                    boss.setVX(0);
                    boss.setVY(0);
                    currStateDuration = 0;
                }
                break;
            default:
                state = StateEnum.IDLE;
                break;
        }
    }

    /** @return the distance from the boss to its MOVE target */
    private float distanceToTarget() {
        return Vector2.dst(target.x, target.y, boss.getX(), boss.getY());
    }

    /** Selects a random point within a bounded region as the MOVE target */
    private void selectTarget() {
        float x = 24 * (0.5f - rand.nextFloat());
        float y = 16 * (0.5f - rand.nextFloat());
        target.set(x,y);
    }

    @Override
    public void moveHorizontal(float movement) {}

    @Override
    public void moveVertical(float movement) {}

    @Override
    public void pressPrimary() {}

    @Override
    public void updateDirection(Vector2 mouseDir) {}

    @Override
    public Vector2 getLocation() {
        return boss.getPosition();
    }
}
