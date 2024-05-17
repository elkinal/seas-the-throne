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
    /** Phase switch (animation time) */
    PHASE_SWITCH,

    /** FIRST PHASE STATES: */
    F_START,
    F_AIMED_ARC,
    F_AIMED_SINGLE,

    /** SECOND PHASE STATES: */
    CHASE_PLAYER,
    RESET,
    S_DELAY_ROTATE_RING,
    S_DELAY_ROTATE_SPIRAL,
    S_DELAY_SPEED_RING,
    S_DELAY_SLOW_RING,
    S_ALTERNATE_RING,
    S_RING_STACK,
    /** The boss is dead */
    DEAD,
  }

  /*
   * -----------------------------------
   * CONSTANTS
   * -----------------------------------
   */
  /** The distance the player must be from the boss before it begins attacking. */
  private static float AGRO_DISTANCE = 20f;

  /** The minimum distance the boss must move during a movement cycle. */
  private static float MIN_MOVE_DIST = 25f;

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

  /** If the boss has reached the first threshold yet (to check phase switching) */
  private boolean firstThreshold;

  /*
   * -------------------------------
   * FIRST PHASE ATTACKS
   * -------------------------------
   */
  private final AttackPattern aimedArcAttack;
  private final AttackPattern fastRingAttack;
  private final AttackPattern slowUnbreakableRingAttack;
  private final AttackPattern delayRotateRingAttack;
  private final AttackPattern aimedSingleAttack;


  /*
   * -------------------------------
   * SECOND PHASE ATTACKS
   * -------------------------------
   */

  /** The unbreakable spinning ring  */
  private final AttackPattern unbreakableRing;
  private final AttackPattern unbreakableAimedSingleAttack;
  private final AttackPattern oscRingAttack;
  private final AttackPattern unbreakableDelayRotateSpiralAttack;
  private final AttackPattern unbreakableHomingSpeedAttack;

  private final AttackPattern unbreakableDelaySpeedRingAttack;
  private final AttackPattern unbreakableDelaySlowRingAttack;
  private final AttackPattern aimedRandomAttack;
  private final AttackPattern alternatingRingAttack;
  private final AttackPattern ringStack1Attack;
  private final AttackPattern ringStack2Attack;
  private final AttackPattern ringStack3Attack;
  private final AttackPattern denseRingAttack;
  private final AttackPattern homingSpeedAttack;
  private final AttackPattern homingRingAttack;



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

    this.firstThreshold = true;
    this.goalPos = new Vector2();
    this.rand = new Random();
    this.bounds = new Rectangle(boss.getX() - 20, boss.getY() - 25, 40, 35);

    this.aimedArcAttack = new AimedArcAttack(40, boss, player, builder, physicsEngine);
    this.fastRingAttack = new RingAttack(boss, 100, 13, 18f, false, builder, physicsEngine);
    this.slowUnbreakableRingAttack = new RingAttack(boss, 120, 15, 8f,
            true, builder, physicsEngine);
    this.delayRotateRingAttack = new DelayedRotateRingAttack(70, 17, 110, MathUtils.PI*2,
            -MathUtils.PI/3, boss, builder, physicsEngine);
    this.aimedSingleAttack = new SingleBulletAttack(15, false, boss, builder, physicsEngine);

    this.homingSpeedAttack = new DelayedTrackingSpeedArcAttack(70, 6, 2*MathUtils.PI, 0,
            50, boss, player, builder, physicsEngine);
    this.unbreakableHomingSpeedAttack = new UnbreakableDelayedTrackingSpeedArcAttack(70, 3, 2*MathUtils.PI, 0,
            50, boss, player, builder, physicsEngine);
    this.homingRingAttack = new DelayedTrackingArcAttack(100, 11, 2* MathUtils.PI, 0,
            50, boss, player, builder, physicsEngine);

    this.oscRingAttack = new OscillatingRingAttack(boss, player, builder, physicsEngine);
    this.unbreakableDelayRotateSpiralAttack = new DelayedRotateSpiralAttack(boss, 7, 25, 100,
            3*MathUtils.PI/4, true, builder, physicsEngine);
    this.unbreakableDelaySpeedRingAttack = new DelayedSpeedRingAttack(120, 13, 70,
      2*MathUtils.PI, 6f, 24f, true, boss, builder, physicsEngine);
    this.unbreakableDelaySlowRingAttack = new DelayedSpeedRingAttack(120, 13, 60,
            2*MathUtils.PI, 18f, 6f, true, boss, builder, physicsEngine);
    this.alternatingRingAttack = new AlternatingRingAttack(boss, 50, 15, 9f,
            false, builder, physicsEngine);
    this.aimedRandomAttack = new AimedRandomStreamAttack(MathUtils.PI/5, 10, boss,
            player, builder, physicsEngine);

    this.ringStack1Attack = new RingAttack(boss, 100, 11, 10f, false, builder, physicsEngine);
    this.ringStack2Attack = new RingAttack(boss, 100, 11, 11f, false, builder, physicsEngine);
    this.ringStack3Attack = new RingAttack(boss, 100, 11, 12f, false, builder, physicsEngine);
    this.denseRingAttack = new RingAttack(boss, 70, 35, 10f, false, builder, physicsEngine);
    this.unbreakableAimedSingleAttack = new SingleBulletAttack(25, true, boss,
            builder, physicsEngine);

    this.unbreakableRing = new UnbreakableSpinningRing(7f,13, 150, boss, builder, physicsEngine);

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
    aimedArcAttack.cleanup();
    fastRingAttack.cleanup();
    slowUnbreakableRingAttack.cleanup();
    delayRotateRingAttack.cleanup();
    aimedSingleAttack.cleanup();
    homingSpeedAttack.cleanup();
    unbreakableHomingSpeedAttack.cleanup();
    homingRingAttack.cleanup();
    oscRingAttack.cleanup();
    unbreakableDelayRotateSpiralAttack.cleanup();
    unbreakableDelaySpeedRingAttack.cleanup();
    unbreakableDelaySlowRingAttack.cleanup();
    alternatingRingAttack.cleanup();
    aimedRandomAttack.cleanup();
    ringStack1Attack.cleanup();
    ringStack2Attack.cleanup();
    ringStack3Attack.cleanup();
    denseRingAttack.cleanup();
    unbreakableAimedSingleAttack.cleanup();
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
    } else if (boss.reachedHealthThreshold()) {
      if (firstThreshold) {
        firstThreshold = false;
        state = State.PHASE_SWITCH;
        timer = 150;
        boss.launchPhaseTwo();
      } else {
        state = State.CHASE_PLAYER;
        goalPos.set(player.getX(), player.getY());
        boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-16));
      }
    }

    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE && boss.isInRoom()) {
          state = State.F_START;
          timer = 300;
//          state = State.PHASE_SWITCH;
//          timer = 150;
//          boss.launchPhaseTwo();
        }
        break;
      case F_START:
        if (timer <= 0) {
          state = State.F_AIMED_ARC;
          timer = rand.nextInt(150, 300);
        }
        break;
      case F_AIMED_ARC:
        if (timer <= 0) {
          state = State.F_AIMED_SINGLE;
          timer = rand.nextInt(450, 650);
        }
        break;
      case F_AIMED_SINGLE:
        if (timer <= 0) {
          state = State.F_AIMED_ARC;
          timer = rand.nextInt(400, 800);
        }
        break;
      case PHASE_SWITCH:
        if (timer <= 0) {
          state = State.S_DELAY_ROTATE_RING;
          timer = rand.nextInt(300, 500);
        }
        break;
      case S_RING_STACK, S_ALTERNATE_RING, S_DELAY_ROTATE_RING,
              S_DELAY_ROTATE_SPIRAL, S_DELAY_SLOW_RING, S_DELAY_SPEED_RING:
        if (timer <= 0) {
          state = chooseRandomState();
          if (rand.nextInt(0, 3) <= 1) findNewGoalPos(6);
          timer = rand.nextInt(350, 800);
        }
        break;
      case CHASE_PLAYER:
        if (goalPosReached()) {
          state = State.RESET;
          goalPos.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
          boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-7));
        }
        break;
      case RESET:
        if (goalPosReached()) {
          boss.setVX(0);
          boss.setVY(0);
          state = chooseRandomState();
          timer = rand.nextInt(350, 800);
        }
        break;
      case DEAD:
        break;
    }
    if (goalPosReached()) {
      boss.setVX(0);
      boss.setVY(0);
    }
  }

  /** Performs actions based on the controller state */
  private void act() {
    switch (state) {
      case PHASE_SWITCH:
        timer -=1;
        break;
      case F_START:
        aimedArcAttack.update(player.getX(), player.getY());
        if (timer < 100) slowUnbreakableRingAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case F_AIMED_ARC:
        aimedArcAttack.update(player.getX(), player.getY());
        fastRingAttack.update(player.getX(), player.getY());
        slowUnbreakableRingAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case F_AIMED_SINGLE:
        delayRotateRingAttack.update(player.getX(), player.getY());
        aimedSingleAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case S_DELAY_ROTATE_SPIRAL:
        unbreakableDelayRotateSpiralAttack.update(player.getX(), player.getY());
        if (timer < 300) homingSpeedAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case S_DELAY_SPEED_RING:
        if (timer < 200) unbreakableDelaySpeedRingAttack.update(player.getX(), player.getY());
        oscRingAttack.update(player.getX(), player.getY());
        aimedSingleAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case S_DELAY_SLOW_RING:
        unbreakableDelaySlowRingAttack.update(player.getX(), player.getY());
        homingRingAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case S_RING_STACK:
        ringStack1Attack.update(player.getX(), player.getY());
        ringStack2Attack.update(player.getX(), player.getY());
        ringStack3Attack.update(player.getX(), player.getY());
        if (timer < 400) denseRingAttack.update(player.getX(), player.getY());
        unbreakableAimedSingleAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case S_DELAY_ROTATE_RING:
        delayRotateRingAttack.update(player.getX(), player.getY());
        aimedRandomAttack.update(player.getX(), player.getY());
        if (timer < 200) fastRingAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      case S_ALTERNATE_RING:
        alternatingRingAttack.update(player.getX(), player.getY());
        if (timer < 300) unbreakableHomingSpeedAttack.update(player.getX(), player.getY());
        timer -=1;
        break;
      default:
        break;
    }
    if (state != State.IDLE && state != State.DEAD && state != State.F_START && state
            != State.F_AIMED_ARC && state != State.F_AIMED_SINGLE)
      unbreakableRing.update(player.getX(), player.getY());
  }

  /** randomly chooses the next state (for phase two boss) */
  private State chooseRandomState() {
    return switch (rand.nextInt(0, 6)) {
      case 0 -> State.S_DELAY_ROTATE_RING;
      case 1 -> State.S_DELAY_ROTATE_SPIRAL;
      case 2 -> State.S_DELAY_SPEED_RING;
      case 3 -> State.S_DELAY_SLOW_RING;
      case 4 -> State.S_ALTERNATE_RING;
      default -> State.S_RING_STACK;
    };
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
