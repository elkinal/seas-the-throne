package edu.cornell.jade.seasthethrone.bpedit;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;

import edu.cornell.jade.seasthethrone.bpedit.Spawner.BulletFamily;

public class BulletFamilyPool extends Pool<BulletFamily> {

  /**
   * Constructs a bullet family pool
   * 
   * @param initialCapacity initial capacity of pool
   * @param max             max size of pool
   */
  public BulletFamilyPool(int initialCapacity, int max) {
    super(initialCapacity, max);
  }

  @Override
  protected BulletFamily newObject() {
    BulletFamily out = new BulletFamily();
    out.effect = new Queue<>();
    out.delayedActions = new Array<>();
    return out;
  }
}
