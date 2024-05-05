package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.Effect;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.bpedit.Spawner.BulletFamily;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;

import com.badlogic.gdx.utils.*;
import edu.cornell.jade.seasthethrone.model.Model;

import java.util.Random;

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
        float theta = j * centralAngle / (dups-1) + offset;
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

/**
 * Causes a boss model to play it's attack animation when this effect is applied
 */
class PlaysAttackAnimation implements Effect {

  /** model to play attack of */
  private final BossModel model;

  /**
   * Constructs a <code>PlaysAttackAnimation</code> {@link Effect}.
   *
   * @param model model to play attack animation for
   */
  public PlaysAttackAnimation(BossModel model) {
    this.model = model;
  }

  @Override
  public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
    model.bossAttack();
  }
}

/**
 * Causes all bullets to target the given model
 */
class TargetsModel implements Effect {

  /** model to target */
  private final Model model;

  /** spawner we are comming from */
  private final Spawner spawner;

  /**
   * constructs a targets model
   *
   * @param model model to target
   */
  public TargetsModel(Spawner spawner, Model model) {
    this.model = model;
    this.spawner = spawner;
  }

  @Override
  public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
    float dx = model.getX() - spawner.getX();
    float dy = model.getY() - spawner.getY();
    float theta = MathUtils.atan(dy / dx);
    int numBullets = bullets.size;
    for (int i = 0; i < numBullets; i++) {
      BulletFamily orig = bullets.get(i);
      orig.rotate(theta - (dx < 0 ? -1 : 1) * orig.getDir(), 0, 0);
    }
  }
}

/**
 * Causes all bullets randomly spray
 */
class RandomSpray implements Effect {

  /**
   * The random number generator
   */
  private final Random rand;

  /**
   * the full angle the spawner can spray in
   */
  private final float angleRange;

  /**
   * constructs a random spray
   *
   * @param angleRange the full angle the spawner can spray in (radians)
   */
  public RandomSpray(float angleRange) {
    this.angleRange = angleRange;
    this.rand = new Random();
  }

  @Override
  public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
    int numBullets = bullets.size;
    for (int i = 0; i < numBullets; i++) {
      BulletFamily orig = bullets.get(i);
      orig.rotate(rand.nextFloat(-angleRange/2, angleRange/2), 0, 0);
    }
  }
}


/**
 * Causes all bullets to be unbreakable
 */
class Unbreakable implements Effect {

  /**
   * constructs an unbreakable effect
   */
  public Unbreakable() {}

  @Override
  public void apply(Array<BulletFamily> bullets, Pool<BulletFamily> familyPool, Pool<BulletModel> basePool) {
    int numBullets = bullets.size;
    for (int i = 0; i < numBullets; i++) {
      BulletFamily orig = bullets.get(i);
      orig.setUnbreakable();
    }
  }
}
