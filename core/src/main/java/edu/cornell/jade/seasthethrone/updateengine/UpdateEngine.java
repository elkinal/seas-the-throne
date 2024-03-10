package edu.cornell.jade.seasthethrone.updateengine;

import com.badlogic.gdx.utils.ObjectSet;

/** An collection of {@link Updatable}s which can be updated all at once. */
public class UpdateEngine {
  /** All objects to be updated when <code>update</code> is called */
  private ObjectSet<Updatable> objs;

  /** {@link UpdateEngine} constructor. */
  public UpdateEngine() {
    objs = new ObjectSet<>();
  }

  /**
   * Updates the state of all {@link Updatable}s added to the {@link UpdateEngine}. The order of
   * updates is undefined.
   *
   * @param delta the simulated time eclipsed since update was last called
   */
  public void update(float delta) {
    for (Updatable u : objs) {
      u.update(delta);
    }
  }

  /**
   * Adds <code>obj</code> to the {@link UpdateEngine}. If <code>update</code> is subsequently
   * called, <code>obj</code>'s state will be updated.
   *
   * <p>Adding a duplicate object does nothing.
   *
   * @param obj object to be added
   */
  public void add(Updatable obj) {
    objs.add(obj);
  }

  /**
   * Removes <code>obj</code> from the {@link UpdateEngine}. If <code>update</code> is subsequently
   * called, <code>obj</code>'s state will not be updated.
   *
   * <p>Removing an object which was never added does nothing.
   *
   * @param obj object to be removed
   */
  public void remove(Updatable obj) {
    objs.remove(obj);
  }
}
