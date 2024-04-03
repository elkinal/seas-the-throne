package edu.cornell.jade.seasthethrone;

import edu.cornell.jade.seasthethrone.gamemodel.boss.CrabBossModel;

/**
 * A controller defining the bahavior of a crab boss.
 */
public class CrabBossController {
  /** The model being controlled */
  private CrabBossModel boss;

  /** Enumeration of AI states. */
  private static enum State {
    /** The boss is stationary */
    IDLE,
    /** The boss is moving towards its target */
    MOVE,
    /** The boss has been defeated */
    DEAD,
  }

  /** The boss's current state */
  private State state;

  /**
   * Constructs a crab boss controller
   *
   * @param boss crab model being mutated
   */
  public CrabBossController(CrabBossModel boss) {
    this.boss = boss;
  }

  /**
   * Returns if the crab this model controls is dead.
   *
   * @return if the crab this controller controls is dead
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

  }
}
