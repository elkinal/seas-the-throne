package edu.cornell.jade.seasthethrone.bpedit;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.BulletPattern.BulletFamily;
import edu.cornell.jade.seasthethrone.bpedit.BulletPattern.Effect;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;

/**
 * A Temperary class to keep track of hard coded bullet patterns
 */

public class BulletPatternConstants {
  /** A Spiral bullet pattern */
  public static BulletPattern RING;

  static {
    class Delay implements Effect {
      public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
        int numBullets = bullets.size;
        for (int i = 0; i < numBullets; i++) {
          BulletFamily b = bullets.get(i).clone(familyPool);
          b.timestamp = b.timestamp + 120;
          b.addEffect(new Delay());
          bullets.add(b);
        }
      }
    }

    RING = new BulletPattern();
    Effect ring = (bullets, familyPool, basePool) -> {
      int numBullets = bullets.size;
      for (int k = 0; k < numBullets; k++) {
        BulletFamily orig = bullets.get(k);
        for (int i = 0; i < 10; i++) {
          float theta = i * 2f * MathUtils.PI / 10;
          BulletFamily clone = orig.clone(familyPool);
          clone.rotate(theta, 0, 0);
          bullets.add(clone);
        }
      }
    };
    BulletFamily f1 = new BulletFamily(2f, 0f, 1f, 0f, 0.5f, 1);
    f1.addEffect(new Delay());
    f1.addEffect(ring);
    RING.addFamily(f1);
  }
}
