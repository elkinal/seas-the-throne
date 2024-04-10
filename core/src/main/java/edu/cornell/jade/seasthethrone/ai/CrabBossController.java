package edu.cornell.jade.seasthethrone.ai;

import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.patterns.ArcsAcrossTheTopAttack;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.CrabBossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/** A controller defining the bahavior of a crab boss. */
public class CrabBossController implements BossController {
  /** Enumeration of AI states. */
  private static enum State {
    /** The boss is stationary */
    IDLE,
    /** The boss is attacking */
    ATTACK,
    /** The boss is moving towards a target */
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

  /** The x offset from the boss arc across the tops left side starts on */
  private static float X_OFFSET = -30f;

  /** The y offset from the boss arc across the tops left side starts on */
  private static float Y_OFFSET = 20f;

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
  private CrabBossModel boss;

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
   * @param boss crab model being mutated
   * @param player player model being attacked
   * @param builder a builder to create bullet models
   * @param physicsEngine physics engine to add bullet attack to
   */
  public CrabBossController(
      CrabBossModel boss,
      PlayerModel player,
      BulletModel.Builder builder,
      PhysicsEngine physicsEngine) {
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;

    System.out.println(boss.getX() + " " + boss.getY());
    this.attack1 =
        new ArcsAcrossTheTopAttack(
            boss.getX() + X_OFFSET,
            boss.getY() + Y_OFFSET,
            ARC_LINE_LENGTH,
            ARC_PERIOD,
            ARC_SHOTS,
            builder,
            physicsEngine);
  }

  /**
   * Returns if the crab this model controls is dead.
   *
   * @return if the crab this controller controls is dead
   */
  @Override
  public boolean isDead() {
    return boss.isDead();
  }

  /** Marks the boss for removal from the physics engine. */
  @Override
  public void remove() {
    boss.markRemoved(true);
  }

  /** Cleans up this boss's attack pattern */
  public void dispose() {
    attackPattern.cleanup();
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
