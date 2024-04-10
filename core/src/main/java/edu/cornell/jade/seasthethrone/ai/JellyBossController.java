package edu.cornell.jade.seasthethrone.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.patterns.RingAttack;
import edu.cornell.jade.seasthethrone.bpedit.patterns.TrackingSpiralAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.JellyBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

import java.util.Random;

/**
 * A controller defining the behavior of a jelly boss.
 */
public class JellyBossController implements BossController {
  /** Enumeration of AI states. */
  private static enum State {
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
  private static float AGRO_DISTANCE = 35f;

  /** The delay length between successive bullets in the spiral attack */
  private static int SPIRAL_DELAY = 6;

  /** The number of bullets in a circular spiral shot */
  private static int SPIRAL_SHOTS = 22;

  /** The delay length between successive bullets in the ring attack */
  private static int RING_DELAY = 100;

  /** The number of bullets in a circular ring attack */
  private static int RING_SHOTS = 26;

  /*
   * -------------------------------
   * STATE
   * -------------------------------
   */
  /** The model being controlled */
  private JellyBossModel boss;

  /** The player model being attacked */
  private PlayerModel player;

  /** The current attack of the boss. */
  private AttackPattern attackPattern;

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

  /** The first attack */
  private final AttackPattern attack1;

  /** The second attack */
  private final AttackPattern attack2;

  /**
   * Constructs a crab boss controller
   *
   * @param boss          crab model being mutated
   * @param player        player model being attacked
   * @param builder       a builder to create bullet models
   * @param physicsEngine physics engine to add bullet attack to
   */
  public JellyBossController(JellyBossModel boss, PlayerModel player, BulletModel.Builder builder,
                             PhysicsEngine physicsEngine) {
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;

    this.attack1 = new RingAttack(boss, RING_DELAY, RING_SHOTS, builder, physicsEngine);
    this.attack2 = new TrackingSpiralAttack(boss, SPIRAL_DELAY, SPIRAL_SHOTS, builder, physicsEngine);

    this.goalPos = new Vector2();
    this.rand = new Random();
    this.bounds = new Rectangle(boss.getX()-10, boss.getY()-15, 20, 30);
  }

  /** Returns the boss of this controller */
  public BossModel getBoss() {return boss;}

  /** Loads in the boss's previous state */
  public void transferState(int storedHp) {
    this.boss.setHealth(storedHp);
  }

  /**
   * Returns if the jelly this model controls is dead.
   *
   * @return if the jelly this controller controls is dead
   */
  public boolean isTerminated() {
    return boss.isDead();
  }

  @Override
  public void dispose() {attackPattern.cleanup();}

  /** Marks the boss for removal from the physics engine. */
  public void remove() {
    boss.markRemoved(true);
  }

  /**
   * Called every tick. Updates the state of the model based on the controller
   * state.
   *
   * @param delta time since update was last called
   */
  public void update(float delta) {
    nextState();
    act();
  }

  /** Progresses to the next state of the controller. */
  private void nextState() {
    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE) {
          state = State.ATTACK;
          attackPattern = attack1;
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
          attackPattern = attack2;
        }
        break;
      case ATTACK_MOVE:
        if (boss.getPosition().sub(goalPos).dot(boss.getLinearVelocity()) > 0) {
          boss.setVX(0);
          boss.setVY(0);
          state = State.ATTACK;
          attackPattern = attack1;
          timer = rand.nextInt(120, 150);
        } else if (boss.reachedHealthThreshold()) {
          state = State.MOVE;
          findNewGoalPos();
        }
        break;
      case MOVE:
        if (boss.getPosition().sub(goalPos).dot(boss.getLinearVelocity()) > 0){
          boss.setVX(0);
          boss.setVY(0);
          if (rand.nextBoolean()) {
            state = State.ATTACK;
            attackPattern = attack1;
            timer = rand.nextInt(120, 150);
          } else {
            state = State.ATTACK_MOVE;
            findNewGoalPos();
            attackPattern = attack2;
          }
        }
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
        attackPattern.update(player.getX(), player.getY());
        timer -= 1;
        break;
      case ATTACK_MOVE:
        attackPattern.update(player.getX(), player.getY());
        break;
      case MOVE:
        break;
      case DEAD:
        break;
    }
  }

  /** Helper function to generate new goal position & set boss velocity */
  private void findNewGoalPos(){
    goalPos.set(rand.nextFloat(bounds.getX(), bounds.getX()+bounds.getWidth()),
            rand.nextFloat(bounds.getY(), bounds.getY()+bounds.getHeight()));
    while (boss.getPosition().dst(goalPos) < 8) {
      goalPos.set(rand.nextFloat(bounds.getX(), bounds.getX()+bounds.getWidth()),
              rand.nextFloat(bounds.getY(), bounds.getY()+bounds.getHeight()));
    }
    boss.setLinearVelocity(boss.getPosition().sub(goalPos).nor().scl(-5));
  }
}
