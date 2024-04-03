package edu.cornell.jade.seasthethrone.bpedit;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.BulletPattern.BulletFamily;
import edu.cornell.jade.seasthethrone.bpedit.BulletPattern.Effect;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;

/**
 * A temperary class to keep track of hard coded bullet patterns. This servers
 * as an example of
 * creating patterns and effects, but should not be used in production.
 */
public class BulletPatternConstants {
  /** A Spiral bullet pattern */
  public static BulletPattern RING;

  /** A stack of bullets with an underlying ring */
  public static BulletPattern STACK_RING;

  /** A spriral stack of bullets */
  public static BulletPattern SPIRAL;

  /** A ring of arcs */
  public static BulletPattern ARC_RING;

  /** A pretty ice ring */
  public static BulletPattern ICE;

  /** A pretty flower shower ring */
  public static BulletPattern FLOWER;

  static {
    class Periodic implements Effect {
      int ticks;

      public Periodic(int ticks) {
        this.ticks = ticks;
      }

      public void apply(
          Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
        int numBullets = bullets.size;
        for (int i = 0; i < numBullets; i++) {
          BulletFamily orig = bullets.get(i);
          BulletFamily b = orig.clone(orig.timestamp + ticks, familyPool);
          b.effect.addFirst(new Periodic(ticks));
          bullets.add(b);
        }
      }
    }

    class Ring implements Effect {
      private int dups;
      private float errorRange;
      private float randomOff;
      private int delay;
      private boolean aroundOrig;

      public Ring(int dups, float errorRange, float randomOff, int delay, boolean aroundOrig) {
        this.dups = dups;
        this.errorRange = errorRange;
        this.randomOff = randomOff;
        this.delay = delay;
        this.aroundOrig = aroundOrig;
      }

      public void apply(
          Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
        int numBullets = bullets.size;
        float off = MathUtils.random(0f, randomOff);
        for (int k = 0; k < numBullets; k++) {
          BulletFamily orig = bullets.get(k);
          for (int i = 0; i < dups - 1; i++) {
            float theta = off + MathUtils.random(-errorRange, errorRange) + (i + 1) * MathUtils.PI2 / dups;
            BulletFamily clone = orig.clone(orig.getTimestamp() + delay * i, familyPool);
            if (!aroundOrig) {
              clone.rotate(theta, 0, 0);
            } else {
              clone.rotate(theta, orig.bx, orig.by);
            }
            bullets.add(clone);
          }
          if (!aroundOrig) {
            float theta = off + MathUtils.random(-errorRange, errorRange);
            orig.rotate(theta, 0, 0);
          }
        }
      }
    }

    class Stack implements Effect {
      private int dups;
      private float velDiff;

      public Stack(int dups, float velDiff) {
        this.dups = dups;
        this.velDiff = velDiff;
      }

      public void apply(
          Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
        int numBullets = bullets.size;
        for (int i = 0; i < numBullets; i++) {
          BulletFamily orig = bullets.get(i);
          for (int j = 0; j < dups; j++) {
            BulletFamily clone = orig.clone(orig.getTimestamp(), familyPool);
            clone.bvx *= 1 + velDiff * j;
            clone.bvy *= 1 + velDiff * j;
            bullets.add(clone);
          }
        }
      }
    }

    class PeriodicRotate implements Effect {
      int ticks;
      float step;

      public PeriodicRotate(int ticks, float step) {
        this.ticks = ticks;
        this.step = step;
      }

      public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
        int numBullets = bullets.size;
        for (int i = 0; i < numBullets; i++) {
          BulletFamily orig = bullets.get(i);
          BulletFamily b = orig.clone(orig.timestamp + ticks, familyPool);
          b.rotate(step, 0, 0);
          b.effect.addFirst(new PeriodicRotate(ticks, step));
          bullets.add(b);
        }
      }
    }

    class Arc implements Effect {
      float offset;
      float centralAngle;
      int dups;

      public Arc(float offset, float centralAngle, int dups) {
        this.offset = offset;
        this.centralAngle = centralAngle;
        this.dups = dups;
      }

      public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
        int numBullets = bullets.size;
        for (int i = 0; i < numBullets; i++) {
          BulletFamily orig = bullets.get(i);
          for (int j = 0; j < dups; j++) {
            BulletFamily b = orig.clone(orig.timestamp, familyPool);
            float theta = j * centralAngle / (dups - 1) + offset;
            b.rotate(theta, 0, 0);
            bullets.add(b);
          }
        }
      }

    }

    RING = new BulletPattern();
    BulletFamily f1 = new BulletFamily(2f, 0f, 2f, 0f, 0.5f, 0);
    f1.addEffect(new PeriodicRotate(30, 0.5f));
    f1.addEffect(new Ring(10, 0.0f, 0.0f, 0, false));
    RING.addFamily(f1);

    STACK_RING = new BulletPattern();
    BulletFamily srf = new BulletFamily(2f, 0f, 10f, 0f, 0.5f, 0);
    srf.addEffect(new Periodic(60));
    srf.addEffect(new Ring(5, 0f, MathUtils.PI2, 0, false));
    srf.addEffect(new Stack(5, 0.2f));
    BulletFamily b = new BulletFamily(2f, 0, 10f, 0f, 0.5f, 30);
    b.addEffect(new PeriodicRotate(60, 0.5f));
    b.addEffect(new Ring(30, 0f, 0f, 0, false));
    STACK_RING.addFamily(srf);
    STACK_RING.addFamily(b);

    SPIRAL = new BulletPattern();
    BulletFamily c = new BulletFamily(15, 0, -5f, 0f, 0.5f, 0);
    c.addEffect(new Periodic(60));
    c.addEffect(new Ring(20, 0.0f, 0.0f, 5, false));
    c.addEffect(new Ring(5, 0.0f, 0.0f, 0, true));
    SPIRAL.addFamily(c);

    ARC_RING = new BulletPattern();
    BulletFamily d = new BulletFamily(5, 0, 4f, 0f, 0.5f, 0);
    d.addEffect(new Periodic(60));
    d.addEffect(new Ring(6, 0.3f, MathUtils.PI2, 0, false));
    d.addEffect(new Arc(0f, 0.4f, 5));
    ARC_RING.addFamily(d);

    ICE = new BulletPattern();
    BulletFamily e = new BulletFamily(15, 0, -4f, 0f, 0.5f, 0);
    e.addEffect(new Periodic(3));
    e.addEffect(new Ring(8, 0.0f, 0.0f, 0, true));
    e.addEffect(new Ring(7, 0.0f, 0.0f, 0, false));
    ICE.addFamily(e);

    FLOWER = new BulletPattern();
    BulletFamily f = new BulletFamily(15, 0, -2f, 0f, 0.5f, 0);
    f.addEffect(new Periodic(60));
    f.addEffect(new Ring(7, 0.0f, 0.2f, 5, true));
    f.addEffect(new Ring(5, 0.0f, 0.2f, 0, false));
    FLOWER.addFamily(f);
  }
}
