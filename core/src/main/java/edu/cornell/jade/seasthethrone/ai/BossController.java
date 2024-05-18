package edu.cornell.jade.seasthethrone.ai;

import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;

public interface BossController {
  /**
   * Called every tick. Updates the state of the model based on the controller state.
   *
   * @param delta time since update was last called
   */
  public void update(float delta);

  /** Marks the boss for removal from the physics engine. */
  public void remove();

  /**
   * Returns if the boss this model controller is dead.
   *
   * @return if the boss controller is dead
   */
  public boolean isDead();
  /**
   * Returns if the boss this model controller is dead and executed.
   *
   * @return if the boss controller is dead and executed.
   */
  default public boolean isTerminated(){
    return isDead();
  }

  /**
   * Returns if an enemy is a boss or a mob. Bosses are opponents which are combinations of all
   * previous bullet patterns, such as the head or the crab.
   *
   * @return true if an enemy is a boss
   */
  public boolean isBoss();

  /** Cleans up this bosses attack patterns */
  public void dispose();

  /** Returns the boss of this controller */
  public BossModel getBoss();

  /** Gets the health of the boss */
  public int getHealth();

  /** Gets the max health of the boss */
  public int getMaxHealth();

  /** Loads in the boss's previous state */
  public void transferState(int storedHp);
}
