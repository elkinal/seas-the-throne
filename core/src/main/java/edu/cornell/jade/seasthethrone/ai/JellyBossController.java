package edu.cornell.jade.seasthethrone.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.JellyBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

import java.util.Random;

/** A controller defining the behavior of a jelly boss. */
abstract class JellyBossController implements BossController {
  /** Enumeration of AI states. */
  static enum State {
    /** The boss is stationary */
    IDLE,
    /** The boss is attacking */
    ATTACK,
    /** The boss is attacking and moving at the same time */
    ATTACK_MOVE,
    /** The boss is moving (and not attacking) */
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
  protected static float AGRO_DISTANCE = 25f;

  /** The minimum distance the boss must move during a movement cycle. */
  private static float MIN_MOVE_DIST = 8f;

  /*
   * -------------------------------
   * STATE
   * -------------------------------
   */
  /** The model being controlled */
  protected JellyBossModel boss;

  /** The player model being attacked */
  protected PlayerModel player;

  /** The boss's current state */
  protected State state;

  /** The timer for state switching */
  private int timer;

  /** The current goal position of the boss */
  private Vector2 goalPos;

  /** Random number generator */
  private Random rand;

  /** The bounds of which the boss can move */
  private Rectangle bounds;

  /** The first attack */
  private final AttackPattern attack;

  /**
   * Constructs a crab boss controller
   *
   * @param boss crab model being mutated
   * @param player player model being attacked
   * @param builder a builder to create bullet models
   * @param physicsEngine physics engine to add bullet attack to
   */
  public JellyBossController(
      JellyBossModel boss,
      PlayerModel player,
      AttackPattern attack1,
      BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;

    this.attack = attack1;

    this.goalPos = new Vector2();
    this.rand = new Random();
    this.bounds = new Rectangle(boss.getX() - 10, boss.getY() - 15, 20, 30);
  }

  /** Returns the boss of this controller */
  public BossModel getBoss() {
    return boss;
  }

  /** Loads in the boss's previous state */
  public void transferState(int storedHp) {
    this.boss.setHealth(storedHp);
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
    return false;
  }

  @Override
  public void dispose() {
    attack.cleanup();
  }

  /** Marks the boss for removal from the physics engine. */
  public void remove() {
    boss.markRemoved(true);
  }

  /**
   * Called every tick. Updates the state of the model based on the controller state.
   *
   * @param delta time since update was last called
   */
  public void update(float delta) {
    nextState();
    act();
  }

  /** Progresses to the next state of the controller. */
  protected void nextState() {
    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE) {
          state = State.ATTACK;
          timer = rand.nextInt(240, 480);
        }
        break;
      case ATTACK:
        if (boss.reachedHealthThreshold()) {
          state = State.MOVE;
          findNewGoalPos();
        } else if (timer <= 0) {
          state = State.ATTACK_MOVE;
          findNewGoalPos();
        }
        break;
      case ATTACK_MOVE:
        if (boss.getPosition().sub(goalPos).dot(boss.getLinearVelocity()) > 0) {
          boss.setVX(0);
          boss.setVY(0);
          state = State.ATTACK;
          timer = rand.nextInt(120, 150);
        } else if (boss.reachedHealthThreshold()) {
          state = State.MOVE;
          findNewGoalPos();
        }
        break;
      case MOVE:
        if (boss.getPosition().sub(goalPos).dot(boss.getLinearVelocity()) > 0) {
          boss.setVX(0);
          boss.setVY(0);
          if (rand.nextBoolean()) {
            state = State.ATTACK;
            timer = rand.nextInt(120, 150);
          } else {
            state = State.ATTACK_MOVE;
            findNewGoalPos();
          }
        }
      case DEAD:
        break;
    }
  }

  /** Performs actions based on the controller state */
  protected void act() {
    switch (state) {
      case IDLE:
        break;
      case ATTACK:
        attack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case ATTACK_MOVE:
        attack.update(player.getX(), player.getY());
        break;
      case MOVE:
        break;
      case DEAD:
        break;
    }
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
  }
}
