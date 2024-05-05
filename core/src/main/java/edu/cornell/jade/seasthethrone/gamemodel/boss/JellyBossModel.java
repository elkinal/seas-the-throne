package edu.cornell.jade.seasthethrone.gamemodel.boss;

public class JellyBossModel extends BossModel{

  /** {@link JellyBossModel} constructor using Builder */
  public JellyBossModel(Builder builder) {
    super(builder);
}


  @Override
  void setHitbox() {
    hitbox = new float[]{
            -1.7f,  1.2f,
            -0.6f,  2.2f,
            1,      2.2f,
            2.1f,   1.2f,
            2.4f,   -2.3f,
            -2,     -2.3f
    };
  }
}