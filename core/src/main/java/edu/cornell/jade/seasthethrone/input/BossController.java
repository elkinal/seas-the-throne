package edu.cornell.jade.seasthethrone.input;

import java.util.Random;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.gamemodel.BossModel;

public class BossController implements Controllable {
    private BossModel boss;

    private static enum StateEnum {
        IDLE,
        MOVE
    }

    private StateEnum state;
    private Vector2 target = new Vector2();
    private float speed;
    private long ticks;
    private long currStateDuration;
    private long STATE_TIME_LIMIT = 300;

    private Random rand = new Random();


    public BossController(BossModel boss) {
        this.boss = boss;
        state = StateEnum.IDLE;
        speed = 2f;
        ticks = 0;
        currStateDuration = 0;
    }

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

    private float distanceToTarget() {
        return Vector2.dst(target.x, target.y, boss.getX(), boss.getY());
    }

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
