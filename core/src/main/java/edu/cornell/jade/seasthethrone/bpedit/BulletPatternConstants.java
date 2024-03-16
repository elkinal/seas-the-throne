package edu.cornell.jade.seasthethrone.bpedit;

import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.BulletPattern.BulletFamily;
import edu.cornell.jade.seasthethrone.bpedit.BulletPattern.Effect;

/**
 * A Temperary class to keep track of hard coded bullet patterns
 */

public class BulletPatternConstants {
  /** A Spiral bullet pattern */
  static BulletPattern RING;

  static {
    RING = new BulletPattern();
    Effect ring = (bullets, familyPool, basePool) -> {
      for (BulletFamily b : bullets) {
        for (int i = 0; i < 10; i++) {
          float theta = MathUtils.PI / i;
          BulletFamily clone = b.mostlyClone(familyPool, basePool);
          float vx = b.getBase().getVX(), vy = b.getBase().getVY();
          float mag = (float) Math.sqrt(vx * vx + vy * vy);
          clone.getBase().setVX(mag * MathUtils.cos(theta));
          clone.getBase().setVY(mag * MathUtils.sin(theta));
        }
      }
    };
    BulletModel bullet = new BulletModel(3, 3, 0.5f);
    bullet.setVX(4 * 0);
    bullet.setVY(4 * 1);
    BulletFamily f1 = new BulletFamily(bullet, 1);
    f1.addEffect(ring);
    RING.addFamily(f1);
  }
}
