package edu.cornell.jade.seasthethrone.updateengine;

/**
 * An object whose state can be updated discretly over time. An <code>Updatable</code> can be added
 * to an {@link UpdateEngine} which will call <code>update</code> when requested.
 */
public interface Updatable {

  /**
   * Updates method state.
   *
   * @param delta the simulated time eclipsed since update was last called
   */
  public void update(float delta);
}
