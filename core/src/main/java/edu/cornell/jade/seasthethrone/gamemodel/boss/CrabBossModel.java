package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class CrabBossModel extends BossModel {

  /** {@link CrabBossModel} constructor using Builder */
  public CrabBossModel(Builder builder, int frameSize) {
    super(builder, "crab");
  }
}
