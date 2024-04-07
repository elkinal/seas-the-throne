package edu.cornell.jade.seasthethrone.bpedit.patterns;

import edu.cornell.jade.seasthethrone.bpedit.Spawner.Effect;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.BulletFamily;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import com.badlogic.gdx.utils.*;

/** Creates an arc of bullets. */
class Arc implements Effect {
  float offset;
  float centralAngle;
  int dups;

  /**
   * Constructs an arc
   *
   * @param offset       offset from the initial rotation to start the arc in
   *                     radians
   * @param centralAngle size of the arc in radians
   * @param dups         number of bullets in the arc
   *
   */
  public Arc(float offset, float centralAngle, int dups) {
    this.offset = offset;
    this.centralAngle = centralAngle;
    this.dups = dups;
  }

  @Override
  public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
    int numBullets = bullets.size;
    for (int i = 0; i < numBullets; i++) {
      BulletFamily orig = bullets.get(i);
      for (int j = 0; j < dups; j++) {
        BulletFamily b = orig.clone(familyPool);
        float theta = j * centralAngle / (dups - 1) + offset;
        b.rotate(theta, 0, 0);
        bullets.add(b);
      }
    }
  }
}

/**
 * Causes a bullet to duplicate itself after a given number of ticks
 */
class Periodic implements Effect {
  int ticks;

  /**
   * Constructs a <code>Periodic</code> {@link Effect}.
   *
   * @param ticks number of ticks between the origin bullet and the spawn of the
   *              new bullet
   */
  public Periodic(int ticks) {
    this.ticks = ticks;
  }

  @Override
  public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
    int numBullets = bullets.size;
    for (int i = 0; i < numBullets; i++) {
      BulletFamily orig = bullets.get(i);
      BulletFamily b = orig.clone(familyPool);
      b.prependEffect(new Periodic(ticks));
      b.setTimestamp(b.getTimestamp() + ticks);
      bullets.add(b);
    }
  }
}
