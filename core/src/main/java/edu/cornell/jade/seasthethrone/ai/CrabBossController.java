package edu.cornell.jade.seasthethrone.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.patterns.*;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;
import java.util.Random;

/** A controller defining the bahavior of a crab boss. */
public class CrabBossController implements BossController {
  /** Enumeration of AI states. */
  private static enum State {
    /** The boss is stationary */
    IDLE,
    /** The boss reached a threshold*/
    THRESHOLD,
    /** The boss is attacking with delayed rotating arcs */
    ROTATE_RING_ATTACK,
    /** The boss is attacking with delayed homing sped up bullets */
    HOMING_SPEED_ATTACK,
    /** The boss is attacking with a delayed homing ring while moving */
    ATTACK_MOVE,
    /** The boss is moving */
    MOVE,
    /** The boss has been defeated */
    DEAD,
    /** The boss has been executed */
    EXECUTED,
  }

  /*
   * -----------------------------------
   * CONSTANTS
   * -----------------------------------
   */
  /** The distance the player must be from the boss before it begins attacking. */
  private static float AGRO_DISTANCE = 30f;

  /** The minimum distance the boss must move during a movement cycle. */
  private static float MIN_MOVE_DIST = 15f;

  /*
   * -------------------------------
   * STATE
   * -------------------------------
   */
  /** The model being controlled */
  private BossModel boss;

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

  /** The first ring in the ring stack (8 bullets) */
  private final AttackPattern ringStack1Attack;

  /** The second ring in the ring stack */
  private final AttackPattern ringStack2Attack;

  /** The third ring in the ring stack */
  private final AttackPattern ringStack3Attack;

  /** The dense ring attack  */
  private final AttackPattern denseRingAttack;

  /** The delayed rotate ring attack */
  private final AttackPattern delayRotateRingAttack;

  /** The speed ring attack */
  private final AttackPattern speedRingAttack;

  /** The rotating arc that initially points backwards */
  private final AttackPattern backRotateRingAttack;

  /** The homing ring */
  private final AttackPattern homingRingAttack;

  /** The homing bullets that speed up */
  private final AttackPattern homingSpeedAttack;

  /** The unbreakable ring around the boss */
  private final AttackPattern unbreakableRing;

  /**
   * Constructs a head boss controller
   *
   * @param boss head model being mutated
   * @param player player model being attacked
   * @param builder a builder to create bullet models
   * @param physicsEngine physics engine to add bullet attack to
   */
  public CrabBossController(
          BossModel boss,
          PlayerModel player,
          BulletModel.Builder builder,
          PhysicsEngine physicsEngine) {
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;

    this.goalPos = new Vector2();
    this.rand = new Random();
    this.bounds = new Rectangle(boss.getX() - 16, boss.getY() - 13, 32, 26);

    this.ringStack1Attack = new RingAttack(boss, 100, 9, 10f, false, builder, physicsEngine);
    this.ringStack2Attack = new RingAttack(boss, 100, 9, 11f, false, builder, physicsEngine);
    this.ringStack3Attack = new RingAttack(boss, 100, 9, 12f, false, builder, physicsEngine);
    this.denseRingAttack = new RingAttack(boss, 70, 30, 10f, false, builder, physicsEngine);
    this.speedRingAttack = new RingAttack(boss, 100, 13, 20f, false, builder, physicsEngine);

    this.delayRotateRingAttack = new DelayedRotateRingAttack(70, 15, 50, MathUtils.PI*2,
            -MathUtils.PI/3, boss, builder, physicsEngine);
    this.homingSpeedAttack = new DelayedTrackingSpeedArcAttack(70, 4, 2*MathUtils.PI, 0,
            50, boss, player, builder, physicsEngine);
    this.homingRingAttack = new DelayedTrackingArcAttack(100, 11, 2* MathUtils.PI, 0,
            50, boss, player, builder, physicsEngine);
    this.backRotateRingAttack = new DelayedRotateRingAttack(120, 10, 60, MathUtils.PI*2,
            MathUtils.PI, boss, builder, physicsEngine) ;
    this.unbreakableRing = new UnbreakableSpinningRing(7f, 9, 120, boss, builder, physicsEngine);
  }

  @Override
  public int getHealth() {
    return boss.getHealth();
  }

