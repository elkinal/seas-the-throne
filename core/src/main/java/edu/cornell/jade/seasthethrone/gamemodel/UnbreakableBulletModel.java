package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class UnbreakableBulletModel extends BulletModel{

  /** Create an unbreakable bullet */
  public UnbreakableBulletModel(Builder builder) {
    super(builder);
    fishTexture = new Texture("bullet/urchinbullet.png");
    filmStrip = new FilmStrip(fishTexture, 1, 1);
    setMass(1000000f);
    setDensity(100000f);
  }


}
