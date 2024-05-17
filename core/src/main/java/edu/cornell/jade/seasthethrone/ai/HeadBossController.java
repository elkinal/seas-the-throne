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

/** A controller defining the behavior of a head boss. */
public class HeadBossController implements BossController {
  /** Enumeration of AI states. */
  private static enum State {
    /** The boss is stationary */
    IDLE,
    /** The boss is attacking with aimed random stream */
    RANDOM_ATTACK,
    /** The boss is attacking with spiral */
    SPIRAL_ATTACK,
    /** The boss is attacking and moving at the same time */
    ATTACK_MOVE,
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
  private static float MIN_MOVE_DIST = 13f;

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

  /** The unbreakable spiral attack */
  private final AttackPattern unbreakableSpiralAttack;

  /** The unbreakable ring attack  */
  private final AttackPattern unbreakableRingAttack;

  /** The ring attack  */
  private final AttackPattern ringAttack;

  /** The aimed random stream attack */
  private final AttackPattern aimedRandomAttack;

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
  public HeadBossController(
          BossModel boss,
          PlayerModel player,
          BulletModel.Builder builder,
          PhysicsEngine physicsEngine) {
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;

    this.goalPos = new Vector2();
    this.rand = new Random();
    this.bounds = new Rectangle(boss.getX() - 13, boss.getY() - 13, 26, 26);

    this.unbreakableSpiralAttack = new SpiralAttack(boss, 10, 16, true, builder, physicsEngine);
    this.unbreakableRingAttack = new RingAttack(boss, 100, 7, 6f, true,
            builder, physicsEngine);
    this.ringAttack = new RingAttack(boss, 100, 15, 12f, false,
            builder, physicsEngine);
    this.aimedRandomAttack = new AimedRandomStreamAttack(MathUtils.PI/5, 10, boss,
            player, builder, physicsEngine);
    this.unbreakableRing = new UnbreakableSpinningRing(5f,5, 240, boss, builder, physicsEngine);
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
    unbreakableRingAttack.cleanup();
    unbreakableSpiralAttack.cleanup();
    unbreakableRing.cleanup();
    aimedRandomAttack.cleanup();
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
      dispose();
      state = State.DEAD;
    } else if (boss.reachedHealthThreshold()) {
      state = State.ATTACK_MOVE;
      findNewGoalPos();
    }

    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE && boss.isInRoom()) {
          state = State.RANDOM_ATTACK;
          timer = rand.nextInt(360, 480);
        }
        break;
      case RANDOM_ATTACK:
        if (timer <= 0) {
          state = State.SPIRAL_ATTACK;
          timer = rand.nextInt(200, 300);
        }
        break;
      case SPIRAL_ATTACK:
        if (timer <= 0) {
          state = State.RANDOM_ATTACK;
          timer = rand.nextInt(360, 480);
        }
        break;
      case ATTACK_MOVE:
        if (goalPosReached()) {
          boss.setVX(0);
          boss.setVY(0);
          if (rand.nextBoolean()) {
            state = State.SPIRAL_ATTACK;
            timer = rand.nextInt(200, 300);
          } else {
            state = State.RANDOM_ATTACK;
            timer = rand.nextInt(360, 480);
          }
        }
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
      case RANDOM_ATTACK:
        aimedRandomAttack.update(player.getX(), player.getY());
        unbreakableRingAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case SPIRAL_ATTACK:
        unbreakableSpiralAttack.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case ATTACK_MOVE:
        unbreakableRingAttack.update(player.getX(), player.getY());
        ringAttack.update(player.getX(), player.getY());
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
    boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-3));
  }
}