  @Override
  public int getMaxHealth() {
    return boss.getFullHealth();
  }

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
    ringStack1Attack.cleanup();
    ringStack2Attack.cleanup();
    ringStack3Attack.cleanup();
    speedRingAttack.cleanup();
    delayRotateRingAttack.cleanup();
    homingRingAttack.cleanup();
    homingSpeedAttack.cleanup();
    backRotateRingAttack.cleanup();
    unbreakableRing.cleanup();
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
      dispose();
      state = State.DEAD;
      System.out.println("isdead");
      if (boss.isFinishExecute()){
        state = State.EXECUTED;
        player.setFinishExecute(true);
        player.getBodyModel().stopExecuting();
        System.out.println("hahahaah");
      }
    } else if (boss.reachedHealthThreshold()) {
      state = State.THRESHOLD;
      timer = rand.nextInt(480, 600);
      boss.setVX(0);
      boss.setVY(0);
    }

    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE && boss.isInRoom()) {
          state = State.ATTACK_MOVE;
          findNewGoalPos();
        }
        break;
      case THRESHOLD:
        if (timer <= 0) {
          int r = rand.nextInt(0, 3);
          if (r == 0) {
            state = State.MOVE;
            findNewGoalPos();
          } else if (r == 1) {
            state = State.HOMING_SPEED_ATTACK;
            timer = rand.nextInt(360, 480);
          } else {
            state = State.ROTATE_RING_ATTACK;
            timer = rand.nextInt(240, 480);
          }
        }
        break;
      case HOMING_SPEED_ATTACK:
        if (timer <= 0) {
          int r = rand.nextInt(0, 3);
          if (r == 0) {
            state = State.MOVE;
            findNewGoalPos();
          } else if (r == 1) {
            state = State.ATTACK_MOVE;
            findNewGoalPos();
          } else {
            state = State.ROTATE_RING_ATTACK;
            timer = rand.nextInt(240, 480);
          }
        }
        break;
      case ROTATE_RING_ATTACK:
        if (timer <= 0) {
          int r = rand.nextInt(0, 3);
          if (r == 0) {
            state = State.MOVE;
            findNewGoalPos();
          } else if (r == 1) {
            state = State.ATTACK_MOVE;
            findNewGoalPos();
          } else {
            state = State.HOMING_SPEED_ATTACK;
            timer = rand.nextInt(240, 480);
          }
        }
        break;
      case ATTACK_MOVE, MOVE:
        if (goalPosReached()) {
          boss.setVX(0);
          boss.setVY(0);
          if (rand.nextBoolean()) {
            state = State.HOMING_SPEED_ATTACK;
          } else {
            state = State.ROTATE_RING_ATTACK;
          }
          timer = rand.nextInt(300, 600);
        }
        break;
      case DEAD:
        break;
      case EXECUTED:
        System.out.println("executed");
        break;
    }
  }

  /** Performs actions based on the controller state */
  private void act() {
    switch (state) {
      case IDLE, MOVE, EXECUTED:
        break;
      case THRESHOLD:
        ringStack1Attack.update(player.getX(), player.getY());
        ringStack2Attack.update(player.getX(), player.getY());
        ringStack3Attack.update(player.getX(), player.getY());
        denseRingAttack.update(player.getX(), player.getY());
        homingRingAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case ROTATE_RING_ATTACK:
        delayRotateRingAttack.update(player.getX(), player.getY());
        speedRingAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case HOMING_SPEED_ATTACK:
        homingSpeedAttack.update(player.getX(), player.getY());
        backRotateRingAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case ATTACK_MOVE:
        homingRingAttack.update(player.getX(), player.getY());
        break;
    }
    if (state != State.IDLE && state != State.DEAD) unbreakableRing.update(player.getX(), player.getY());
  }

  /** If the goal pos was reached */
  private boolean goalPosReached() {
    return boss.getPosition().sub(goalPos).dot(boss.getLinearVelocity()) > 0;
  }

  /** Helper function to generate new goal position & set boss velocity. * */
  private void findNewGoalPos() {
    goalPos.set(
            rand.nextFloat(bounds.getX(), bounds.getX() + bounds.getWidth()),
            rand.nextFloat(bounds.getY(), bounds.getY() + bounds.getHeight()));
    while (boss.getPosition().dst(goalPos) < MIN_MOVE_DIST) {
      goalPos.set(
              rand.nextFloat(bounds.getX(), bounds.getX() + bounds.getWidth()),
              rand.nextFloat(bounds.getY(), bounds.getY() + bounds.getHeight()));
    }
    boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-6));
  }
}
