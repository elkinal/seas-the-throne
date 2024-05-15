package edu.cornell.jade.seasthethrone.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.patterns.*;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.FinalBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

import java.util.Random;

public class FinalBossController implements BossController {
  /** Enumeration of AI states. */
  private static enum State {
    /** The boss is stationary */
    IDLE,
    DELAY_SPIRAL_ROTATE,
    DELAY_SPEED_RING,
    /** The boss is dead */
    DEAD,
  }

  /*
   * -----------------------------------
   * CONSTANTS
   * -----------------------------------
   */
  /** The distance the player must be from the boss before it begins attacking. */
  private static float AGRO_DISTANCE = 15f;

  /** The minimum distance the boss must move during a movement cycle. */
  private static float MIN_MOVE_DIST = 30f;

  /*
   * -------------------------------
   * STATE
   * -------------------------------
   */
  /** The model being controlled */
  private FinalBossModel boss;

  /** The player model being attacked */
  private PlayerModel player;

  /** The boss's current state */
  private State state;

  /** The timer for state switching */
  private int timer;

  /** The current goal position of the boss */
  private Vector2 goalPos;

  /** Random number generator */
  private Random rand;

  /** The bounds of which the boss can move */
  private Rectangle bounds;


  /** The unbreakable spinning ring for the first phase */
  private final AttackPattern unbreakableRing;

  /** The unbreakable spiral with a delayed rotate attack */
  private final AttackPattern unbreakableDelayRotateSpiralAttack;

  private final AttackPattern unbreakableDelaySpeedRingAttack;

  /**
   * Constructs a head boss controller
   *
   * @param boss head model being mutated
   * @param player player model being attacked
   * @param builder a builder to create bullet models
   * @param physicsEngine physics engine to add bullet attack to
   */
  public FinalBossController(
          FinalBossModel boss,
          PlayerModel player,
          BulletModel.Builder builder,
          PhysicsEngine physicsEngine) {
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;

    this.goalPos = new Vector2();
    this.rand = new Random();
    this.bounds = new Rectangle(boss.getX() - 25, boss.getY() - 15, 50, 40);

    this.unbreakableDelayRotateSpiralAttack = new DelayedRotateSpiralAttack(boss, 7, 25, 100,
            3*MathUtils.PI/4, true, builder, physicsEngine);
    this.unbreakableDelaySpeedRingAttack = new DelayedSpeedRingAttack(100, 13, 70,
      2*MathUtils.PI, 6f, 20f, true, boss, builder, physicsEngine);
    this.unbreakableRing = new UnbreakableSpinningRing(5f,9, 150, boss, builder, physicsEngine);

  }

  @Override
  public int getHealth() {
    return boss.getHealth();
  }

  @Override
  public int getMaxHealth() { return boss.getFullHealth(); }

  @Override
  public boolean isDead() {
    return boss.isDead();
  }

  @Override
  public boolean isBoss() {
    return true;
  }

  /** Loads in the boss's previous state */
  public void transferState(int storedHp) {
    this.boss.setHealth(storedHp);
  }

  /** Marks the boss for removal from the physics engine. */
  @Override
  public void remove() {
    boss.markRemoved(true);
  }

  /** Cleans up this boss's attack pattern */
  public void dispose() {
//    unbreakableRingAttack.cleanup();
//    unbreakableSpiralAttack.cleanup();
//    unbreakableRing.cleanup();
//    aimedRandomAttack.cleanup();
//    ringAttack.cleanup();
  }

  /** Returns the boss of this controller */
  public BossModel getBoss() {
    return boss;
  }

  /**
   * Called every tick. Updates the state of the model based on the controller state.
   *
   * @param delta time since update was last called
   */
  @Override
  public void update(float delta) {
    nextState();
    act();
  }

  /** Progresses to the next state of the controller. */
  private void nextState() {
    if (boss.isDead()) {
      state = State.DEAD;
    } else if (boss.reachedHealthThreshold()) {
    }

    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE && boss.isInRoom()) {
          state = State.IDLE;
        }
        break;
      case DEAD:
        dispose();
        break;
    }
  }

  /** Performs actions based on the controller state */
  private void act() {
    switch (state) {
      case IDLE:
        break;
      case DELAY_SPIRAL_ROTATE:
        unbreakableDelayRotateSpiralAttack.update(player.getX(), player.getY());
        break;
      case DELAY_SPEED_RING:
        unbreakableDelaySpeedRingAttack.update(player.getX(), player.getY());
        break;
      case DEAD:
        break;
    }
    if (state != State.IDLE && state != State.DEAD) unbreakableRing.update(player.getX(), player.getY());
  }

  /** If the goal pos was reached */
  private boolean goalPosReached() {
    return boss.getPosition().sub(goalPos).dot(boss.getLinearVelocity()) > 0;
  }

  /** Helper function to generate new goal position & set boss velocity. * */
  private void findNewGoalPos(int vel) {
    goalPos.set(
            rand.nextFloat(bounds.getX(), bounds.getX() + bounds.getWidth()),
            rand.nextFloat(bounds.getY(), bounds.getY() + bounds.getHeight()));
    while (boss.getPosition().dst(goalPos) < MIN_MOVE_DIST) {
      goalPos.set(
              rand.nextFloat(bounds.getX(), bounds.getX() + bounds.getWidth()),
              rand.nextFloat(bounds.getY(), bounds.getY() + bounds.getHeight()));
    }
    boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-vel));
  }
}
