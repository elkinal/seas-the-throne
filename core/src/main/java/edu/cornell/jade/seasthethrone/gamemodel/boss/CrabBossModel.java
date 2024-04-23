package edu.cornell.jade.seasthethrone.gamemodel.boss;

public class CrabBossModel extends BossModel {

  /** {@link CrabBossModel} constructor using Builder */
  public CrabBossModel(Builder builder) {
    super(builder);
  }

  @Override
  void setHitbox() {
    hitbox = new float[]{
            -6.5f,  7,
            -5,     8.5f,
            -1.5f,  4,
            -0.35f, 4.6f,
            0.7f,   4,
            1,      5.5f,
            4,      7,
            5.5f,   7,
            5.5f,   5,
            6,      4,
            5.5f,   -4,
            4.5f,   -3,
            4,      1.8f,
            1,      1.5f,
            3,      -3,
            2,      -8,
            -3,     -7,
            -4,     -2,
            -3,     0,
            -6,     4
    };
  }
}
