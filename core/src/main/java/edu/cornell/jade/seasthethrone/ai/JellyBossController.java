package edu.cornell.jade.seasthethrone.ai;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.patterns.TrackingSpiralAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.JellyBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

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
  private static float AGRO_DISTANCE = 30f;

  /** The length of the arc of bullets */
  private static float ARC_LINE_LENGTH = 60f;

  /** The ticks in a period of the arc attack */
  private static int ARC_PERIOD = 60;

  /** The number of arc shots fired in a single period */
  private static int ARC_SHOTS = 6;

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

  /** The first attack */
  private final AttackPattern attack1;

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

    this.attack1 = new TrackingSpiralAttack(boss, 6, 24, builder, physicsEngine);
  }

  /**
   * Returns if the jelly this model controls is dead.
   *
   * @return if the jelly this controller controls is dead
   */
  public boolean isTerminated() {
    return boss.isTerminated();
  }

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
        }
        break;
      case ATTACK:
        break;
      case MOVE:
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
      case ATTACK:
        attackPattern.update(player.getX(), player.getY());
        break;
      case MOVE:
        break;
      case DEAD:
        break;
    }
  }
}
