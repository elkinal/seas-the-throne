package edu.cornell.jade.seasthethrone.gamemodel.boss;

public class SharkBossModel extends BossModel {

  /** {@link SharkBossModel} constructor using Builder */
  public SharkBossModel(BossModel.Builder builder) {
    super(builder);
  }

  @Override
  void setHitbox() {
    hitbox = new float[]{
            -2,     1.2f,
            -0.6f,  2.5f,
            1,      2.5f,
            2.4f,   1.2f,
            2.4f,   -2.5f,
            -2,     -2.5f
    };
  }
}

