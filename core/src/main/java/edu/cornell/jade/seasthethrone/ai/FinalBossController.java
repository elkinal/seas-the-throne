package edu.cornell.jade.seasthethrone.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.patterns.AimedArcAttack;
import edu.cornell.jade.seasthethrone.bpedit.patterns.OscillatingRingAttack;
import edu.cornell.jade.seasthethrone.bpedit.patterns.RingAttack;
import edu.cornell.jade.seasthethrone.bpedit.patterns.UnbreakableSpinningRing;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

import java.util.Random;

public class FinalBossController implements BossController{
  /** Enumeration of AI states. */
  private static enum State {
    /** The boss is stationary */
    IDLE,
    /** The boss is attacking with aimed arcs */
    ARC_ATTACK,
    /** The boss is attacking with oscillating rings */
    OSC_ATTACK,
    /** The boss is attacking and moving at the same time */
    ATTACK_MOVE,
    /** The boss is moving towards where the player was */
    CHASE_PLAYER,
    /** The boss is resetting its position */
    RESET,
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

  /** The oscillating ring attack */
  private final AttackPattern oscRingAttack;

  /** The ring attack (acting as fixed angle streams) */
  private final AttackPattern ringAttack;

  /** The aimed arc attack */
  private final AttackPattern aimedArcAttack;

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
  public FinalBossController(
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

    this.oscRingAttack = new OscillatingRingAttack(boss, player, builder, physicsEngine);
    this.ringAttack = new RingAttack(boss, 20, 5, false, builder, physicsEngine);
    this.aimedArcAttack = new AimedArcAttack(60, boss, player, builder, physicsEngine);
    this.unbreakableRing = new UnbreakableSpinningRing(17, 240, boss, builder, physicsEngine);
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
    oscRingAttack.cleanup();
    aimedArcAttack.cleanup();
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
      state = State.CHASE_PLAYER;
      goalPos.set(player.getX(), player.getY());
      boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-15));
    }

    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE) {
          state = State.ARC_ATTACK;
          timer = rand.nextInt(240, 480);
        }
        break;
      case ARC_ATTACK:
        if (timer <= 0) {
          if (rand.nextBoolean()) {
            state = State.ATTACK_MOVE;
            findNewGoalPos();
          } else {
            state = State.OSC_ATTACK;
            timer = rand.nextInt(240, 480);
          }
        }
        break;
      case OSC_ATTACK:
        if (timer <= 0) {
          if (rand.nextBoolean()) {
            state = State.ATTACK_MOVE;
            findNewGoalPos();
          } else {
            state = State.ARC_ATTACK;
            timer = rand.nextInt(240, 480);
          }
        }
        break;
      case ATTACK_MOVE:
        if (goalPosReached()) {
          boss.setVX(0);
          boss.setVY(0);
          if (rand.nextBoolean()) {
            state = State.OSC_ATTACK;
          } else {
            state = State.ARC_ATTACK;
          }
          timer = rand.nextInt(240, 480);
        }
        break;
      case CHASE_PLAYER:
        if (goalPosReached()) {
          state = State.RESET;
          goalPos.set(
              bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
          boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-5));
        }
        break;
      case RESET:
        if (goalPosReached()) {
          boss.setVX(0);
          boss.setVY(0);
          state = State.ARC_ATTACK;
          timer = rand.nextInt(240, 480);
        }
        break;
      case DEAD:
        oscRingAttack.cleanup();
        ringAttack.cleanup();
        aimedArcAttack.cleanup();
        unbreakableRing.cleanup();
        break;
    }
  }

  /** Performs actions based on the controller state */
  private void act() {
    switch (state) {
      case IDLE:
        break;
      case ARC_ATTACK:
        aimedArcAttack.update(player.getX(), player.getY());
        ringAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case OSC_ATTACK:
        oscRingAttack.update(player.getX(), player.getY());
        ringAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case ATTACK_MOVE:
        aimedArcAttack.update(player.getX(), player.getY());
        break;
      case CHASE_PLAYER:
        break;
      case RESET:
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
