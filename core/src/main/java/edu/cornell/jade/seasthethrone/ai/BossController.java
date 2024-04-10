package edu.cornell.jade.seasthethrone.ai;

import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
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
   * Returns if the boss this model controls is dead.
   *
   * @return if the boss this controller controls is dead
   */
  public boolean isDead();

  /** Cleans up this bosses attack patterns */
  public void dispose();
}
