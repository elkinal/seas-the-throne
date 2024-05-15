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

public class SwordfishBossController implements BossController {
  /** Enumeration of AI states. */
  private static enum State {
    /** The boss is stationary */
    IDLE,
    /** The boss reached a threshold */
    THRESHOLD,
    /** The boss is attacking with aimed arcs */
    ARC_ATTACK,
    /** The boss is attacking with rings */
    RING_ATTACK,
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

  /** The minimum distance the boss must move during a movement cycle. */
  private static float MIN_MOVE_DIST = 20f;

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

  /** The arc attack */
  private final AttackPattern arcAttack;

  /** The ring attack  */
  private final AttackPattern ringAttack;


  /** The fast ring attack  */
  private final AttackPattern speedRingAttack;

  /**
   * Constructs a swordfish boss controller
   *
   * @param boss head model being mutated
   * @param player player model being attacked
   * @param builder a builder to create bullet models
   * @param physicsEngine physics engine to add bullet attack to
   */
  public SwordfishBossController(
          BossModel boss,
          PlayerModel player,
          BulletModel.Builder builder,
          PhysicsEngine physicsEngine) {
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;

    this.goalPos = new Vector2();
    this.rand = new Random();
    this.bounds = new Rectangle(boss.getX() - 15, boss.getY() - 15, 30, 30);

    this.arcAttack = new AimedArcAttack(100, boss, player, builder, physicsEngine);
    this.ringAttack = new RingAttack(boss, 100, 10, 12f, false,
            builder, physicsEngine);
    this.speedRingAttack = new RingAttack(boss, 100, 11, 18f, false,
            builder, physicsEngine);
  }

  @Override
  public int getHealth() {
    return boss.getHealth();
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
    arcAttack.cleanup();
    ringAttack.cleanup();
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
      state = State.THRESHOLD;
      boss.setVX(0);
      boss.setVY(0);
      timer = rand.nextInt(240, 360);
    }

    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE && boss.isInRoom()) {
          state = State.ARC_ATTACK;
          findNewGoalPos();
        }
        break;
      case ARC_ATTACK:
        if (timer <= 0) {
          state = State.RING_ATTACK;
          findNewGoalPos();
        }
        break;
      case THRESHOLD:
        if (timer <= 0) {
          if (rand.nextBoolean()) {
            state = State.ARC_ATTACK;
          } else {
            state = State.RING_ATTACK;
          }
          findNewGoalPos();
        }
        break;
      case RING_ATTACK:
        if (goalPosReached()) {
          boss.setVX(0);
          boss.setVY(0);
          state = State.ARC_ATTACK;
          findNewGoalPos();
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
      case THRESHOLD:
        speedRingAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case ARC_ATTACK:
        arcAttack.update(player.getX(), player.getY());
        break;
      case RING_ATTACK:
        ringAttack.update(player.getX(), player.getY());
        break;
      case DEAD:
        break;
    }
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
    boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-5));
  }
}

