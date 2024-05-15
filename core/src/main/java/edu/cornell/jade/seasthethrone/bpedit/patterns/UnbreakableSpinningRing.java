package edu.cornell.jade.seasthethrone.bpedit.patterns;

import com.badlogic.gdx.math.MathUtils;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.bpedit.Spawner;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.physics.PhysicsEngine;

/** A ring of spinning unbreakable bullets around an origin */
public final class UnbreakableSpinningRing extends AttackPattern {
  /*
   * -------------------------------
   * CONSTANTS RELATED TO THE ATTACK
   * -------------------------------
   */

  /** The angle of the arc of a shot */
  public static float CENTRAL_ANGLE = MathUtils.PI / 3;

  /** the spawner actually creating the arc */
  private final Spawner spawner;

  /** the model of the attacking boss */
  private final BossModel model;

  /**
   * Constructs the attack
   *
   * @param offset        the offset form the center of the boss
   * @param dups          the number of bullets in the ring
   * @param period        the length of full rotation of the bullet in ticks
   * @param model         boss shooting the bullet
   * @param builder       a builder to create bullet models
   * @param physicsEngine {@link PhysicsEngine} to add bullets to
   */
  public UnbreakableSpinningRing(float offset, int dups, int period, BossModel model, BulletModel.Builder builder,
                        PhysicsEngine physicsEngine) {
    this.spawner = SpawnerFactory.constructSpinningRing(offset, dups, period, model, builder, physicsEngine);
    this.model = model;

    this.spawner.translate(model.getX(), model.getY());
    this.spawner.setUnbreakable();
    this.addSpawner(spawner);
  }

  @Override
  protected void animateStep() {}
}

